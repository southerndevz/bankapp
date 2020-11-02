package com.bankapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bankapp.R
import com.bankapp.databinding.Account
import com.bankapp.databinding.FragmentAddAccountBinding
import com.google.android.material.snackbar.Snackbar


class AddAccountFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var binding: FragmentAddAccountBinding
    private lateinit var textWatcher: TextWatcher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_account, container, false)

        /** This block initializes the toolbar's title and back arrow button */
        with((activity as AppCompatActivity).supportActionBar) {
            this?.title = getString(R.string.add_account)
            this?.setDisplayHomeAsUpEnabled(true)
            this?.setDisplayShowHomeEnabled(true)
        }


        with(binding) {

            /** Populates the AutoCompleteTextView's drop-down item from the string-array in resources and sets it up */
            addAccountTypeAutocompleteTextView.setAdapter(ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                resources.getStringArray(R.array.account_type_array)
            ))

            addAccountTypeAutocompleteTextView.apply {
                setOnFocusChangeListener { _, focused ->
                    runCatching {
                        if (focused) {
                            if (!text.isNullOrBlank()) {
                                val editable = text
                                setText("", true)
                                showDropDown()
                                setText(editable, false)
                            }
                        }
                    }
                }
            }
            /** Sets up the fields so that they can enable the Create button when all fields are populated */
            addNameEditText.addTextChangedListener(getTextWatcher())
            addBalanceEditText.addTextChangedListener(getTextWatcher())
            addAccountTypeAutocompleteTextView.addTextChangedListener(getTextWatcher())
            addAccountNumberEditText.addTextChangedListener(getTextWatcher())

            /** Sets up the button listeners */
            addCreateButton.setOnClickListener {
                sharedViewModel.createBankAccount(
                    Account(
                        name = addNameEditText.text.toString().trim(),
                        accountNo = addAccountNumberEditText.text.toString().trim(),
                        balance = addBalanceEditText.text.toString().toLong(),
                        acctType = addAccountTypeAutocompleteTextView.text.toString().trim()
                    )
                )
            }
            addCancelButton.setOnClickListener { button ->
                button.findNavController().popBackStack()
            }
        }

        /** Sets up the viewModel observers */
        sharedViewModel.createAccountsState.observe(viewLifecycleOwner, { state ->
            when (state) {
                is CreateAccountState.Idle -> Unit
                is CreateAccountState.Loading -> accountCreateLoading()
                is CreateAccountState.Error -> {
                    accountCreateNotLoading()
                    Snackbar.make(
                        binding.addAccountRoot,
                        getString(R.string.could_not_add_account),
                        Snackbar.LENGTH_LONG
                    ).show()
                    sharedViewModel.setCreateAccountsState(CreateAccountState.Idle)
                }
                is CreateAccountState.ShowData -> {
                    accountCreateNotLoading()
                    binding.addCreateButton.isEnabled = false
                    findNavController().popBackStack()
                    Snackbar.make(
                        binding.addAccountRoot,
                        getString(R.string.account_created),
                        Snackbar.LENGTH_LONG
                    ).show()
                    sharedViewModel.setCreateAccountsState(CreateAccountState.Idle)
                }
            }
        })
        return binding.root
    }


    /** Functions to execute by viewModel state listeners */
    private fun accountCreateLoading() {
        with(binding) {
            addCreateButton.visibility = View.INVISIBLE
            addProgressBar.visibility = View.VISIBLE
        }
    }

    private fun accountCreateNotLoading() {
        with(binding) {
            addCreateButton.visibility = View.VISIBLE
            addProgressBar.visibility = View.INVISIBLE
        }
    }

    /** The logic that is used by input fields to enable/disable the create button */
    private fun getTextWatcher(): TextWatcher {
        if (!this::textWatcher.isInitialized) {
            textWatcher = object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    with(binding) {
                        addCreateButton.isEnabled = !addNameEditText.text.isNullOrBlank() &&
                                !addAccountNumberEditText.text.isNullOrBlank() &&
                                !addAccountTypeAutocompleteTextView.text.isNullOrBlank() &&
                                !addBalanceEditText.text.isNullOrBlank()
                    }
                }

                override fun afterTextChanged(p0: Editable?) {}
            }
        }
        return textWatcher
    }
}