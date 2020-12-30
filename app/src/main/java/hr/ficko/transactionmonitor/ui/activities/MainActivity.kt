package hr.ficko.transactionmonitor.ui.activities

import android.os.Bundle
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
import hr.ficko.transactionmonitor.ui.TransactionListAdapter
import hr.ficko.transactionmonitor.viewModels.RepositoryViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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
        viewModel.transactionsLiveData.observe(this, getTransactionObserver())
        viewModel.accountsLiveData.observe(this, getAccountObserver())
        viewModel.errorLiveData.observe(this, getErrorObserver())
    }

    private fun initializeSpinner() {
        TODO("set first value to 'Choose account' and disable it")
        changeProgressLoaderVisibility()

        TODO("when spinner option is selected, get data for the desired account")
//        fetchData(accountId)
    }

    private fun loadUserData() {
        viewModel.loadUserData()
        populateSpinner()
    }


    private fun populateSpinner() {
        viewModel.getAccountData()
//        binding.spnAccountDropdown.
    }

    private fun fetchData(id: Int) {
        changeProgressLoaderVisibility()
        viewModel.getTransactionsForAccount(id)
    }

    private fun getTransactionObserver() = Observer<List<Transaction>> { data ->
        data?.let {
            changeProgressLoaderVisibility()
            showData(it)
        }
    }

    private fun getAccountObserver() = Observer<List<Account>> {

    }

    private fun getErrorObserver() = Observer<Boolean> {

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
}
