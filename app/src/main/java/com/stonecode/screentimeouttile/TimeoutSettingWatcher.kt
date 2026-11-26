package com.stonecode.screentimeouttile

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.provider.Settings

/**
 * Wakes the app only when Android reports a change to the screen timeout
 * setting, so a placed widget stays correct after changes made in system
 * Settings or by another app. No process stays running between changes.
 * Content-trigger jobs fire once, so each run schedules the next one.
 */
class TimeoutSettingWatcher : JobService() {

    override fun onStartJob(params: JobParameters): Boolean {
        TimeoutWidgets.updateAll(this)
        if (TimeoutWidgets.hasWidgets(this)) schedule(this)
        return false
    }

    override fun onStopJob(params: JobParameters): Boolean = false

    companion object {
        private const val JOB_ID = 4_201

        fun schedule(context: Context) {
            val scheduler = context.getSystemService(JobScheduler::class.java) ?: return
            val uri = Settings.System.getUriFor(Settings.System.SCREEN_OFF_TIMEOUT)
            val job = JobInfo.Builder(JOB_ID, ComponentName(context, TimeoutSettingWatcher::class.java))
                .addTriggerContentUri(JobInfo.TriggerContentUri(uri, 0))
                .setTriggerContentUpdateDelay(0)
                .setTriggerContentMaxDelay(1_000)
                .build()
            runCatching { scheduler.schedule(job) }
        }

        fun cancel(context: Context) {
            context.getSystemService(JobScheduler::class.java)?.cancel(JOB_ID)
        }
    }
}
