package com.example.fragments2.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fragments2.model.User
import com.example.fragments2.model.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KProperty

sealed class TextValidationErrorType: TextValidationStatus.Invalid(){
    data object InvalidNameFormat: TextValidationErrorType()
    data object InvalidEmailFormat: TextValidationErrorType()
    data object PhoneNumberFormat: TextValidationErrorType()
    // data object InvalidCountry: TextValidationErrorType()
    // data object InvalidCity: TextValidationErrorType()
}

class UserFormState(
    var name: String,
    var lastName: String,
    var phoneNumber: String,
    var email: String,
    var country: String,
    var city: String,
    var himself: String
)

class UserFormValidationStatus(
    var name: TextValidationStatus,
    var lastName: TextValidationStatus,
    var phoneNumber: TextValidationStatus,
    var email: TextValidationStatus,
    var country: TextValidationStatus,
    var city: TextValidationStatus,
    var himself: TextValidationStatus
)

sealed class TextValidationStatus {
    data object Empty: TextValidationStatus()
    data object Valid: TextValidationStatus()
    open class Invalid: TextValidationStatus()
}

@HiltViewModel
class UserEditViewModel @Inject constructor(
    private val repository: UserRepository,
    state: SavedStateHandle
): ViewModel() {

    private val idLiveData = state.getLiveData<Int>("UserID")
    val id: Int
        get() = idLiveData.value ?: 0

    private val _userFormState = MutableStateFlow(UserFormState("", "", "", "", "", "", ""))
    val userFormState = _userFormState.asStateFlow()

    private val _validationStatus = MutableStateFlow(UserFormValidationStatus(
        name = TextValidationStatus.Empty,
        lastName = TextValidationStatus.Empty,
        phoneNumber = TextValidationStatus.Empty,
        email = TextValidationStatus.Empty,
        country = TextValidationStatus.Empty,
        city = TextValidationStatus.Empty,
        himself = TextValidationStatus.Empty
    ))
    val validationStatus = _validationStatus.asStateFlow()

    init {
        if(id != 0){ viewModelScope.launch{ viewModelScope.launch(Dispatchers.IO) { repository.getUserById(id)?.apply {
            updateUserForm(
                UserFormState::name to name,
                UserFormState::lastName to lastName,
                UserFormState::phoneNumber to phoneNumber,
                UserFormState::email to email,
                UserFormState::country to country,
                UserFormState::city to city,
                UserFormState::himself to himself
            )
        }}}}
    }

    private val _backEvent = MutableSharedFlow<Unit>()
    val backEvent = _backEvent.asSharedFlow()

    fun submitUser(){
        if(id == 0){
            viewModelScope.launch(Dispatchers.IO) {
                repository.addUser(
                    User(
                        name = userFormState.value.name,
                        lastName = userFormState.value.lastName,
                        phoneNumber = userFormState.value.phoneNumber,
                        email = userFormState.value.email,
                        country = userFormState.value.country,
                        city = userFormState.value.city,
                        himself = userFormState.value.himself
                    )
                )
            }
        }
        else {
            viewModelScope.launch(Dispatchers.IO) {
                repository.updateUser(
                    User(
                        id = id,
                        name = userFormState.value.name,
                        lastName = userFormState.value.lastName,
                        phoneNumber = userFormState.value.phoneNumber,
                        email = userFormState.value.email,
                        country = userFormState.value.country,
                        city = userFormState.value.city,
                        himself = userFormState.value.himself
                    )
                )
            }
        }
        viewModelScope.launch{ _backEvent.emit(Unit) }
    }

    fun resetForm(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getUserById(id)?.apply {
                updateUserForm(
                    UserFormState::name to name,
                    UserFormState::lastName to lastName,
                    UserFormState::phoneNumber to phoneNumber,
                    UserFormState::email to email,
                    UserFormState::country to country,
                    UserFormState::city to city,
                    UserFormState::himself to himself
                )
            }
        }
    }

    fun clearForm(){
        updateUserForm(
            UserFormState::name to "",
            UserFormState::lastName to "",
            UserFormState::phoneNumber to "",
            UserFormState::email to "",
            UserFormState::country to "",
            UserFormState::city to "",
            UserFormState::himself to ""
        )
    }

    fun userNameChanged(name: String) = updateUserForm(UserFormState::name to name)
    fun userLastNameChanged(lastName: String) = updateUserForm(UserFormState::lastName to lastName)
    fun userPhoneNumberChanged(phoneNumber: String) = updateUserForm(UserFormState::phoneNumber to phoneNumber)
    fun userEmailChanged(email: String) = updateUserForm(UserFormState::email to email)
    fun userCountryChanged(country: String) = updateUserForm(UserFormState::country to country)
    fun userCityChanged(city: String) = updateUserForm(UserFormState::city to city)
    fun userHimselfChanged(himself: String) = updateUserForm(UserFormState::himself to himself)

    private fun updateUserForm(vararg properties: Pair<KProperty<String>, String>){

        var name: String = userFormState.value.name
        var lastName: String = userFormState.value.lastName
        var phoneNumber: String = userFormState.value.phoneNumber
        var email: String = userFormState.value.email
        var country: String = userFormState.value.country
        var city: String = userFormState.value.city
        var himself: String = userFormState.value.himself

        for((property, value) in properties){
            when(property){
                UserFormState::name -> name = value
                UserFormState::lastName -> lastName = value
                UserFormState::phoneNumber -> phoneNumber = value
                UserFormState::email -> email = value
                UserFormState::country -> country = value
                UserFormState::city -> city = value
                UserFormState::himself -> himself = value
            }
        }

        _userFormState.update {
            UserFormState(
                name = name,
                lastName = lastName,
                phoneNumber = phoneNumber,
                email = email,
                country = country,
                city = city,
                himself = himself
            )
        }
        validateForm()
    }

    private fun validateForm() {

        val nameRegexp = Regex(pattern = """^[A-Z][a-z]*$|^[А-Я][а-я]*$""")
        val emailRegexp = Regex("""^([A-Z]|[a-z]|[0-9])+\.?([A-Z]|[a-z]|[0-9])*@[a-z]+\.[a-z]+""")
        val phoneNumberRegexp = Regex("""\+[0-9]+\([0-9]{3}\)[0-9]{3} [0-9]{2}-[0-9]{2}""")

        val nameValidationStatus: TextValidationStatus = when{
            userFormState.value.name.isEmpty() -> TextValidationStatus.Empty
            !userFormState.value.name.matches(nameRegexp) -> TextValidationErrorType.InvalidNameFormat
            else -> TextValidationStatus.Valid
        }
        val lastNameValidationStatus: TextValidationStatus = when{
            userFormState.value.lastName.isEmpty() -> TextValidationStatus.Empty
            !userFormState.value.lastName.matches(nameRegexp) -> TextValidationErrorType.InvalidNameFormat
            else -> TextValidationStatus.Valid
        }
        val cityValidationStatus: TextValidationStatus = when{
            userFormState.value.city.isEmpty() -> TextValidationStatus.Empty
            !userFormState.value.city.matches(nameRegexp) -> TextValidationErrorType.InvalidNameFormat
            else -> TextValidationStatus.Valid
        }
        val countryValidationStatus: TextValidationStatus = when{
            userFormState.value.country.isEmpty() -> TextValidationStatus.Empty
            !userFormState.value.country.matches(nameRegexp) -> TextValidationErrorType.InvalidNameFormat
            else -> TextValidationStatus.Valid
        }
        val emailValidationStatus: TextValidationStatus = when{
            userFormState.value.email.isEmpty() -> TextValidationStatus.Empty
            !userFormState.value.email.matches(emailRegexp) -> TextValidationErrorType.InvalidEmailFormat
            else -> TextValidationStatus.Valid
        }
        val phoneNumberValidationStatus: TextValidationStatus = when{
            userFormState.value.phoneNumber.isEmpty() -> TextValidationStatus.Empty
            !userFormState.value.phoneNumber.matches(phoneNumberRegexp) -> TextValidationErrorType.PhoneNumberFormat
            else -> TextValidationStatus.Valid
        }


        val form = userFormState.value
        _validationStatus.update {
            UserFormValidationStatus(
                name = nameValidationStatus,
                lastName = lastNameValidationStatus,
                phoneNumber = phoneNumberValidationStatus,
                email = emailValidationStatus,
                country = countryValidationStatus,
                city = cityValidationStatus,
                himself = if (form.himself.isEmpty()) TextValidationStatus.Empty else TextValidationStatus.Valid
            )
        }
    }
}