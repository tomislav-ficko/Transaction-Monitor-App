package hr.ficko.transactionmonitor.viewModels

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.ficko.transactionmonitor.other.*
import hr.ficko.transactionmonitor.viewModels.UserViewModel.NameValidationStatus.*
import hr.ficko.transactionmonitor.viewModels.UserViewModel.PinValidationStatus.*
import timber.log.Timber

class UserViewModel @ViewModelInject constructor(
    @ApplicationContext private val applicationContext: Context
) : ViewModel() {

    val nameValidationLiveData: MutableLiveData<NameError> = MutableLiveData()
    val pinValidationLiveData: MutableLiveData<PinError> = MutableLiveData()
    val displayNameLiveData: MutableLiveData<String> = MutableLiveData()

    class PinError(val occurred: Boolean, val reason: PinValidationStatus)

    enum class PinValidationStatus {
        VALID,
        INVALID,
        INCORRECT_LENGTH,
        NOT_REGISTERED
    }

    class NameError(val occurred: Boolean, val reason: NameValidationStatus)

    enum class NameValidationStatus {
        VALID,
        NAME_EMPTY,
        NAME_TOO_LONG,
        NAME_FORBIDDEN_CHAR,
        SURNAME_EMPTY,
        SURNAME_TOO_LONG,
        SURNAME_FORBIDDEN_CHAR
    }

    fun getSavedNameAndSurname() {
        val name = getSharedPreferences().getString(KEY_NAME, "")
        val surname = getSharedPreferences().getString(KEY_SURNAME, "")
        displayNameLiveData.postValue("$name $surname")
    }

    fun checkPinValidityAndNotifyFragment(pin: String) {
        when (pinValidation(pin)) {
            INCORRECT_LENGTH -> postPinErrorToLiveData(INCORRECT_LENGTH)
            NOT_REGISTERED -> postPinErrorToLiveData(NOT_REGISTERED)
            INVALID -> postPinErrorToLiveData(INVALID)
            else -> {
                persistPin(pin)
                pinValidationLiveData.postValue(PinError(false, PinValidationStatus.VALID))
            }
        }
    }

    fun checkNameValidityAndNotifyFragment(name: String, surname: String) {
        when (nameValidation(name, surname)) {
            NAME_EMPTY -> postNameErrorToLiveData(NAME_EMPTY)
            SURNAME_EMPTY -> postNameErrorToLiveData(SURNAME_EMPTY)
            NAME_TOO_LONG -> postNameErrorToLiveData(NAME_TOO_LONG)
            SURNAME_TOO_LONG -> postNameErrorToLiveData(SURNAME_TOO_LONG)
            NAME_FORBIDDEN_CHAR -> postNameErrorToLiveData(NAME_FORBIDDEN_CHAR)
            SURNAME_FORBIDDEN_CHAR -> postNameErrorToLiveData(SURNAME_FORBIDDEN_CHAR)
            else -> {
                persistNames(name, surname)
                nameValidationLiveData.postValue(
                    NameError(
                        false,
                        NameValidationStatus.VALID
                    )
                )
            }
        }
    }

    private fun persistNames(name: String, surname: String) {
        getSharedPreferences()
            .edit()
            .putString(KEY_NAME, name)
            .putString(KEY_SURNAME, surname)
            .apply()
        Timber.d("Names saved to SharedPreferences")
    }

    private fun persistPin(pin: String) {
        getSharedPreferences()
            .edit()
            .putString(KEY_PIN, pin)
            .apply()
        Timber.d("PIN saved to SharedPreferences")
    }

    private fun pinValidation(pin: String): PinValidationStatus {
        return when {
            pinIsIncorrectLength(pin) -> INCORRECT_LENGTH
            registrationNotDone() -> NOT_REGISTERED
            pin != getSavedPin() -> INVALID
            else -> PinValidationStatus.VALID
        }
    }

    private fun nameValidation(name: String, surname: String): NameValidationStatus {
        return when {
            name.isEmpty() -> NAME_EMPTY
            surname.isEmpty() -> SURNAME_EMPTY
            nameIsIncorrectLength(name) -> NAME_TOO_LONG
            nameIsIncorrectLength(surname) -> SURNAME_TOO_LONG
            containsNonAlphanumChars(name) -> NAME_FORBIDDEN_CHAR
            containsNonAlphanumChars(surname) -> SURNAME_FORBIDDEN_CHAR
            else -> NameValidationStatus.VALID
        }
    }

    private fun postPinErrorToLiveData(reason: PinValidationStatus) =
        pinValidationLiveData.postValue(PinError(true, reason))

    private fun postNameErrorToLiveData(reason: NameValidationStatus) =
        nameValidationLiveData.postValue(NameError(true, reason))

    private fun getSharedPreferences() =
        applicationContext.getSharedPreferences(
            SHARED_PREFERENCES,
            Context.MODE_PRIVATE
        )

    private fun getSavedPin() = getSharedPreferences().getString(KEY_PIN, DEFAULT_VALUE)

    private fun registrationNotDone() = !getSharedPreferences().contains(KEY_PIN)

    private fun pinIsIncorrectLength(pin: String) = pin.length < 4 || pin.length > 6

    private fun containsNonAlphanumChars(string: String): Boolean =
        string.filter { it in 'A'..'Z' || it in 'a'..'z' || it in '0'..'9' }
            .length != string.length

    private fun nameIsIncorrectLength(string: String): Boolean = string.length > 30
}
