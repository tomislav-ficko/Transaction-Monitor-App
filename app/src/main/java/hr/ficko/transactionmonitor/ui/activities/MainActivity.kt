package hr.ficko.transactionmonitor.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import hr.ficko.transactionmonitor.databinding.ActivityMainBinding
import hr.ficko.transactionmonitor.models.Account
import hr.ficko.transactionmonitor.models.Transaction
import hr.ficko.transactionmonitor.ui.SpinnerAdapter
import hr.ficko.transactionmonitor.ui.TransactionListAdapter
import hr.ficko.transactionmonitor.viewModels.RepositoryViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    @Inject
    lateinit var listAdapter: TransactionListAdapter

    private val viewModel by viewModels<RepositoryViewModel>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflateLayout()
        initializeRecyclerView()
        observeLiveData()
        initializeSpinner()

        loadUserData()
    }

    private fun inflateLayout() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initializeRecyclerView() {
        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = listAdapter
            addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    private fun observeLiveData() {
        viewModel.transactionsLiveData.observe(this, transactionObserver())
        viewModel.accountsLiveData.observe(this, accountObserver())
        viewModel.errorLiveData.observe(this, errorObserver())
    }

    private fun initializeSpinner() {
        changeProgressLoaderVisibility()
        binding.spnAccountDropdown.onItemSelectedListener = this
    }

    private fun loadUserData() {
        viewModel.loadUserData()
        populateSpinner()
    }


    private fun populateSpinner() {
        viewModel.getAccountData()
    }

    private fun getTransactionData(id: Int) {
        changeProgressLoaderVisibility()
        viewModel.getTransactionsForAccount(id)
    }

    private fun transactionObserver() = Observer<List<Transaction>> { data ->
        data?.let {
            changeProgressLoaderVisibility()
            showData(it)
        }
    }

    private fun accountObserver() = Observer<List<Account>> {
        it?.let {
            val ibanList = mutableListOf<String>().apply {
                add("Select IBAN") // This will be the spinner hint, must be the first element in the list
                addAll(it.map { account -> account.iban })
            }
            val adapter = SpinnerAdapter(this, binding.spnAccountDropdown.id, ibanList)
            binding.spnAccountDropdown.adapter = adapter
        }
    }

    private fun errorObserver() = Observer<Boolean> {

    }

    private fun showData(data: List<Transaction>) {
        listAdapter.apply {
            dataset = data
            notifyDataSetChanged()
        }
    }

    private fun changeProgressLoaderVisibility() {
        binding.apply {
            if (progressLoader.isVisible) {
                progressLoader.visibility = ProgressBar.INVISIBLE
            } else {
                progressLoader.visibility = ProgressBar.VISIBLE
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        // Skip the first item (hint)
        if (position > 0) {
            getTransactionData(position - 1)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        TODO("Not yet implemented")
    }
}
