package com.pereira.baudoapp.utils

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.pereira.baudoapp.FixedVideoActivity
import com.pereira.baudoapp.R
import com.pereira.baudoapp.databinding.ActivityUserBinding
import com.pereira.baudoapp.databinding.SettingsDialogBinding

class SettingsDialog(
    private val onSubmitClickListener: () -> Unit,
    private val onBaudoVideoLinkListener: () -> Unit
): DialogFragment() {
    private lateinit var binding: SettingsDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)
        binding = SettingsDialogBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        setup()

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.TOP)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    private fun setup() {
        binding.settingsPopUpDisconnect.setOnClickListener {
            onSubmitClickListener.invoke()
        }
        binding.settingsPopUpClose.setOnClickListener {
            dialog?.dismiss()
        }
        binding.baudoVideoLink.setOnClickListener {
            dialog?.dismiss()
            onBaudoVideoLinkListener.invoke()
        }
    }
}