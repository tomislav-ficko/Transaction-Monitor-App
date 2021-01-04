package hr.ficko.transactionmonitor.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hr.ficko.transactionmonitor.R
import hr.ficko.transactionmonitor.databinding.FragmentPinEntryBinding
import hr.ficko.transactionmonitor.ui.activities.LoginActivity
import hr.ficko.transactionmonitor.ui.activities.RegistrationActivity
import hr.ficko.transactionmonitor.viewModels.UserViewModel
import timber.log.Timber

@AndroidEntryPoint
class PinEntryFragment : Fragment() {

    private val viewModel by viewModels<UserViewModel>()
    private lateinit var binding: FragmentPinEntryBinding

    companion object {
        fun newInstance(): PinEntryFragment {
            return PinEntryFragment()
        }
    }

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

    private fun addNameAndSurnameToTitleIfRegistered() {
        viewModel.getSavedNameAndSurname()
    }

    private fun defineButtonActions() {
        //TODO buttons presses not working for some reason
        binding.btnNext.setOnClickListener {
            Timber.d("Button pressed, validating entered PIN")
            viewModel.checkPinValidityAndSave(binding.etPinEntry.text.toString())
        }

        binding.btnRegister.setOnClickListener {
            Timber.d("Button pressed, navigating to registration fragment")
            findNavController().navigate(R.id.action_loginFragment_to_nameRegistrationFragment)
        }
    }

    private fun defineObservers() {
        viewModel.pinValidationLiveData.observe(viewLifecycleOwner, validationObserver())
        viewModel.displayNameLiveData.observe(viewLifecycleOwner, displayNameObserver())
    }

    private fun displayNameObserver() = Observer<String> { nameAndSurname ->
        binding.tvTitle.text = "Welcome $nameAndSurname"
    }

    private fun validationObserver() = Observer<UserViewModel.PinError> { error ->
        error?.let {
            if (errorNotPresent(it)) {
                continueToMainActivity()
            } else {
                //TODO handle errors
            }
        }
    }

    private fun continueToMainActivity() {
        findNavController().navigate(R.id.action_pinRegistrationFragment_to_mainActivity)
    }

    private fun errorNotPresent(error: UserViewModel.PinError) = !error.occurred

    fun changeTitle(newTitle: String) {
        binding.tvTitle.text = newTitle
    }
}
