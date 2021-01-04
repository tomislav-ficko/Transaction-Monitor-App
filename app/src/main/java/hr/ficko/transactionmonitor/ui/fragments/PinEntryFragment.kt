package hr.ficko.transactionmonitor.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hr.ficko.transactionmonitor.R
import hr.ficko.transactionmonitor.databinding.FragmentPinEntryBinding
import hr.ficko.transactionmonitor.viewModels.UserViewModel
import hr.ficko.transactionmonitor.viewModels.UserViewModel.PinError
import hr.ficko.transactionmonitor.viewModels.UserViewModel.PinValidationStatus.*
import timber.log.Timber

@AndroidEntryPoint
class PinEntryFragment : Fragment() {

    private val viewModel by viewModels<UserViewModel>()
    private lateinit var binding: FragmentPinEntryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPinEntryBinding.inflate(layoutInflater)

        defineButtonActions()
        defineObservers()
        addNameAndSurnameToTitleIfRegistered()

        return inflater.inflate(R.layout.fragment_pin_entry, container, false)
    }

    private fun addNameAndSurnameToTitleIfRegistered() =
        viewModel.getSavedNameAndSurname()

    private fun defineButtonActions() {
        //TODO check why button presses are not working
        binding.btnNext.setOnClickListener {
            Timber.d("Button pressed, validating entered PIN")
            viewModel.checkPinValidityAndNotifyFragment(binding.etPinEntry.text.toString())
        }

        binding.btnRegister.setOnClickListener {
            Timber.d("Button pressed, navigating to registration fragment")
            continueToNameRegistration()
        }
    }

    private fun defineObservers() {
        viewModel.pinValidationLiveData.observe(viewLifecycleOwner, validationObserver())
        viewModel.displayNameLiveData.observe(viewLifecycleOwner, displayNameObserver())
    }

    @SuppressLint("SetTextI18n")
    private fun displayNameObserver() = Observer<String> { nameAndSurname ->
        binding.tvTitle.text = "Welcome $nameAndSurname"
    }

    private fun validationObserver() = Observer<PinError> { error ->
        error?.let {
            when (it.reason) {
                INVALID -> showErrorMessage("PIN invalid")
                INCORRECT_LENGTH -> showErrorMessage("Incorrect PIN length")
                NOT_REGISTERED -> showErrorMessage("Please register first")
                else -> continueToMainActivity()
            }
        }
    }

    private fun continueToNameRegistration() =
        findNavController().navigate(R.id.action_loginFragment_to_nameRegistrationFragment)

    private fun continueToMainActivity() =
        findNavController().navigate(R.id.action_pinRegistrationFragment_to_mainActivity)

    private fun showErrorMessage(content: String) = Toast.makeText(
        context,
        content,
        Toast.LENGTH_LONG
    ).show()
}
