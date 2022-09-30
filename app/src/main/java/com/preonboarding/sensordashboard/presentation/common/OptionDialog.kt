package com.preonboarding.sensordashboard.presentation.common

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.preonboarding.sensordashboard.databinding.DialogOptionBinding

class OptionDialog(
    private val context: Context,
    private val playClicked: () -> Unit,
    private val deleteClicked: () -> Unit
) {

    private val builder = AlertDialog.Builder(context)

    fun showDialog() {
        val inflater = LayoutInflater.from(context)
        val binding = DialogOptionBinding.inflate(inflater, null, false)

        builder.setView(binding.root)

        val dialog = builder.create()

        binding.btnPlay.setOnClickListener {
            playClicked.invoke()
            dialog.dismiss()
        }

        binding.btnDelete.setOnClickListener {
            deleteClicked.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }
}