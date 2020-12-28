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
import timber.log.Timber

class UserViewModel @ViewModelInject constructor(
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    val nameValidationLiveData: MutableLiveData<NameError> = MutableLiveData()
    val pinValidationLiveData: MutableLiveData<PinError> = MutableLiveData()

    class PinError(
        val occurred: Boolean,
        val reason: String
    )

    class NameError(
        val occurred: Boolean,
        val reason: String
    )

    fun checkPinValidityAndSave(pin: String) {
        when (pinValidation(pin)) {
            PinValidationStatus.INCORRECT_LENGTH -> {
                pinValidationLiveData.postValue(PinError(true, "PIN length incorrect"))
            }
            PinValidationStatus.NOT_REGISTERED -> {
                pinValidationLiveData.postValue(PinError(true, "PIN not registered"))
            }
            PinValidationStatus.INVALID -> {
                pinValidationLiveData.postValue(PinError(true, "PIN incorrect"))
            }
            PinValidationStatus.VALID -> {
                persistPin(pin)
                pinValidationLiveData.postValue(PinError(false, "PIN correct"))
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
            VALID -> {
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
            isIncorrectLength(pin) -> {
                PinValidationStatus.INCORRECT_LENGTH
            }
            registrationNotDone() -> {
                PinValidationStatus.NOT_REGISTERED
            }
            pin != getSavedPin() -> {
                PinValidationStatus.INVALID
            }
            else -> {
                PinValidationStatus.VALID
            }
        }
    }

    private fun nameValidation(name: String, surname: String): NameValidationStatus {
        TODO("Not yet implemented")
    }

    private fun getSavedPin() = sharedPreferences.getString(KEY_PIN, DEFAULT_VALUE)

    private fun registrationNotDone() = !sharedPreferences.contains(KEY_PIN)

    private fun isIncorrectLength(pin: String) = pin.length < 4 || pin.length > 6

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
