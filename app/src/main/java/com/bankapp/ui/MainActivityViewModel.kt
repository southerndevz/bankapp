package com.bankapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bankapp.databinding.Account
import com.bankapp.networking.BankWebService
import com.bankapp.networking.ResultState
import com.bankapp.networking.networkOperation
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {
    private val webService: BankWebService = BankWebService.invoke()
    private val _fetchAccountsState: MutableLiveData<FetchAccountsState> = MutableLiveData()
    val fetchAccountsState: LiveData<FetchAccountsState> get() = _fetchAccountsState
    private val _fetchAccountState: MutableLiveData<FetchAccountState> = MutableLiveData()
    val fetchAccountState: LiveData<FetchAccountState> get() = _fetchAccountState
    private val _createAccountsState: MutableLiveData<CreateAccountState> = MutableLiveData()
    val createAccountsState: LiveData<CreateAccountState> get() = _createAccountsState
    private val _updateAccountState: MutableLiveData<UpdateAccountState> = MutableLiveData()
    val updateAccountState: LiveData<UpdateAccountState> get() = _updateAccountState
    private val _deleteAccountState: MutableLiveData<DeleteAccountState> = MutableLiveData()
    val deleteAccountState: LiveData<DeleteAccountState> get() = _deleteAccountState

    fun getAllBankAccounts() {
        viewModelScope.launch {
            _fetchAccountsState.postValue(FetchAccountsState.Loading)
            when (val accounts = networkOperation { webService.getAccounts() }) {
                is ResultState.Error -> _fetchAccountsState.postValue(FetchAccountsState.Error)
                is ResultState.Success<*> -> _fetchAccountsState.postValue(
                    FetchAccountsState.ShowData(accounts.data)
                )
            }
        }
    }

    fun getBankAccount(accountNumber: String) {
        viewModelScope.launch {
            _fetchAccountState.postValue(FetchAccountState.Loading)
            when (val account = networkOperation { webService.getAccount(accountNumber) }) {
                is ResultState.Error -> _fetchAccountState.postValue(FetchAccountState.Error)
                is ResultState.Success<*> -> _fetchAccountState.postValue(
                    FetchAccountState.ShowData(
                        account.data as Account
                    )
                )
            }
        }
    }

    fun createBankAccount(account: Account) {
        viewModelScope.launch {
            _createAccountsState.postValue(CreateAccountState.Loading)
            when (val response = networkOperation { webService.createAccount(account) }) {
                is ResultState.Error -> _createAccountsState.postValue(CreateAccountState.Error)
                is ResultState.Success<*> -> _createAccountsState.postValue(
                    CreateAccountState.ShowData(
                        response.data as Account
                    )
                )
            }
        }
    }

    fun updateBankAccount(account: Account) {
        _updateAccountState.postValue(UpdateAccountState.Loading)
        viewModelScope.launch {
            when (val response = networkOperation { webService.updateAccount(account) }) {
                is ResultState.Error -> _updateAccountState.postValue(UpdateAccountState.Error)
                is ResultState.Success<*> -> _updateAccountState.postValue(
                    UpdateAccountState.ShowData(
                        response.data as Account
                    )
                )
            }
        }
    }

    fun delete(accountNumber: String, position: Int) {
        viewModelScope.launch {
            _deleteAccountState.postValue(DeleteAccountState.Loading)
            when (networkOperation { webService.deleteAccount(accountNumber) }) {
                is ResultState.Error -> _deleteAccountState.postValue(DeleteAccountState.Error)
                is ResultState.Success<*> -> _deleteAccountState.postValue(
                    DeleteAccountState.ShowData(position)
                )
            }
        }
    }

    fun setFetchAccountsState(state: FetchAccountsState){
        _fetchAccountsState.postValue(state)
    }

    fun setFetchAccountState(state: FetchAccountState){
        _fetchAccountState.postValue(state)
    }

    fun setCreateAccountsState(state: CreateAccountState){
        _createAccountsState.postValue(state)
    }

    fun setUpdateAccountState(state: UpdateAccountState)
    {
        _updateAccountState.postValue(state)
    }
    fun setDeleteAccountState(state: DeleteAccountState){
        _deleteAccountState.postValue(state)
    }
}