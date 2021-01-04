package hr.ficko.transactionmonitor.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hr.ficko.transactionmonitor.R
import hr.ficko.transactionmonitor.databinding.ActivityLoginBinding

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateLayout()
    }

    private fun inflateLayout() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.navHostFragment).navigateUp() // binding.navHostFragment.id is not available
}
