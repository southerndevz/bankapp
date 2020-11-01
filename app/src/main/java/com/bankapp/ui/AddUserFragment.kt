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
import com.bankapp.databinding.FragmentAddUserBinding
import com.google.android.material.snackbar.Snackbar


class AddUserFragment : Fragment() {

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var binding: FragmentAddUserBinding
    private lateinit var textWatcher: TextWatcher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_user, container, false)
        with((activity as AppCompatActivity).supportActionBar) {
            this?.title = getString(R.string.add_user)
            this?.setDisplayHomeAsUpEnabled(true)
            this?.setDisplayShowHomeEnabled(true)
        }

        with(binding) {
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
            addNameEditText.addTextChangedListener(getTextWatcher())
            addBalanceEditText.addTextChangedListener(getTextWatcher())
            addAccountTypeAutocompleteTextView.addTextChangedListener(getTextWatcher())
            addAccountNumberEditText.addTextChangedListener(getTextWatcher())
            addAddButton.setOnClickListener {
                mainActivityViewModel.createBankAccount(
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
        mainActivityViewModel.createAccountsState.observe(viewLifecycleOwner, { state ->
            when (state) {
                is CreateAccountState.Idle -> Unit
                is CreateAccountState.Loading -> accountCreateLoading()
                is CreateAccountState.Error -> {
                    accountCreateNotLoading()
                    Snackbar.make(
                        binding.addUserRoot,
                        getString(R.string.could_not_add_user),
                        Snackbar.LENGTH_LONG
                    ).show()
                    mainActivityViewModel.setCreateAccountsState(CreateAccountState.Idle)
                }
                is CreateAccountState.ShowData -> {
                    accountCreateNotLoading()
                    binding.addAddButton.isEnabled = false
                    findNavController().popBackStack()
                    Snackbar.make(
                        binding.addUserRoot,
                        getString(R.string.user_added),
                        Snackbar.LENGTH_LONG
                    ).show()
                    mainActivityViewModel.setCreateAccountsState(CreateAccountState.Idle)
                }
            }
        })
        return binding.root
    }


    private fun accountCreateLoading() {
        with(binding) {
            addAddButton.visibility = View.INVISIBLE
            addProgressBar.visibility = View.VISIBLE
        }
    }

    private fun accountCreateNotLoading() {
        with(binding) {
            addAddButton.visibility = View.VISIBLE
            addProgressBar.visibility = View.INVISIBLE
        }
    }

    private fun getTextWatcher(): TextWatcher {
        if (!this::textWatcher.isInitialized) {
            textWatcher = object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    with(binding) {
                        addAddButton.isEnabled = !addNameEditText.text.isNullOrBlank() &&
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