package hr.ficko.transactionmonitor.ui

import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import hr.ficko.transactionmonitor.databinding.ActivityRegistrationBinding
import hr.ficko.transactionmonitor.ui.fragments.NameEntryFragment
import hr.ficko.transactionmonitor.ui.fragments.PinEntryFragment
import hr.ficko.transactionmonitor.viewModels.UserViewModel

@AndroidEntryPoint
class RegistrationActivity : BaseActivity() {

    private val viewModel by viewModels<UserViewModel>()
    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateLayout()
        displayRegistrationFragment()
    }

    fun nameEntrySuccessful() {
        displayPinEntryFragment()
    }

    fun registrationSuccessful() {
        navigateToMainActivity(this)
    }

    private fun inflateLayout() {
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun displayRegistrationFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(
                binding.registrationContainer.id,
                NameEntryFragment.newInstance(),
                "nameEntryFragment"
            )
            .commit()
    }

    private fun displayPinEntryFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(
                binding.registrationContainer.id,
                PinEntryFragment.newInstance(),
                "pinEntryFragment"
            )
            .commit()
    }
}
