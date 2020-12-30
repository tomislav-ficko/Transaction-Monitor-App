package hr.ficko.transactionmonitor.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import hr.ficko.transactionmonitor.ui.activities.MainActivity
import timber.log.Timber

open class BaseActivity : AppCompatActivity() {

    fun navigateToMainActivity(context: Context) {
        Timber.d("Navigating to MainActivity")
        ContextCompat.startActivity(
            this,
            Intent(context, MainActivity::class.java),
            null
        )
    }
}