package com.bankapp.networking

import com.bankapp.databinding.Account
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface BankWebService {

    @GET(value = "/api/accounts")
    suspend fun getAccounts(): List<Account>

    @GET(value = "/api/accounts/{accountNo}")
    suspend fun getAccount(@Path("accountNo") accountNo: String): Account

    @POST(value = "/api/accounts")
    suspend fun createAccount(@Body account: Account): Account

    @PUT(value = "/api/accounts/{accountNo}")
    suspend fun updateAccount(@Body account: Account): Account

    @DELETE(value = "/api/accounts/{accountNo}")
    suspend fun deleteAccount(@Path("accountNo") accountNo: String)

    companion object{
        operator fun invoke(): BankWebService {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .client(OkHttpClient())
                .baseUrl("https://employee-bank-app.herokuapp.com")
                .build()
                .create(BankWebService::class.java)
        }
    }

}
