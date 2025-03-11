package com.example.fragments2.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.fragments2.R
import com.example.fragments2.databinding.FragmentUserEditBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@AndroidEntryPoint
class UserEditFragment : Fragment() {

    private var binding: FragmentUserEditBinding? = null

    private val viewModel: UserEditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserEditBinding.inflate(inflater, container, false)

        binding?.let{ with(it){

            // binding editTexts
            nameEditText.doAfterTextChanged { text -> viewModel.userNameChanged(text.toString()) }
            lastNameEditText.doAfterTextChanged { text -> viewModel.userLastNameChanged(text.toString()) }
            phoneNumberEditText.doAfterTextChanged { text -> viewModel.userPhoneNumberChanged(text.toString()) }
            emailEditText.doAfterTextChanged { text -> viewModel.userEmailChanged(text.toString()) }
            countryEditText.doAfterTextChanged { text -> viewModel.userCountryChanged(text.toString()) }
            cityEditText.doAfterTextChanged { text -> viewModel.userCityChanged(text.toString()) }
            aboutHimselfEditText.doAfterTextChanged { text -> viewModel.userHimselfChanged(text.toString()) }

            // this button is used to submit user data
            submitButton.setOnClickListener { viewModel.submitUser() }
            //set submit button disabled by default
            submitButton.isEnabled = false

            //reset button
            resetButton.setOnClickListener { viewModel.resetForm() }

            //clear button
            clearButton.setOnClickListener { viewModel.clearForm() }

            // back button
            lifecycleScope.launch {
                viewModel.backEvent.collect {
                    findNavController().popBackStack()
                }
            }

            // reading values from viewmodel
            lifecycleScope.launch {
                viewModel.userFormState.collect { value -> value.apply {
                    if (nameEditText.text.toString() != name) nameEditText.setText(name)
                    if (lastNameEditText.text.toString() != lastName) lastNameEditText.setText(lastName)
                    if (phoneNumberEditText.text.toString() != phoneNumber) phoneNumberEditText.setText(phoneNumber)
                    if (emailEditText.text.toString() != email) emailEditText.setText(email)
                    if (countryEditText.text.toString() != country) countryEditText.setText(country)
                    if (cityEditText.text.toString() != city) cityEditText.setText(city)
                    if (aboutHimselfEditText.text.toString() != himself) aboutHimselfEditText.setText(himself)
                }}
            }

            // validation
            lifecycleScope.launch {
                viewModel.validationStatus.collect { value -> value.apply {
                    nameEditText.error = (name as? TextValidationStatus.Invalid)?.description
                    lastNameEditText.error = (name as? TextValidationStatus.Invalid)?.description
                    phoneNumberEditText.error = (phoneNumber as? TextValidationStatus.Invalid)?.description
                    emailEditText.error = (email as? TextValidationStatus.Invalid)?.description
                    countryEditText.error = (country as? TextValidationStatus.Invalid)?.description
                    cityEditText.error = (city as? TextValidationStatus.Invalid)?.description
                    aboutHimselfEditText.error = (himself as? TextValidationStatus.Invalid)?.description

                    // name couldn't be empty
                    nameEditText.error = if (name is TextValidationStatus.Empty) getText(R.string.edittext_empty_error) else null
                    lastNameEditText.error = if (lastName is TextValidationStatus.Empty) getText(R.string.edittext_empty_error) else null
                    phoneNumberEditText.error = if (phoneNumber is TextValidationStatus.Empty) getText(R.string.edittext_empty_error) else null
                    emailEditText.error = if (email is TextValidationStatus.Empty) getText(R.string.edittext_empty_error) else null
                    countryEditText.error = if (country is TextValidationStatus.Empty) getText(R.string.edittext_empty_error) else null
                    cityEditText.error = if (city is TextValidationStatus.Empty) getText(R.string.edittext_empty_error) else null
                    aboutHimselfEditText.error = if (himself is TextValidationStatus.Empty) getText(R.string.edittext_empty_error) else null

                    // if all fields are valid, submit button is enabled
                    submitButton.isEnabled = name is TextValidationStatus.Valid &&
                            lastName is TextValidationStatus.Valid &&
                            phoneNumber is TextValidationStatus.Valid &&
                            email is TextValidationStatus.Valid &&
                            country is TextValidationStatus.Valid &&
                            city is TextValidationStatus.Valid &&
                            himself is TextValidationStatus.Valid
                }}
            }
        }}

        return binding?.root
    }
}