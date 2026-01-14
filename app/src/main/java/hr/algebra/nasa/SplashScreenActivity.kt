package hr.algebra.nasa

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import hr.algebra.nasa.api.NasaWorker
import hr.algebra.nasa.databinding.ActivitySplashScreenBinding
import hr.algebra.nasa.framework.amOnline
import hr.algebra.nasa.framework.applyAnimation
import hr.algebra.nasa.framework.callDelayed
import hr.algebra.nasa.framework.getBooleanPreference

private const val DELAY = 3000L
const val DATA_IMPORTED = "hr.algebra.nasa.data_imported"
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAnimations()
        redirect()

    }

    private fun startAnimations() {

        binding.tvSplash.applyAnimation(R.anim.blink)
        binding.ivSplash.applyAnimation(R.anim.rotate)

    }

    private fun redirect() {

        if(getBooleanPreference(DATA_IMPORTED)) {
            callDelayed(DELAY) { startActivity(Intent(this, HostActivity::class.java)) }
        } else {
            if(amOnline()) {
                // FG
                WorkManager.getInstance(this).apply {
                    enqueueUniqueWork(
                        DATA_IMPORTED,
                        ExistingWorkPolicy.KEEP,
                        OneTimeWorkRequest.from(NasaWorker::class.java)
                    )
                }
            } else {
                binding.tvSplash.text = getString(R.string.no_internet)
                callDelayed(DELAY) { finish() }
            }
        }
    }
}

