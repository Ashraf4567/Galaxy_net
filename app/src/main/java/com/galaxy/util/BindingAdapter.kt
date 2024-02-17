package com.galaxy.util

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("app:error")
fun bindErrorOnTextInputLayout(textInputLayout: TextInputLayout, errorMessage: String?) {

    textInputLayout.error = errorMessage
}