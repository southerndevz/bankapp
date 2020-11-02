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

class SharedViewModel : ViewModel() {

    private val webService: BankWebService = BankWebService.invoke() /** Defines & creates the Retrofit web service (Requires Android v26 */
    private val _fetchAccountListState: MutableLiveData<FetchAccountListState> = MutableLiveData()
    val fetchAccountListState: LiveData<FetchAccountListState> get() = _fetchAccountListState
    private val _fetchAccountState: MutableLiveData<FetchAccountState> = MutableLiveData()
    val fetchAccountState: LiveData<FetchAccountState> get() = _fetchAccountState
    private val _createAccountsState: MutableLiveData<CreateAccountState> = MutableLiveData()
    val createAccountsState: LiveData<CreateAccountState> get() = _createAccountsState
    private val _updateAccountState: MutableLiveData<UpdateAccountState> = MutableLiveData()
    val updateAccountState: LiveData<UpdateAccountState> get() = _updateAccountState
    private val _deleteAccountState: MutableLiveData<DeleteAccountState> = MutableLiveData()
    val deleteAccountState: LiveData<DeleteAccountState> get() = _deleteAccountState

    /** I will show this one example, all the others works similarly */
    fun getAllBankAccounts() {
        viewModelScope.launch { /** The viewModel launches a coroutine */
            _fetchAccountListState.postValue(FetchAccountListState.Loading) /** The loading state is set while it fetches data,
                                                                           see the fragment that observes this property*/
            when (val accounts = networkOperation { webService.getAccounts() }) { /** The fetch operation happens in the networkOperation { } function
                                                                                       and the result, i.e. 'val accounts' is assigned and checked */
                is ResultState.Error -> _fetchAccountListState.postValue(FetchAccountListState.Error)
                is ResultState.Success<*> -> _fetchAccountListState.postValue(
                    FetchAccountListState.ShowData(accounts.data) /** If it is successful it tells the observers of FetchAccountsState to Show the data
                                                                    which must be cast as a List<Account> (See UIStates.kt) and observers*/
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

    fun setFetchAccountsState(state: FetchAccountListState){
        _fetchAccountListState.postValue(state)
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