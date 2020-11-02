package com.bankapp.ui

import com.bankapp.databinding.Account

sealed class FetchAccountListState{
    data class ShowData(val accounts: Any): FetchAccountListState() /** Unlike in other states the fetch of a List of Accounts
                                                                     is still set as being of type Any,
                                                                     the final conversion is done before the list is submitted
                                                                     to the recyclerView adapter's submitList() function,
                                                                     it is the cast to being a List<Account> */
    object Idle: FetchAccountListState()
    object Loading: FetchAccountListState()
    object Error: FetchAccountListState()
}

sealed class FetchAccountState{
    data class ShowData(val accounts: Account): FetchAccountState()
    object Loading: FetchAccountState()
    object Error: FetchAccountState()
    object Idle: FetchAccountState()
}

sealed class CreateAccountState{
    data class ShowData(val accounts: Account): CreateAccountState()
    object Loading: CreateAccountState()
    object Error: CreateAccountState()
    object Idle: CreateAccountState()
}

sealed class UpdateAccountState{
    data class ShowData(val accounts: Account): UpdateAccountState()
    object Loading: UpdateAccountState()
    object Error: UpdateAccountState()
    object Idle: UpdateAccountState()
}

sealed class DeleteAccountState{
    data class ShowData(val position: Int): DeleteAccountState()
    object Loading: DeleteAccountState()
    object Error: DeleteAccountState()
    object Idle: DeleteAccountState()
}