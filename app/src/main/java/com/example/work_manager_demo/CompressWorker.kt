package com.example.work_manager_demo

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import java.lang.Exception

class CompressWorker(context: Context, params:WorkerParameters):CoroutineWorker(context,params) {
    override suspend fun doWork(): Result {
        return try {
            var i = 0
            while (i<=50){
                delay(50L)
                Log.d("MyWork", "Compress: $i")
                i += 1
            }
            Result.success()
        }catch (e:Exception){
            Result.failure()
        }
    }
}