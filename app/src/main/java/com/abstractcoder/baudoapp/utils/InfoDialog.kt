package com.abstractcoder.baudoapp.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.InfoDialogBinding

class InfoDialog(
    private val messageType: String
): DialogFragment() {
    private lateinit var binding: InfoDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreate(savedInstanceState)
        binding = InfoDialogBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)

        setup()

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.TOP)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        return dialog
    }

    private fun setup() {
        val info = InfoText()
        info.assignValues(requireContext(), messageType)

        if (messageType == "comunidad" || messageType == "eventos") {
            binding.infoContainer2.visibility = LinearLayout.GONE
            binding.infoTitle.text = info.title
            binding.infoTitle.visibility = if (info.title == "") TextView.GONE else TextView.VISIBLE
            binding.infoMainText.text = info.mainText
            binding.infoMainText.visibility = if (info.mainText == "") TextView.GONE else TextView.VISIBLE
            binding.infoComplementaryText1.text = info.complementary
            binding.infoComplementaryText1.visibility = if (info.complementary == "") TextView.GONE else TextView.VISIBLE
            binding.InfoComplementaryText2.text = info.complementary2
            binding.InfoComplementaryText2.visibility = if (info.complementary2 == "") TextView.GONE else TextView.VISIBLE
        } else {
            binding.infoContainer1.visibility = LinearLayout.GONE
            binding.infoTitle1.text = info.title
            binding.infoTitle1.visibility = if (info.title == "") TextView.GONE else TextView.VISIBLE
            binding.infoMainText1.text = info.mainText
            binding.infoMainText1.visibility = if (info.mainText == "") TextView.GONE else TextView.VISIBLE
            binding.infoComplementaryText11.text = info.complementary
            binding.infoComplementaryText11.visibility = if (info.complementary == "") TextView.GONE else TextView.VISIBLE
            binding.InfoComplementaryText21.text = info.complementary2
            binding.InfoComplementaryText21.visibility = if (info.complementary2 == "") TextView.GONE else TextView.VISIBLE
        }
    }
}