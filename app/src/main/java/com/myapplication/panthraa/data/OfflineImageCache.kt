package com.myapplication.panthraa.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import java.security.MessageDigest

object OfflineImageCache {
    suspend fun loadBitmap(
        context: Context,
        imageUrl: String?,
        imageUri: Uri? = null,
    ): Bitmap? = withContext(Dispatchers.IO) {
        when {
            imageUri != null -> context.contentResolver.openInputStream(imageUri)
                ?.use { BitmapFactory.decodeStream(it) }
            imageUrl.isNullOrBlank() -> null
            else -> loadRemoteOrCached(context, imageUrl)
        }
    }

    suspend fun cacheUrls(context: Context, urls: Iterable<String?>) = withContext(Dispatchers.IO) {
        urls
            .filterNot { it.isNullOrBlank() }
            .map { it!!.trim() }
            .distinct()
            .forEach { url ->
                runCatching { ensureCached(context, url) }
                    .onFailure { Log.w(TAG, "Failed to cache image: $url", it) }
            }
    }

    private fun loadRemoteOrCached(context: Context, imageUrl: String): Bitmap? {
        readCachedBitmap(context, imageUrl)?.let { return it }
        return runCatching {
            ensureCached(context, imageUrl)
            readCachedBitmap(context, imageUrl)
        }.onFailure {
            Log.w(TAG, "Failed to load image, using placeholder: $imageUrl", it)
        }.getOrNull()
    }

    private fun ensureCached(context: Context, imageUrl: String) {
        val file = cacheFile(context, imageUrl)
        if (file.exists() && file.length() > 0L) return

        file.parentFile?.mkdirs()
        val tempFile = File(file.parentFile, "${file.name}.tmp")
        val connection = URL(imageUrl).openConnection().apply {
            connectTimeout = 8_000
            readTimeout = 12_000
        }

        connection.getInputStream().use { input ->
            tempFile.outputStream().use { output -> input.copyTo(output) }
        }
        if (tempFile.length() > 0L) {
            if (file.exists()) file.delete()
            tempFile.renameTo(file)
        } else {
            tempFile.delete()
        }
    }

    private fun readCachedBitmap(context: Context, imageUrl: String): Bitmap? {
        val file = cacheFile(context, imageUrl)
        if (!file.exists() || file.length() <= 0L) return null
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    private fun cacheFile(context: Context, imageUrl: String): File {
        return File(cacheDirectory(context), "${sha256(imageUrl)}.img")
    }

    private fun cacheDirectory(context: Context): File {
        return File(context.applicationContext.filesDir, IMAGE_CACHE_DIR)
    }

    private fun sha256(value: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(value.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private const val TAG = "OfflineImageCache"
    private const val IMAGE_CACHE_DIR = "offline_review_images"
}

