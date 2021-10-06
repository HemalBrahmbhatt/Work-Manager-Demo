package com.example.work_manager_demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.example.work_manager_demo.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.upload)

        binding.apply {
            progressBar.hide()

            btnStart.setOnClickListener {
                setOneTimeWork()
                //setPeriodicWork()
            }

            srl.setOnRefreshListener {
                srl.isRefreshing = true
                lifecycleScope.launch {
                    delay(1000L)
                    progressBar.hide()
                    txtNum.text = null
                    txtStatus.text = null
                    srl.isRefreshing = false
                }
            }
        }
    }


    private fun setOneTimeWork() {
        binding.btnStart.isVisible = false
        val workManager = WorkManager.getInstance(applicationContext)

        val constraints =
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val uploadWork = OneTimeWorkRequest.Builder(UploadWorker::class.java)
            .setConstraints(constraints)
            .build()

        val filterWork = OneTimeWorkRequest.Builder(FilterWorker::class.java)
            .build()

        val downloadWork = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
            .build()

        val compressWork = OneTimeWorkRequest.Builder(CompressWorker::class.java)
            .build()

        val parallelWork = mutableListOf<OneTimeWorkRequest>()
        parallelWork.add(filterWork)
        parallelWork.add(downloadWork)

        workManager
            .beginWith(parallelWork)
            .then(compressWork)
            .then(uploadWork)
            .enqueue()

        workManager.getWorkInfoByIdLiveData(uploadWork.id).observe(this, {
            binding.apply {
                when {
                    it.state.isFinished -> {
                        progressBar.isIndeterminate = false
                        progressBar.show()
                        txtNum.text = getString(R.string.num_100)
                        progressBar.progress = 100
                        txtStatus.text = it.state.name
                        btnStart.isVisible = true
                    }
                    it.state == WorkInfo.State.BLOCKED -> {
                        progressBar.hide()
                        progressBar.isIndeterminate = true
                        progressBar.show()
                        txtNum.text = null
                        txtStatus.text = it.state.name
                    }
                    it.state == WorkInfo.State.ENQUEUED -> {
                        progressBar.hide()
                        progressBar.isIndeterminate = true
                        progressBar.show()
                        txtNum.text = null
                        txtStatus.text = it.state.name
                    }
                    else -> {
                        progressBar.isIndeterminate = false
                        progressBar.show()
                        txtStatus.text = it.state.name
                        txtNum.text =
                            it.progress.getInt(getString(R.string.progress), 0).toString().plus("%")
                        progressBar.progress = it.progress.getInt(getString(R.string.progress), -1)
                    }
                }
            }
        })
    }

    /*private fun setPeriodicWork(){
        val periodicWork  = PeriodicWorkRequest.Builder(DownloadWorker::class.java,16,TimeUnit.MINUTES).build()
        WorkManager
            .getInstance(applicationContext)
            .enqueue(periodicWork)
    }*/

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}