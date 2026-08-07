package com.myapplication.panthraa.widget

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

object PanthraaScheduleWidgetUpdater {
    private const val UNIQUE_REFRESH_WORK = "panthraa_schedule_widget_refresh"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun refreshAndSchedule(context: Context) {
        val appContext = context.applicationContext
        scope.launch {
            runCatching { PanthraaScheduleWidget.updateAll(appContext) }
        }
        scheduleNext(appContext)
    }

    fun scheduleNext(context: Context) {
        val appContext = context.applicationContext
        val delayMillis = ScheduleWidgetRepository.nextRefreshDelayMillis(appContext)
        val workManager = WorkManager.getInstance(appContext)
        if (delayMillis == null) {
            workManager.cancelUniqueWork(UNIQUE_REFRESH_WORK)
            return
        }

        val request = OneTimeWorkRequestBuilder<PanthraaScheduleWidgetRefreshWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .build()
        workManager.enqueueUniqueWork(
            UNIQUE_REFRESH_WORK,
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context.applicationContext).cancelUniqueWork(UNIQUE_REFRESH_WORK)
    }
}

class PanthraaScheduleWidgetRefreshWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return runCatching {
            PanthraaScheduleWidget.updateAll(applicationContext)
            PanthraaScheduleWidgetUpdater.scheduleNext(applicationContext)
            Result.success()
        }.getOrElse {
            Result.retry()
        }
    }
}
