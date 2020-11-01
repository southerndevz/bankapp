package com.bankapp.ui

import com.bankapp.databinding.Account

sealed class FetchAccountsState{
    data class ShowData(val accounts: Any): FetchAccountsState()
    object Idle: FetchAccountsState()
    object Loading: FetchAccountsState()
    object Error: FetchAccountsState()
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