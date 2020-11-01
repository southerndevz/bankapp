package com.bankapp.databinding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bankapp.R
import com.bankapp.databinding.AccountRecyclerViewAdapter.AccountViewHolder
import com.bankapp.ui.DeleteAccountState
import com.bankapp.ui.MainActivityViewModel

class AccountRecyclerViewAdapter(
    private var listOfAccounts: List<Account>,
    private val listener: OnAccountButtonClicked,
    private val viewLifecycleOwner: LifecycleOwner,
    private val mainActivityViewModel: MainActivityViewModel
) : RecyclerView.Adapter<AccountViewHolder>() {


    inner class AccountViewHolder(val recyclerViewItemBinding: RecyclerViewAccountItemBinding) :
        RecyclerView.ViewHolder(recyclerViewItemBinding.root) {
        init {
            mainActivityViewModel.deleteAccountState.observe(viewLifecycleOwner, { state ->
                when(state){
                    is DeleteAccountState.Loading -> with(recyclerViewItemBinding){deleteAccountPerson.isEnabled = false; editAccountPersonButton.isEnabled = false}
                    else -> with(recyclerViewItemBinding){deleteAccountPerson.isEnabled = true; editAccountPersonButton.isEnabled = true }
                }
            })
            recyclerViewItemBinding.editAccountPersonButton.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onEditClick(listOfAccounts[adapterPosition].accountNo, adapterPosition)
                }
            }
            recyclerViewItemBinding.deleteAccountPerson.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(
                        listOfAccounts[adapterPosition].accountNo,
                        adapterPosition
                    )
                }
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
        holder.recyclerViewItemBinding.bankAccount = listOfAccounts[position]
    }

    override fun getItemCount() = listOfAccounts.size

    fun removeDeletedAccount(position: Int) {
        listOfAccounts = listOfAccounts.minus(listOfAccounts[position])
        notifyItemRemoved(position)
    }

    fun submitList(newList: List<Account>){
        if (!listOfAccounts.containsAll(newList)) {
            listOfAccounts = newList
            notifyDataSetChanged()
        }
    }

    interface OnAccountButtonClicked {
        fun onEditClick(accountNo: String, position: Int)
        fun onDeleteClick(accountNo: String, position: Int)
    }
}