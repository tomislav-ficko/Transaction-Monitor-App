package hr.ficko.transactionmonitor.viewModels

import android.content.SharedPreferences
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hr.ficko.transactionmonitor.other.DEFAULT_VALUE
import hr.ficko.transactionmonitor.other.KEY_NAME
import hr.ficko.transactionmonitor.other.KEY_PIN
import hr.ficko.transactionmonitor.other.KEY_SURNAME
import hr.ficko.transactionmonitor.viewModels.UserViewModel.NameValidationStatus.*
import hr.ficko.transactionmonitor.viewModels.UserViewModel.PinValidationStatus.*
import timber.log.Timber

class UserViewModel @ViewModelInject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val nameValidationLiveData: MutableLiveData<NameError> = MutableLiveData()
    val pinValidationLiveData: MutableLiveData<PinError> = MutableLiveData()
    val displayNameLiveData: MutableLiveData<String> = MutableLiveData()

    class PinError(
        val occurred: Boolean,
        val reason: PinValidationStatus
    )

    class NameError(
        val occurred: Boolean,
        val reason: String
    )

    fun getSavedNameAndSurname() {
        val name = sharedPreferences.getString(KEY_NAME, "")
        val surname = sharedPreferences.getString(KEY_SURNAME, "")
        displayNameLiveData.postValue("$name $surname")
    }

    fun checkPinValidityAndSave(pin: String) {
        when (pinValidation(pin)) {
            INCORRECT_LENGTH -> {
                pinValidationLiveData.postValue(PinError(true, INCORRECT_LENGTH))
            }
            NOT_REGISTERED -> {
                pinValidationLiveData.postValue(PinError(true, NOT_REGISTERED))
            }
            INVALID -> {
                pinValidationLiveData.postValue(PinError(true, INVALID))
            }
            PinValidationStatus.VALID -> {
                persistPin(pin)
                pinValidationLiveData.postValue(PinError(false, PinValidationStatus.VALID))
            }
        }
    }

    fun checkNameValidityAndSave(name: String, surname: String) {
        when (nameValidation(name, surname)) {
            NAME_EMPTY, SURNAME_EMPTY -> {
                nameValidationLiveData.postValue(
                    NameError(
                        true,
                        "Both fields are required"
                    )
                )
            }
            NAME_TOO_LONG, SURNAME_TOO_LONG -> {
                nameValidationLiveData.postValue(
                    NameError(
                        true,
                        "Fields must not be longer than 30 characters"
                    )
                )
            }
            NAME_FORBIDDEN_CHAR, SURNAME_FORBIDDEN_CHAR -> {
                nameValidationLiveData.postValue(
                    NameError(
                        true,
                        "Fields must contain only alphanumeric characters"
                    )
                )
            }
            NameValidationStatus.VALID -> {
                persistNames(name, surname)
                nameValidationLiveData.postValue(
                    NameError(
                        false,
                        "Names valid"
                    )
                )
            }
        }
    }

    private fun persistNames(name: String, surname: String) {
        sharedPreferences
            .edit()
            .putString(KEY_NAME, name)
            .putString(KEY_SURNAME, surname)
            .apply()
        Timber.d("Names saved to SharedPreferences")
    }

    private fun persistPin(pin: String) {
        sharedPreferences
            .edit()
            .putString(KEY_PIN, pin)
            .apply()
        Timber.d("PIN saved to SharedPreferences")
    }

    private fun pinValidation(pin: String): PinValidationStatus {
        return when {
            pinIsIncorrectLength(pin) -> {
                INCORRECT_LENGTH
            }
            registrationNotDone() -> {
                NOT_REGISTERED
            }
            pin != getSavedPin() -> {
                INVALID
            }
            else -> {
                PinValidationStatus.VALID
            }
        }
    }

    private fun nameValidation(name: String, surname: String): NameValidationStatus {
        return when {
            containsNonAlphanumChars(name) -> {
                NAME_FORBIDDEN_CHAR
            }
            containsNonAlphanumChars(surname) -> {
                SURNAME_FORBIDDEN_CHAR
            }
            nameIsIncorrectLength(name) -> {
                NAME_TOO_LONG
            }
            nameIsIncorrectLength(surname) -> {
                SURNAME_TOO_LONG
            }
            name.isEmpty() -> {
                NAME_EMPTY
            }
            surname.isEmpty() -> {
                SURNAME_EMPTY
            }
            else -> {
                NameValidationStatus.VALID
            }
        }
    }

    private fun getSavedPin() = sharedPreferences.getString(KEY_PIN, DEFAULT_VALUE)

    private fun registrationNotDone() = !sharedPreferences.contains(KEY_PIN)

    private fun pinIsIncorrectLength(pin: String) = pin.length < 4 || pin.length > 6

    private fun containsNonAlphanumChars(string: String): Boolean =
        string.filter { it in 'A'..'Z' || it in 'a'..'z' || it in '0'..'9' }
            .length != string.length

    private fun nameIsIncorrectLength(string: String): Boolean = string.length > 30

    enum class PinValidationStatus {
        VALID,
        INVALID,
        INCORRECT_LENGTH,
        NOT_REGISTERED
    }

    enum class NameValidationStatus {
        VALID,
        NAME_EMPTY,
        NAME_TOO_LONG,
        NAME_FORBIDDEN_CHAR,
        SURNAME_EMPTY,
        SURNAME_TOO_LONG,
        SURNAME_FORBIDDEN_CHAR
    }
}
