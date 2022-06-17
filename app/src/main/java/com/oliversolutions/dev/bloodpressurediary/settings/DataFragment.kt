package com.oliversolutions.dev.bloodpressurediary.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider.getUriForFile
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.oliversolutions.dev.bloodpressurediary.R
import com.oliversolutions.dev.bloodpressurediary.database.BloodPressureDatabase
import com.oliversolutions.dev.bloodpressurediary.databinding.FragmentDataBinding
import ir.androidexception.roomdatabasebackupandrestore.Backup
import ir.androidexception.roomdatabasebackupandrestore.Restore
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import android.Manifest
import androidx.core.content.ContextCompat.checkSelfPermission

class DataFragment : Fragment() {
    private lateinit var binding: FragmentDataBinding
    val viewModel: DataViewModel by lazy {
        ViewModelProvider(this).get(DataViewModel::class.java)
    }
    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                restoreBackup()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.grant_this_permission),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val inputStream: InputStream =
                    requireContext().contentResolver.openInputStream(result.data!!.data!!)!!
                val fileName = "hpbackup_" + System.currentTimeMillis() + ".txt"
                val newFile = File(File(requireContext().filesDir, "backups"), fileName)
                val fileOutPutStream = FileOutputStream(newFile)
                fileOutPutStream.write(inputStream.readBytes())
                fileOutPutStream.close()
                val database = BloodPressureDatabase.getInstance(requireContext())
                Restore.Init()
                    .database(database)
                    .backupFilePath(newFile.path)
                    .onWorkFinishListener { success, _ ->
                        newFile.delete()
                        if (success) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.data_successfully_restored),
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.error_ocurred),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    .execute()
            }
        }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_data, container, false
        )
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)
        binding.createBackupLinearLayout.setOnClickListener {
            createBackup()
        }

        binding.deleteDataLinearLayout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.confirm_delete_data))
                .setNegativeButton(getString(R.string.no), null)
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    viewModel.clear()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.successfully_deleted),
                        Toast.LENGTH_LONG
                    ).show()
                }.show()
        }

        binding.restoreBackupLinearLayout.setOnClickListener {
            if (checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                restoreBackup()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        return binding.root
    }

    private fun restoreBackup() {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)
        resultLauncher.launch(intent)

    }

    private fun createBackup() {
        val database = BloodPressureDatabase.getInstance(requireContext())
        val backupFilePath = File(requireContext().filesDir, "backups")
        val fileName = "hpbackup_" + System.currentTimeMillis() + ".txt"
        Backup.Init()
            .database(database)
            .path(backupFilePath.toString())
            .fileName(fileName)
            .onWorkFinishListener { success, _ ->
                if (success) {
                    val newFile = File(File(requireContext().filesDir, "backups"), fileName)
                    val contentUri = if (Build.VERSION.SDK_INT < 24) {
                        Uri.fromFile(newFile)
                    } else {
                        getUriForFile(
                            requireContext(),
                            "com.oliversolutions.dev.fileprovider",
                            newFile
                        )
                    }
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.clipData = ClipData.newRawUri("", contentUri)
                    sharingIntent.type = "text/*"
                    sharingIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
                    startActivity(Intent.createChooser(sharingIntent, getString(R.string.share)))
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_ocurred),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }.execute()
    }
}