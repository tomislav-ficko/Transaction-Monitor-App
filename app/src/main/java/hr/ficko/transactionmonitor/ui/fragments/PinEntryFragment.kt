package hr.ficko.transactionmonitor.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import hr.ficko.transactionmonitor.R
import hr.ficko.transactionmonitor.databinding.FragmentPinEntryBinding
import hr.ficko.transactionmonitor.ui.LoginActivity
import hr.ficko.transactionmonitor.ui.RegistrationActivity
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

        return inflater.inflate(R.layout.fragment_pin_entry, container, false)
    }

    private fun defineButtonActions() {
        binding.apply {
            btnNext.setOnClickListener {
                Timber.d("Button pressed, validating entered PIN")
                viewModel.checkPinValidityAndSave(etPinEntry.text.toString())
            }

            btnRegister.setOnClickListener {
                Timber.d("Button pressed, navigating to registration fragment")
            }
        }
    }

    private fun defineObservers() {
        viewModel.pinValidationLiveData.observe(viewLifecycleOwner, validationObserver())
    }

    private fun validationObserver() = Observer<UserViewModel.PinError> { error ->
        error?.let {
            if (errorNotPresent(it)) {
                notifyActivityAboutSuccessfulAction()
            } else {
                //TODO handle errors
            }
        }
    }

    private fun notifyActivityAboutSuccessfulAction() {
        if (activity is LoginActivity) {
            (activity as LoginActivity).loginSuccessful()
        } else if (activity is RegistrationActivity) {
            (activity as RegistrationActivity).registrationSuccessful()
        }
    }

    private fun errorNotPresent(error: UserViewModel.PinError) = !error.occurred

    fun changeTitle(newTitle: String) {
        binding.tvTitle.text = newTitle
    }
}
