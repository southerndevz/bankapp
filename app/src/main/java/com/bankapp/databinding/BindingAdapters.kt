package com.bankapp.databinding

import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bankapp.R
import com.bankapp.ui.SharedViewModel

@BindingAdapter("onTextChangedValidation")
fun EditText.bindTextWatcher(textWatcher: TextWatcher) {
    addTextChangedListener(textWatcher)
}

@BindingAdapter("onTextChangedValidation")
fun TextView.bindTextWatcher(textWatcher: TextWatcher) {
    addTextChangedListener(textWatcher)
}

@BindingAdapter("accountsMenuAdapter")
fun AutoCompleteTextView.bindAdapter(enable: Boolean) {
    setAdapter(
        ArrayAdapter(
            context, android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.account_type_array)
        )
    )
}

@BindingAdapter("onFocusChangedListener")
fun AutoCompleteTextView.bindFocusChangeListener(enable: Boolean) {
    onFocusChangeListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(p0: View?, focused: Boolean) {
            runCatching {
                if (focused) {
                    if (!text.isNullOrBlank()) {
                        val editable = text
                        setText("", true)
                        showDropDown()
                        setText(editable, false)
                    }
                }
            }
        }
    }
}

@BindingAdapter("onFlingListener")
fun RecyclerView.bindOnFlingListener(sharedViewModel: SharedViewModel) {
   onFlingListener = object : RecyclerView.OnFlingListener() {
        override fun onFling(velocityX: Int, velocityY: Int): Boolean {
            if (velocityY > velocityX) {
                /** This listener detects if you fling to navigate to the bottom and that calls the viewModel
                to refresh the list */
                sharedViewModel.getAllBankAccounts()
            }
            return false
        }
    }
}

