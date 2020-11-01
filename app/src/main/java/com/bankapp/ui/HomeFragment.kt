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
import com.bankapp.databinding.FragmentDisplayUsersBinding
import com.google.android.material.snackbar.Snackbar


@Suppress("UNCHECKED_CAST")
class HomeFragment : Fragment(), AccountRecyclerViewAdapter.OnAccountButtonClicked {

    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var binding: FragmentDisplayUsersBinding
    private lateinit var recyclerViewAdapter: AccountRecyclerViewAdapter

    override fun onResume() {
        super.onResume()
        mainActivityViewModel.getAllBankAccounts()
        with((activity as AppCompatActivity).supportActionBar){
            this?.title = getString(R.string.app_name)
            this?.setDisplayHomeAsUpEnabled(false)
            this?.setDisplayShowHomeEnabled(false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_display_users, container, false)
        recyclerViewAdapter = AccountRecyclerViewAdapter(emptyList(), this, viewLifecycleOwner, mainActivityViewModel)
        with(binding.bankAccountsRecyclerView) {
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            onFlingListener = object : RecyclerView.OnFlingListener() {
                override fun onFling(velocityX: Int, velocityY: Int): Boolean {
                    if(velocityY > velocityX) {
                        mainActivityViewModel.getAllBankAccounts()
                    }
                    return false
                }

            }
        }
        mainActivityViewModel.fetchAccountsState.observe(viewLifecycleOwner, { state ->
            when (state) {
                is FetchAccountsState.ShowData -> {
                    binding.loadUsersProgressBar.visibility = View.INVISIBLE
                    recyclerViewAdapter.submitList(state.accounts as List<Account>)
                }
                is FetchAccountsState.Loading -> {
                    if (recyclerViewAdapter.itemCount == 0){
                        binding.loadUsersProgressBar.visibility = View.VISIBLE
                    }
                }
                is FetchAccountsState.Idle -> Unit
                is FetchAccountsState.Error -> {
                    binding.loadUsersProgressBar.visibility = View.INVISIBLE
                    Snackbar.make(
                        binding.displayUsersRoot,
                        getString(R.string.could_not_refresh_list),
                        Snackbar.LENGTH_LONG
                    ).show()
                    mainActivityViewModel.setDeleteAccountState(DeleteAccountState.Idle)
                }
            }
        })

        mainActivityViewModel.deleteAccountState.observe(viewLifecycleOwner, { deleteAccountState ->
            when (deleteAccountState) {
                is DeleteAccountState.Error -> {
                    Snackbar.make(
                        binding.displayUsersRoot,
                        getString(R.string.could_not_delete_account),
                        Snackbar.LENGTH_LONG).show()
                    mainActivityViewModel.setDeleteAccountState(DeleteAccountState.Idle)
                }
                is DeleteAccountState.Idle -> Unit
                is DeleteAccountState.ShowData -> {
                    if (this::recyclerViewAdapter.isInitialized) {
                        recyclerViewAdapter.removeDeletedAccount(deleteAccountState.position)
                    }
                    mainActivityViewModel.setDeleteAccountState(DeleteAccountState.Idle)
                }
            }
        })
        binding.floatingActionButton.setOnClickListener {
           findNavController().navigate(HomeFragmentDirections.actionHomeToAddUser())
        }
        return binding.root
    }

    override fun onEditClick(accountNo: String, position: Int) {
        findNavController().navigate(HomeFragmentDirections.actionHomeToUpdateUser(accountNo))
    }

    override fun onDeleteClick(accountNo: String, position: Int) {
        mainActivityViewModel.delete(accountNo, position)
    }
}