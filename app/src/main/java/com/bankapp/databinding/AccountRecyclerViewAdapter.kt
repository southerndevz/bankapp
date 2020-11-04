package com.bankapp.databinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bankapp.R
import com.bankapp.databinding.AccountRecyclerViewAdapter.AccountViewHolder
import com.bankapp.ui.DeleteAccountState
import com.bankapp.ui.SharedViewModel

class AccountRecyclerViewAdapter(
    private val listener: OnAccountButtonClicked,
    private val viewLifecycleOwner: LifecycleOwner, /** We need the viewLifecycleOwner to be our observer of the delete operation.
                                                        It will enable us to enable or disable the buttons */
    private val sharedViewModel: SharedViewModel /** The viewModel is needed because that's where our liveData is when we delete one RecyclerView item */
) : RecyclerView.Adapter<AccountViewHolder>() {

    private var listOfAccounts: List<Account> = emptyList() /** This list will be updated using the submitList() function whenever
                                                                a web request for an updated list is successful */

    inner class AccountViewHolder(val recyclerViewItemBinding: RecyclerViewAccountItemBinding) :
        RecyclerView.ViewHolder(recyclerViewItemBinding.root) {

        init {
            recyclerViewItemBinding.listener = this

            /** Observing if delete is in progress so that the buttons can be disabled temporarily */
            sharedViewModel.deleteAccountState.observe(viewLifecycleOwner, { state ->
                when (state) {
                    is DeleteAccountState.Loading -> with(recyclerViewItemBinding) {
                        deleteAccountPerson.isEnabled = false; editAccountPersonButton.isEnabled =
                        false
                    }
                    else -> with(recyclerViewItemBinding) {
                        deleteAccountPerson.isEnabled = true; editAccountPersonButton.isEnabled =
                        true
                    }
                }
            })
        }

        fun onDeleteButtonClick(){
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onDeleteClick(
                    listOfAccounts[adapterPosition].accountNo,
                    adapterPosition
                )
            }
        }

        fun onEditButtonClick(){
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listener.onEditClick(listOfAccounts[adapterPosition].accountNo, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AccountViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.recycler_view_account_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        /** To bind each item in the list, an object of it passed to the recycler_view_item_binding layout file and the databinding takes care
        of setting the data into the textViews. (See the layout to see how that is done, e.g 'android:text="@{account.name}"') */
        holder.recyclerViewItemBinding.bankAccount = listOfAccounts[position]
    }

    override fun getItemCount() = listOfAccounts.size


    fun removeDeletedAccount(position: Int) {
        listOfAccounts = listOfAccounts.minus(listOfAccounts[position])
        notifyItemRemoved(position)
    }

    fun submitList(newList: List<Account>) {
        if (!listOfAccounts.containsAll(newList)) {
            listOfAccounts = newList
            notifyDataSetChanged() // We tell th
        }
    }

    interface OnAccountButtonClicked { /** This interface will forward clicks from the Home Fragment which extend this interface.
                                           The Fragment will be passed as the listener in this adapter's constructor, it will of course implement the functions below */
        fun onEditClick(accountNo: String, position: Int)
        fun onDeleteClick(accountNo: String, position: Int)
    }
}