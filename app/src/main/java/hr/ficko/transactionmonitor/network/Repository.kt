package hr.ficko.transactionmonitor.network

import hr.ficko.transactionmonitor.models.Account
import hr.ficko.transactionmonitor.models.UserDataResponseModel
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(
    private val apiService: ApiService
) {

    lateinit var accounts: List<Account>

    fun getUserData(): Response<UserDataResponseModel> {
        return apiService.getUserData().execute()
    }
}
