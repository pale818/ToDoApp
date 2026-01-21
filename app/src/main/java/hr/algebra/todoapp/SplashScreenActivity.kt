package hr.algebra.todoapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
//import hr.algebra.todoapp.api.SyncTasksWorker
import hr.algebra.todoapp.databinding.ActivitySplashScreenBinding
import hr.algebra.todoapp.framework.amOnline
import hr.algebra.todoapp.framework.applyAnimation
import hr.algebra.todoapp.framework.callDelayed
import hr.algebra.todoapp.framework.getBooleanPreference

private const val DELAY = 3000L

private const val UNIQUE_SYNC_WORK = "sync_tasks_work"
const val DATA_IMPORTED = "hr.algebra.todoapp.data_imported"
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAnimations()
        //redirect()


    }

    private fun startAnimations() {

        binding.tvSplash.applyAnimation(R.anim.blink)
        binding.ivSplash.applyAnimation(R.anim.rotate)
        callDelayed(DELAY) { startActivity(Intent(this, HostActivity::class.java)) }

    }

   /* private fun redirect() {

        if(getBooleanPreference(DATA_IMPORTED)) {
            callDelayed(DELAY) { startActivity(Intent(this, HostActivity::class.java)) }
        } else {
            if(amOnline()) {
                // FG
                WorkManager.getInstance(this).apply {
                    enqueueUniqueWork(
                        UNIQUE_SYNC_WORK,
                        ExistingWorkPolicy.KEEP,
                        OneTimeWorkRequest.from(SyncTasksWorker::class.java)
                    )

                }
            } else {
                binding.tvSplash.text = getString(R.string.no_internet)
                callDelayed(DELAY) { finish() }
            }
        }
    }*/
}

