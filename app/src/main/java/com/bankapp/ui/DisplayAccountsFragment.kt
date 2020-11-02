package com.bankapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bankapp.R
import com.bankapp.databinding.Account
import com.bankapp.databinding.AccountRecyclerViewAdapter
import com.bankapp.databinding.FragmentDisplayAccountsBinding
import com.google.android.material.snackbar.Snackbar

@Suppress("UNCHECKED_CAST")
class DisplayAccountsFragment : Fragment(), AccountRecyclerViewAdapter.OnAccountButtonClicked {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var binding: FragmentDisplayAccountsBinding
    private lateinit var recyclerViewAdapter: AccountRecyclerViewAdapter

    override fun onResume() {
        super.onResume()
        /** Whenever the fragment regains focus it makes a network request to refresh the list */
        sharedViewModel.getAllBankAccounts()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_display_accounts, container, false)

        /** This block initializes the toolbar's title and back arrow button */
        with((activity as AppCompatActivity).supportActionBar){
            this?.title = getString(R.string.app_name)
            this?.setDisplayHomeAsUpEnabled(false)
            this?.setDisplayShowHomeEnabled(false)
        }

        /** This block initializes the RecyclerView and it's adapter */
        recyclerViewAdapter = AccountRecyclerViewAdapter(this, viewLifecycleOwner, sharedViewModel)
        with(binding.bankAccountsRecyclerView) {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            onFlingListener = object : RecyclerView.OnFlingListener() {
                override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                    if(velocityY > velocityX) {
                        /** This listener detects if you fling to navigate to the bottom and that calls the viewModel
                            to refresh the list */
                        sharedViewModel.getAllBankAccounts()
                    }
                    return false
                }
            }
        }

        /** Sets up the viewModel observers */
        sharedViewModel.fetchAccountListState.observe(viewLifecycleOwner, { state ->
            when (state) {
                is FetchAccountListState.ShowData -> {
                    binding.loadUsersProgressBar.visibility = View.INVISIBLE
                    recyclerViewAdapter.submitList(state.accounts as List<Account>)
                }
                is FetchAccountListState.Loading -> {
                    if (recyclerViewAdapter.itemCount == 0){
                        binding.loadUsersProgressBar.visibility = View.VISIBLE
                    }
                }
                is FetchAccountListState.Idle -> Unit
                is FetchAccountListState.Error -> {
                    binding.loadUsersProgressBar.visibility = View.INVISIBLE
                    Snackbar.make(
                        binding.displayUsersRoot,
                        getString(R.string.could_not_refresh_list),
                        Snackbar.LENGTH_LONG
                    ).show()
                    sharedViewModel.setDeleteAccountState(DeleteAccountState.Idle)
                }
            }
        })

        sharedViewModel.deleteAccountState.observe(viewLifecycleOwner, { deleteAccountState ->
            when (deleteAccountState) {
                is DeleteAccountState.Error -> {
                    Snackbar.make(
                        binding.displayUsersRoot,
                        getString(R.string.could_not_delete_account),
                        Snackbar.LENGTH_LONG).show()
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

        /** Sets up the floating action button listener */
        binding.floatingActionButton.setOnClickListener {
           findNavController().navigate(DisplayAccountsFragmentDirections.actionHomeToAddUser())
        }
        return binding.root
    }

    /** The following 'override functions' detect clicks on the edit/delete buttons of a RecyclerView item's .
        This fragment implements the OnAccountButtonClicked of the AccountRecyclerViewAdapter,
        and listens to those clicks.
        It (this Fragment) is passed as the listener in the AccountRecyclerViewAdapter's constructor. */

    override fun onEditClick(accountNo: String, position: Int) {
        findNavController().navigate(DisplayAccountsFragmentDirections.actionHomeToUpdateUser(accountNo))
    }

    override fun onDeleteClick(accountNo: String, position: Int) {
        sharedViewModel.delete(accountNo, position)
    }
}