package hr.ficko.transactionmonitor.ui.fragments

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
import hr.ficko.transactionmonitor.databinding.FragmentNameEntryBinding
import hr.ficko.transactionmonitor.viewModels.UserViewModel
import hr.ficko.transactionmonitor.viewModels.UserViewModel.NameValidationStatus.*
import timber.log.Timber

@AndroidEntryPoint
class NameEntryFragment : Fragment() {

    private val viewModel by viewModels<UserViewModel>()
    private lateinit var binding: FragmentNameEntryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNameEntryBinding.inflate(layoutInflater)

        defineButtonActions()
        defineObserver()

        return inflater.inflate(R.layout.fragment_name_entry, container, false)
    }

    private fun defineButtonActions() {
        binding.apply {
            btnRegistration.setOnClickListener {
                Timber.d("Button pressed, validating entered name and surname")
                viewModel.checkNameValidityAndNotifyFragment(
                    etName.text.toString(),
                    etSurname.text.toString()
                )
            }
        }
    }

    private fun defineObserver() {
        viewModel.nameValidationLiveData.observe(viewLifecycleOwner, validationObserver())
    }

    private fun validationObserver() = Observer<UserViewModel.NameError> { error ->
        error?.let {
            when (it.reason) {
                NAME_EMPTY -> showErrorMessage("Name empty")
                NAME_TOO_LONG -> showErrorMessage("Name must be shorter than 30 chars")
                NAME_FORBIDDEN_CHAR -> showErrorMessage("Name can only contain alphanum chars")
                SURNAME_EMPTY -> showErrorMessage("Surname empty")
                SURNAME_TOO_LONG -> showErrorMessage("Surname must be shorter than 30 chars")
                SURNAME_FORBIDDEN_CHAR -> showErrorMessage("Surname can only contain alphanum chars")
                else -> continueToPinRegistration()
            }
        }
    }

    private fun continueToPinRegistration() =
        findNavController().navigate(R.id.action_nameRegistrationFragment_to_pinRegistrationFragment)

    private fun showErrorMessage(content: String) = Toast.makeText(
        context,
        content,
        Toast.LENGTH_LONG
    ).show()
}
