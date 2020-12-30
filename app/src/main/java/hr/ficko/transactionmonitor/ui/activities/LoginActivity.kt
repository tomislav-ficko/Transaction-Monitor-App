package hr.ficko.transactionmonitor.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import hr.ficko.transactionmonitor.databinding.ActivityLoginBinding
import hr.ficko.transactionmonitor.ui.BaseActivity
import hr.ficko.transactionmonitor.ui.fragments.PinEntryFragment
import hr.ficko.transactionmonitor.viewModels.UserViewModel

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private val viewModel by viewModels<UserViewModel>()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateLayout()
        displayLoginFragment()
    }

    fun loginSuccessful() {
        navigateToMainActivity(this)
    }

    private fun inflateLayout() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun displayLoginFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(
                binding.loginContainer.id,
                PinEntryFragment.newInstance(),
                "pinEntryFragment"
            )
            .commit()
    }
}
