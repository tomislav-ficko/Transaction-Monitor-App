package hr.ficko.transactionmonitor.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDataResponseModel(
    @Json(name = "user_id") val userId: String,
    @Json(name = "acounts") val accounts: List<Account>
)

@JsonClass(generateAdapter = true)
data class Account(
    @Json(name = "id") val accountId: String,
    @Json(name = "IBAN") val iban: String,
    @Json(name = "amount") val currentAmount: String,
    @Json(name = "currency") val currency: String,
    @Json(name = "transactions") val transactions: List<Transaction>
)

@JsonClass(generateAdapter = true)
data class Transaction(
    @Json(name = "id") val transactionId: String,
    @Json(name = "date") val date: String,
    @Json(name = "description") val description: String,
    @Json(name = "amount") val amount: String,
    @Json(name = "type") val type: String?,
)
