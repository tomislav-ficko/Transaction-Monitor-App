package hr.ficko.transactionmonitor.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
    lateinit var mainContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(layoutInflater)

        defineButtonAction()
        initializeRecyclerView()
        initializeFragment()
        observeLiveData()

//        loadUserDataAndPopulateSpinner()
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

    private fun observeLiveData() {
        viewModel.transactionsLiveData.observe(context as AppCompatActivity, transactionObserver())
        viewModel.accountsLiveData.observe(context as AppCompatActivity, accountObserver())
        viewModel.errorLiveData.observe(context as AppCompatActivity, errorObserver())
    }

    // TODO throws error: cannot cast FragmentContextWrapper to AppCompatActivity
    private fun initializeFragment() {
        val adapter =
            SpinnerAdapter(
                mainContext,
                R.layout.support_simple_spinner_dropdown_item,
                listOf("Select IBAN")
            )

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
        var adapter =
            SpinnerAdapter(
                context as AppCompatActivity,
                R.layout.support_simple_spinner_dropdown_item,
                newIbanList
            )
        binding.spnAccountDropdown.adapter = adapter
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
