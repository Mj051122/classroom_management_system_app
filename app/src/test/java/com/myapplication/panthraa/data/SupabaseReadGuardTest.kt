package com.myapplication.panthraa.data

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class SupabaseReadGuardTest {
    @Test
    fun freshTtlCacheHitReusesResponse() = runBlocking {
        var now = 0L
        var calls = 0
        val guard = SupabaseReadGuard(nowMillis = { now })

        val first = guard.read("classes:user=1", setOf("classes:user=1"), 1_000) {
            calls++
            "first"
        }
        now = 500L
        val second = guard.read("classes:user=1", setOf("classes:user=1"), 1_000) {
            calls++
            "second"
        }
        now = 1_001L
        val third = guard.read("classes:user=1", setOf("classes:user=1"), 1_000) {
            calls++
            "third"
        }

        assertEquals("first", first)
        assertEquals("first", second)
        assertEquals("third", third)
        assertEquals(2, calls)
    }

    @Test
    fun forceRefreshBypassesFreshCache() = runBlocking {
        var calls = 0
        val guard = SupabaseReadGuard(nowMillis = { 0L })

        guard.read("assignments:class=1", setOf("assignments:class=1"), 60_000) {
            calls++
            "cached"
        }
        val refreshed = guard.read(
            key = "assignments:class=1",
            groups = setOf("assignments:class=1"),
            ttlMillis = 60_000,
            policy = CachePolicy.FORCE_REFRESH,
        ) {
            calls++
            "fresh"
        }

        assertEquals("fresh", refreshed)
        assertEquals(2, calls)
    }

    @Test
    fun identicalRequestsAreCoalesced() = runBlocking {
        val guard = SupabaseReadGuard(nowMillis = { 0L })
        val started = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        var calls = 0

        val results = coroutineScope {
            val first = async {
                guard.read("status:class=1", setOf("status:class=1"), 30_000) {
                    calls++
                    started.complete(Unit)
                    release.await()
                    "status"
                }
            }
            started.await()
            val second = async {
                guard.read("status:class=1", setOf("status:class=1"), 30_000) {
                    calls++
                    "duplicate"
                }
            }
            release.complete(Unit)
            awaitAll(first, second)
        }

        assertEquals(listOf("status", "status"), results)
        assertEquals(1, calls)
    }

    @Test
    fun onlyFourNetworkReadsRunAtOnce() = runBlocking {
        val guard = SupabaseReadGuard(maxConcurrentReads = 4, nowMillis = { 0L })
        val active = AtomicInteger(0)
        val maxActive = AtomicInteger(0)

        coroutineScope {
            (0 until 12).map { index ->
                async {
                    guard.read("key=$index", setOf("group=$index"), 1_000) {
                        val activeNow = active.incrementAndGet()
                        maxActive.updateAndGet { current -> maxOf(current, activeNow) }
                        delay(25)
                        active.decrementAndGet()
                        index
                    }
                }
            }.awaitAll()
        }

        assertTrue(maxActive.get() <= 4)
    }

    @Test
    fun lruCacheIsBounded() = runBlocking {
        val guard = SupabaseReadGuard(maxEntries = 150, nowMillis = { 0L })

        repeat(151) { index ->
            guard.read("key=$index", setOf("group=$index"), 60_000) {
                index
            }
        }

        assertEquals(150, guard.cachedEntryCount())
    }

    @Test
    fun invalidatedGenerationRejectsOldResponse() = runBlocking {
        val guard = SupabaseReadGuard(nowMillis = { 0L })
        val started = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()

        supervisorScope {
            val oldRead = async {
                guard.read("announcements:user=1", setOf("announcements:user=1"), 60_000) {
                    started.complete(Unit)
                    release.await()
                    "old"
                }
            }

            started.await()
            guard.invalidateGroup("announcements:user=1")
            release.complete(Unit)

            try {
                oldRead.await()
                fail("Expected stale read to be rejected")
            } catch (_: StaleReadResultException) {
                assertEquals(0, guard.cachedEntryCount())
            }
        }
    }
}
