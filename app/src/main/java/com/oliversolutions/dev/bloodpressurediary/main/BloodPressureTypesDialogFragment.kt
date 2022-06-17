package com.oliversolutions.dev.bloodpressurediary.main

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.oliversolutions.dev.bloodpressurediary.R

class BloodPressureTypesDialogFragment : DialogFragment() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setView(R.layout.dialog_fragment_high_pressure_types)
            .setPositiveButton("Ok") { _,_ -> }
            .create()

    companion object {
        const val TAG = "HighPressureTypesDialogFragment"
    }
}
