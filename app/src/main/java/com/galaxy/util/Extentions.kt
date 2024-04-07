package com.galaxy.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.showConfirmationDialog(
    title: String,
    message: String,
    positiveText: String = "Yes",
    negativeText: String = "Cancel",
    onPositiveClick: (DialogInterface, Int) -> Unit,
    onNegativeClick: () -> Unit = {} // Empty lambda
) {
    val builder = AlertDialog.Builder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveText, onPositiveClick)
        .setNegativeButton(negativeText) { dialog, _ ->
            onNegativeClick() // Call the lambda expression for negative click
            dialog.dismiss() // Dismiss the dialog after the action
        }
    builder.create().show()
}



