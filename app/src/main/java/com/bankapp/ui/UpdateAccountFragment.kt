package com.bankapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bankapp.R
import com.bankapp.databinding.Account
import com.bankapp.databinding.FragmentUpdateAccountBinding
import com.google.android.material.snackbar.Snackbar


class UpdateAccountFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var binding: FragmentUpdateAccountBinding
    private lateinit var textWatcher: TextWatcher
    private val actionNavArgs: UpdateAccountFragmentArgs by navArgs() /** The navController will populate this for us*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_account, container, false)
        binding.listener = this
        sharedViewModel.getBankAccount(actionNavArgs.accountNo) /** This gets the 'accountNo' from the navigation arguments (or, navArgs, see the app_nav.xml > click the
        arrow that connects to the UpdateAccountFragment & see it's attributes)
        This accountNo is passed to the action that opens this fragment.
        The viewModel then retrieves the account from the network by the accountNo from the actionNavArgs.*/



        with((activity as AppCompatActivity).supportActionBar) {
            this?.title = getString(R.string.update_account)
            this?.setDisplayHomeAsUpEnabled(true)
            this?.setDisplayShowHomeEnabled(true)
        }

        /** Sets up the viewModel fetchAccountState observer */
        sharedViewModel.updateAccountState.observe(viewLifecycleOwner, { state ->
            when (state) {
                is UpdateAccountState.Idle -> Unit
                is UpdateAccountState.Loading -> accountUpdateLoading()
                is UpdateAccountState.Error -> {
                    accountUpdateNotLoading()
                    Snackbar.make(
                        binding.updateAccountRoot,
                        getString(R.string.could_not_update_account),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    sharedViewModel.setUpdateAccountState(UpdateAccountState.Idle)
                }
                is UpdateAccountState.ShowData -> {
                    accountUpdateNotLoading()
                    findNavController().popBackStack()
                    binding.updateUpdateButton.isEnabled = false
                    Snackbar.make(
                        binding.updateAccountRoot,
                        getString(R.string.account_updated),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    sharedViewModel.setUpdateAccountState(UpdateAccountState.Idle)
                }
            }
        })

        /** Sets up the viewModel fetchAccountState observer */
        sharedViewModel.fetchAccountState.observe(viewLifecycleOwner, { state ->
            when (state) {
                is FetchAccountState.Idle -> Unit
                is FetchAccountState.Loading -> accountFetchLoading()
                is FetchAccountState.Error -> {
                    Snackbar.make(
                        binding.updateAccountRoot,
                        getString(R.string.could_not_fetch_account),
                        Snackbar.LENGTH_LONG
                    ).show()
                    binding.loadUpdateAccountProgressBar.visibility = INVISIBLE
                    sharedViewModel.setFetchAccountState(FetchAccountState.Idle)
                }
                is FetchAccountState.ShowData -> {
                    accountFetchNotLoading()
                    binding.bankAccount = state.accounts
                    sharedViewModel.setFetchAccountState(FetchAccountState.Idle)
                }
            }
        })
        return binding.root
    }

    fun updateButtonClick() {
        with(binding) {
            sharedViewModel.updateBankAccount(
                Account(
                    name = updateNameEditText.text.toString().trim(),
                    accountNo = updateAccountNoTextView.text.toString().trim(),
                    balance = updateBalanceEditText.text.toString().toLong(),
                    acctType = updateAccountTypeAutocompleteTextView.text.toString().trim()
                )
            )
        }
    }

    /** These functions render the UI state according to the LiveData from the viewModel */
    private fun accountUpdateLoading() {
        with(binding) {
            updateUpdateButton.visibility = INVISIBLE
            updateProgressBar.visibility = View.VISIBLE
        }
    }
    private fun accountUpdateNotLoading() {
        with(binding) {
            updateUpdateButton.visibility = View.VISIBLE
            updateProgressBar.visibility = INVISIBLE
        }
    }
    private fun accountFetchLoading() {
        with(binding) {
            updateUpdateButton.visibility = INVISIBLE
            updateNameTextLayout.visibility = INVISIBLE
            updateAccountNoLabel.visibility = INVISIBLE
            updateAccountNoTextView.visibility = INVISIBLE
            updateAccountTypeTextLayout.visibility = INVISIBLE
            updateBalanceTextLayout.visibility = INVISIBLE
            loadUpdateAccountProgressBar.visibility = VISIBLE
        }
    }
    private fun accountFetchNotLoading() {
        with(binding) {
            updateUpdateButton.visibility = VISIBLE
            updateNameTextLayout.visibility = VISIBLE
            updateAccountNoLabel.visibility = VISIBLE
            updateAccountNoTextView.visibility = VISIBLE
            updateAccountTypeTextLayout.visibility = VISIBLE
            updateBalanceTextLayout.visibility = VISIBLE
            loadUpdateAccountProgressBar.visibility = INVISIBLE
        }
    }

    /** The logic that is used by input fields of this fragment to enable/disable the update button */
    fun getTextWatcher(): TextWatcher {
        if (!this::textWatcher.isInitialized) {
            textWatcher = object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    with(binding) {
                        updateUpdateButton.isEnabled =
                            !updateNameEditText.text.isNullOrBlank() &&
                                    !updateAccountNoTextView.text.isNullOrBlank() &&
                                    !updateAccountTypeAutocompleteTextView.text.isNullOrBlank() &&
                                    !updateBalanceEditText.text.isNullOrBlank()
                    }
                }

                override fun afterTextChanged(p0: Editable?) {}
            }
        }
        return textWatcher
    }
}