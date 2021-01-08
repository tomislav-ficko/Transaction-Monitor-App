package hr.ficko.transactionmonitor.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import hr.ficko.transactionmonitor.R
import hr.ficko.transactionmonitor.databinding.FragmentMainBinding
import hr.ficko.transactionmonitor.models.Account
import hr.ficko.transactionmonitor.models.Transaction
import hr.ficko.transactionmonitor.ui.SpinnerAdapter
import hr.ficko.transactionmonitor.ui.TransactionListAdapter
import hr.ficko.transactionmonitor.viewModels.RepositoryViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(), AdapterView.OnItemSelectedListener {

    @Inject
    lateinit var listAdapter: TransactionListAdapter

    private val viewModel by viewModels<RepositoryViewModel>()
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(layoutInflater)

        defineButtonAction()
        initializeRecyclerView()
        initializeFragment()
        observeLiveData(viewLifecycleOwner)
        loadUserDataAndPopulateSpinner()

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private fun defineButtonAction() =
        binding.btnLogout.setOnClickListener {
            continueToLoginFragment()
        }

    private fun initializeRecyclerView() =
        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

    private fun observeLiveData(viewLifecycleOwner: LifecycleOwner) {
        viewModel.transactionsLiveData.observe(viewLifecycleOwner, transactionObserver())
        viewModel.accountsLiveData.observe(viewLifecycleOwner, accountObserver())
        viewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver())
    }

    private fun initializeFragment() {
        val adapter = createNewSpinnerAdapter(listOf("Select IBAN"))

        binding.apply {
            spnAccountDropdown.adapter = adapter
            spnAccountDropdown.setSelection(0)
            spnAccountDropdown.onItemSelectedListener = this@MainFragment
            tvStatusValue.text = getString(R.string.value_unknown_account_status)
        }
    }

    private fun loadUserDataAndPopulateSpinner() {
        viewModel.loadUserDataAndDeliverAccountData()
    }

    private fun getTransactionData(id: Int) {
        changeProgressLoaderVisibility()
        viewModel.getTransactionsForAccount(id)
    }

    private fun accountObserver() = Observer<List<Account>> { accountData ->
        accountData?.let {
            val newIbanList = mutableListOf<String>().apply {
                add("Select IBAN") // This will be the spinner hint, must be the first element in the list
                addAll(it.map { account -> account.iban })
            }
            updateSpinnerAccountData(newIbanList)
        }
    }

    private fun updateSpinnerAccountData(newIbanList: MutableList<String>) {
        binding.spnAccountDropdown.adapter = createNewSpinnerAdapter(newIbanList)
    }

    private fun createNewSpinnerAdapter(data: List<String>): SpinnerAdapter? {
        return activity?.baseContext?.let { baseContext ->
            SpinnerAdapter(
                baseContext,
                R.layout.support_simple_spinner_dropdown_item,
                data
            )
        }
    }

    private fun transactionObserver() = Observer<List<Transaction>> { data ->
        data?.let {
            changeProgressLoaderVisibility()
            showData(it)
        }
    }

    private fun errorObserver() = Observer<Boolean> { errorOccurred ->
        if (errorOccurred) {
            Toast.makeText(
                context,
                "Network problem occurred, data could not be retrieved.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showData(data: List<Transaction>) =
        listAdapter.apply {
            dataset = data
            notifyDataSetChanged()
        }

    private fun changeProgressLoaderVisibility() =
        binding.apply {
            if (progressLoader.isVisible) {
                progressLoader.visibility = ProgressBar.INVISIBLE
            } else {
                progressLoader.visibility = ProgressBar.VISIBLE
            }
        }

    private fun continueToLoginFragment() =
        findNavController().navigate(R.id.action_mainFragment_to_loginFragment)

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        // Skip the first item (hint)
        if (position > 0) {
            getTransactionData(position - 1)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // stub
    }
}

