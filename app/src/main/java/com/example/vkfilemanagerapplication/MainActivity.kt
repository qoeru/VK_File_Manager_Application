package com.example.vkfilemanagerapplication

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.vkfilemanagerapplication.databinding.ActivityMainBinding
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var currentFilesCheckSum: MutableMap<File, String>

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Пожалуйста, предоставьте разрешение!", Toast.LENGTH_LONG).show()
            }
        }

    private fun generateCheckSumForFiles(): MutableMap<File, String> {
        val checkSums = mutableMapOf<File, String>()
        File(Environment.getExternalStorageDirectory().absolutePath).walk().forEach {
            if(!it.isDirectory)
            {
                val checkSum = generateCheckSumForASingleFile(it)
                checkSums[it] = checkSum
            }
        }
        return checkSums
    }

    private fun generateCheckSumForASingleFile(file: File): String {
        val md = MessageDigest.getInstance("MD5")
        return try {
            val fis = FileInputStream(file)
            val bytes = md.digest()
            fis.close()
            bytes.toString()
        } catch (_: Exception) {
            ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        if(!checkPermission())
        {
            requestPermission()
        } else {
            prefs = getPreferences(Context.MODE_PRIVATE)
            val previousFiles = prefs.getStringSet(getString(R.string.last_updated_file_list), setOf())

            currentFilesCheckSum = generateCheckSumForFiles()

            val changedFiles = ArrayList<File>()

            if(previousFiles.isNullOrEmpty())
            {
                // первый запуск приложения
                for(checkSum in currentFilesCheckSum)
                {
                    changedFiles.add(checkSum.key)
                }
            } else {
                for(checkSum in currentFilesCheckSum)
                {
                    if(!previousFiles.contains(checkSum.value))
                    {
                        changedFiles.add(checkSum.key)
                    }
                }
            }

            val storageFragment = StorageFragment()
            storageFragment.setChangedFiles(changedFiles)
            replaceFragment(storageFragment)
        }
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        prefs = getPreferences(Context.MODE_PRIVATE)
        val stringSet = currentFilesCheckSum.values.toSet()
        with(prefs.edit())
        {
            putStringSet(getString(R.string.last_updated_file_list), stringSet)
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
    private fun checkPermission(): Boolean {
        if (
            ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    private fun requestPermission(){
        requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }


}