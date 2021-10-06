package com.example.work_manager_demo

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

class UploadWorker(context: Context, params: WorkerParameters): CoroutineWorker(context,params) {
    override suspend fun doWork(): Result {
        return try {
            var i = 0
            val data = Data.Builder()
            while (i <= 100){
                delay(50L)
                setProgress(workDataOf(this.applicationContext.getString(R.string.progress) to i))
                data.putAll(workDataOf(this.applicationContext.getString(R.string.number) to i))
                i += 1
            }

            Result.success(data.build())
        }catch (e:Exception){
            Result.failure()
        }
    }
}