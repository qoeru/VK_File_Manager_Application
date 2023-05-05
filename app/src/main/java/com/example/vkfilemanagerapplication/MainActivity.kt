package com.example.vkfilemanagerapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.vkfilemanagerapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(FilesFragment())

        binding.bottomNavigationBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.files -> {
                    replaceFragment(FilesFragment())
                }
                R.id.home -> {
                    replaceFragment(HomeFragment())
                }
                R.id.externalStorage -> {
                    replaceFragment(ExternalStorageFragment())
                }
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

}