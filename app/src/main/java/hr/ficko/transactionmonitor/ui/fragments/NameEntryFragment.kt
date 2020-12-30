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
import hr.ficko.transactionmonitor.databinding.FragmentNameEntryBinding
import hr.ficko.transactionmonitor.ui.activities.RegistrationActivity
import hr.ficko.transactionmonitor.viewModels.UserViewModel
import timber.log.Timber

@AndroidEntryPoint
class NameEntryFragment : Fragment() {

    private val viewModel by viewModels<UserViewModel>()
    private lateinit var binding: FragmentNameEntryBinding

    companion object {
        fun newInstance(): NameEntryFragment {
            return NameEntryFragment()
        }
    }

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
                viewModel.checkNameValidityAndSave(
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
            if (errorNotPresent(it)) {
                notifyActivityAboutSuccessfulAction()
            } else {
                //TODO handle errors
            }
        }
    }

    private fun notifyActivityAboutSuccessfulAction() {
        (activity as RegistrationActivity).nameEntrySuccessful()
    }

    private fun errorNotPresent(error: UserViewModel.NameError) = !error.occurred
}
