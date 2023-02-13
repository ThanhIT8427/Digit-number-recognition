package com.example.tienanh.Activity

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.tienanh.R
import com.example.tienanh.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initPermission()
        initNav()
    }

    fun initPermission(){
        val permission =
            ActivityCompat.checkSelfPermission(binding.root.context,Manifest.permission.READ_EXTERNAL_STORAGE)   //checkSelfPermission(binding.root.context, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                binding.root.context as Activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }

    }
    fun initNav(){
        getFragment(Gradle())
        onClickNav()
    }
    fun onClickNav(){
        binding.bottomNav.setOnNavigationItemSelectedListener {
            item -> when(item.itemId) {
                R.id.draw -> {
                    getFragment(Draw())
                    return@setOnNavigationItemSelectedListener true
                }
            R.id.gradle -> {
                getFragment(Gradle())
                return@setOnNavigationItemSelectedListener true
            }
            }
            false
        }
    }
    private fun getFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentMain,fragment)
        fragmentTransaction.commit()
    }
}