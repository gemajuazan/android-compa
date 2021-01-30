package org.example.compa.utils

import android.app.AlertDialog
import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.example.compa.R

class MaterialDialog {

    companion object {

        inline fun createDialog(context: Context, dialog: MaterialAlertDialogBuilder.() -> Unit): androidx.appcompat.app.AlertDialog {
            val builder = MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered)
                .setTitle(" ")
            builder.dialog()
            return builder.create()
        }

    }
}