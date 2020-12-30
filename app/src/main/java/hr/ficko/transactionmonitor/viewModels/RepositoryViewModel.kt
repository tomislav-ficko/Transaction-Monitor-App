package hr.ficko.transactionmonitor.viewModels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.ficko.transactionmonitor.models.Account
import hr.ficko.transactionmonitor.models.Transaction
import hr.ficko.transactionmonitor.models.UserDataResponseModel
import hr.ficko.transactionmonitor.network.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber

class RepositoryViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    val transactionsLiveData: MutableLiveData<List<Transaction>> = MutableLiveData()
    val accountsLiveData: MutableLiveData<List<Account>> = MutableLiveData()
    val errorLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun loadUserData() {
        getUserData()
    }

    fun getAccountData() {
        accountsLiveData.postValue(repository.accounts)
    }

    fun getTransactionsForAccount(id: Int) {
        transactionsLiveData.postValue(repository.accounts[id].transactions)
    }

    private fun getUserData(): Boolean {
        var status = false
        viewModelScope.launch(Dispatchers.IO) {
            status = getResponseSuccessStatus(
                repository.getUserData()
            )
        }
        return status
    }

    private fun getResponseSuccessStatus(response: Response<UserDataResponseModel>?): Boolean {
        if (response == null) {
            Timber.d("Error -> Response is null")
            errorLiveData.postValue(true)
        } else if (!response.isSuccessful) {
            Timber.d(
                "Error -> Response not successful. Status %d: %s",
                response.code(),
                response.message()
            )
            errorLiveData.postValue(true)
        } else {
            response.body()?.let {
                repository.accounts = it.accounts
                return true
            }
        }
        return false
    }
}