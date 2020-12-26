package hr.ficko.transactionmonitor.network

import hr.ficko.transactionmonitor.models.UserDataResponseModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("builds/ISBD_public/Zadatak_1.json")
    fun getUserData(): Call<UserDataResponseModel>
}