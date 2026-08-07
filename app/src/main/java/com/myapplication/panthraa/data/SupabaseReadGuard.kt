package com.myapplication.panthraa.data

import android.os.SystemClock
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.util.LinkedHashMap

enum class CachePolicy {
    USE_FRESH,
    FORCE_REFRESH,
}

class StaleReadResultException(key: String) : IllegalStateException("Ignored stale read result for $key.")

class SupabaseReadGuard(
    private val maxEntries: Int = 150,
    maxConcurrentReads: Int = 4,
    private val nowMillis: () -> Long = { SystemClock.elapsedRealtime() },
) {
    private val networkSemaphore = Semaphore(maxConcurrentReads)
    private val lock = Any()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val cache = object : LinkedHashMap<String, CacheEntry>(maxEntries, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, CacheEntry>?): Boolean {
            return size > maxEntries
        }
    }
    private val inFlight = mutableMapOf<String, Deferred<Any>>()
    private val generations = mutableMapOf<String, Long>()

    suspend fun <T : Any> read(
        key: String,
        groups: Set<String>,
        ttlMillis: Long,
        policy: CachePolicy = CachePolicy.USE_FRESH,
        block: suspend () -> T,
    ): T {
        val now = nowMillis()
        var cachedValue: Any? = null
        var sharedRequest: Deferred<Any>? = null
        var shouldReturnCached = false

        synchronized(lock) {
            if (policy == CachePolicy.USE_FRESH) {
                val entry = cache[key]
                if (entry != null && entry.expiresAtMillis > now) {
                    cachedValue = entry.value
                    shouldReturnCached = true
                    return@synchronized
                }
            }

            val existing = inFlight[key]
            if (existing != null) {
                sharedRequest = existing
                return@synchronized
            }

            val capturedGenerations = groups.associateWith { generations[it] ?: 0L }
            lateinit var created: Deferred<Any>
            created = scope.async(start = CoroutineStart.LAZY) {
                try {
                    val result = networkSemaphore.withPermit { block() }
                    val canCache = synchronized(lock) {
                        capturedGenerations.all { (group, generation) ->
                            (generations[group] ?: 0L) == generation
                        }
                    }
                    if (!canCache) {
                        throw StaleReadResultException(key)
                    }
                    synchronized(lock) {
                        cache[key] = CacheEntry(
                            value = result,
                            expiresAtMillis = nowMillis() + ttlMillis,
                            groups = groups,
                        )
                    }
                    result
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } finally {
                    synchronized(lock) {
                        if (inFlight[key] === created) {
                            inFlight.remove(key)
                        }
                    }
                }
            }
            inFlight[key] = created
            sharedRequest = created
            created.start()
        }

        if (shouldReturnCached) {
            @Suppress("UNCHECKED_CAST")
            return cachedValue as T
        }

        @Suppress("UNCHECKED_CAST")
        return sharedRequest!!.await() as T
    }

    fun invalidateGroup(group: String) {
        synchronized(lock) {
            generations[group] = (generations[group] ?: 0L) + 1L
            val iterator = cache.entries.iterator()
            while (iterator.hasNext()) {
                if (group in iterator.next().value.groups) {
                    iterator.remove()
                }
            }
        }
    }

    fun clear() {
        synchronized(lock) {
            inFlight.values.forEach { it.cancel() }
            inFlight.clear()
            cache.clear()
            generations.clear()
        }
    }

    fun cachedEntryCount(): Int = synchronized(lock) { cache.size }

    companion object {
        val shared = SupabaseReadGuard()
    }
}

private data class CacheEntry(
    val value: Any,
    val expiresAtMillis: Long,
    val groups: Set<String>,
)
