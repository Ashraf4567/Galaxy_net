package com.galaxy.util

import android.widget.RadioGroup
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("app:error")
fun bindErrorOnTextInputLayout(textInputLayout: TextInputLayout, errorMessage: String?) {

    textInputLayout.error = errorMessage
}

@BindingAdapter("app:onCheckedChanged")
fun setOnCheckedChangeListener(
    radioGroup: RadioGroup,
    listener: RadioGroup.OnCheckedChangeListener?
) {
    radioGroup.setOnCheckedChangeListener(listener)
}
