package com.bankapp.databinding

/* This object class will be used in a databinding layouts.
The field names have to match the JSON response from the web app. Do not change the field names!! */
data class Account(
    val accountNo: String,
    val name: String,
    val acctType: String,
    val balance: Long
)