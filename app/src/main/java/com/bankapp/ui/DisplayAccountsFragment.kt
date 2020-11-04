package com.bankapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bankapp.R
import com.bankapp.databinding.Account
import com.bankapp.databinding.AccountRecyclerViewAdapter
import com.bankapp.databinding.FragmentDisplayAccountsBinding
import com.google.android.material.snackbar.Snackbar

@Suppress("UNCHECKED_CAST") /**Suppress the unchecked cast that happens on the
                                        submitList(accountsList) function because
                                        I forcefully cast the state.accounts to a List<Accounts>,
                                        I mean what other successful result will that operation yield
                                        except for a list of Accounts? */
class DisplayAccountsFragment : Fragment(), AccountRecyclerViewAdapter.OnAccountButtonClicked {

    val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var binding: FragmentDisplayAccountsBinding
    private lateinit var recyclerViewAdapter: AccountRecyclerViewAdapter

    override fun onResume() {
        super.onResume()
        /** Whenever the fragment regains focus it makes a network request to refresh the list */
        sharedViewModel.getAllBankAccounts()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_display_accounts, container, false)
        binding.listener = this

        /** This block initializes the toolbar's title and removes the back arrow button.
        Since this fragment is the startDestination it does not need a back button on the toolbar */
        with((activity as AppCompatActivity).supportActionBar) {
            this?.title = getString(R.string.app_name)
            this?.setDisplayHomeAsUpEnabled(false)
            this?.setDisplayShowHomeEnabled(false)
        }

        /** This block initializes the RecyclerView and it's adapter */
        recyclerViewAdapter = AccountRecyclerViewAdapter(
            listener = this,
            viewLifecycleOwner = viewLifecycleOwner,
            sharedViewModel = sharedViewModel)
        binding.bankAccountsRecyclerView.adapter = recyclerViewAdapter

        /** Sets up the viewModel fetchAccountListState observer */
        sharedViewModel.fetchAccountListState.observe(viewLifecycleOwner, { state ->
            when (state) {
                is FetchAccountListState.ShowData -> {
                    recyclerViewAdapter.submitList(state.accounts as List<Account>)
                    binding.loadUsersProgressBar.visibility = View.INVISIBLE
                }
                is FetchAccountListState.Loading -> {
                    if (recyclerViewAdapter.itemCount == 0) {
                        binding.loadUsersProgressBar.visibility = View.VISIBLE
                    }
                }
                is FetchAccountListState.Idle -> Unit
                is FetchAccountListState.Error -> {
                    Snackbar.make(
                        binding.displayUsersRoot,
                        getString(R.string.could_not_refresh_list),
                        Snackbar.LENGTH_LONG
                    ).show()
                    binding.loadUsersProgressBar.visibility = View.INVISIBLE
                    sharedViewModel.setDeleteAccountState(DeleteAccountState.Idle)
                }
            }
        })

        /** Sets up the viewModel deleteAccountState observer */
        sharedViewModel.deleteAccountState.observe(viewLifecycleOwner, { deleteAccountState ->
            when (deleteAccountState) {
                is DeleteAccountState.Error -> {
                    Snackbar.make(
                        binding.displayUsersRoot,
                        getString(R.string.could_not_delete_account),
                        Snackbar.LENGTH_LONG
                    ).show()
                    sharedViewModel.setDeleteAccountState(DeleteAccountState.Idle)
                }
                is DeleteAccountState.Idle -> Unit
                is DeleteAccountState.ShowData -> {
                    if (this::recyclerViewAdapter.isInitialized) {
                        recyclerViewAdapter.removeDeletedAccount(deleteAccountState.position)
                    }
                    sharedViewModel.setDeleteAccountState(DeleteAccountState.Idle)
                }
            }
        })
        return binding.root
    }

    fun onFABClick(fab: View) {
        fab.findNavController().navigate(DisplayAccountsFragmentDirections.actionHomeToAddUser())
    }

    /** The following 'override functions' detect clicks on the edit/delete buttons of a RecyclerView item's .
    This fragment implements the OnAccountButtonClicked of the AccountRecyclerViewAdapter,
    and listens to those clicks.
    It (this Fragment) is passed as the listener in the AccountRecyclerViewAdapter's constructor. */
    override fun onEditClick(accountNo: String, position: Int) {
        findNavController().navigate(
            DisplayAccountsFragmentDirections.actionHomeToUpdateUser(
                accountNo
            )
        )
    }

    override fun onDeleteClick(accountNo: String, position: Int) {
        sharedViewModel.delete(accountNo, position)
    }
}
