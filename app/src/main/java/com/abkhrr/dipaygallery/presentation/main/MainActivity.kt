package com.abkhrr.dipaygallery.presentation.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.abkhrr.dipaygallery.BR
import com.abkhrr.dipaygallery.R
import com.abkhrr.dipaygallery.databinding.ActivityMainBinding
import com.abkhrr.dipaygallery.factory.ViewModelFactory
import com.abkhrr.dipaygallery.presentation.base.BaseActivity
import dagger.android.HasAndroidInjector
import java.util.*
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), HasAndroidInjector {

    @Inject
    lateinit var factory: ViewModelFactory

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.activity_main

    private var permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )

    override val viewModel: MainViewModel
        get() = ViewModelProvider(this, factory).get(MainViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
    }

    private fun checkPermissions(): Boolean {
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(this@MainActivity, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                listPermissionsNeeded.toTypedArray(),
                    MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        result: IntArray
    ) {
        when (requestCode) {
            MULTIPLE_PERMISSIONS -> {
                if (result.isNotEmpty()) {
                    var permissionsDenied = ""
                    for (per in permissions) {
                        if (result[0] == PackageManager.PERMISSION_DENIED) {
                            permissionsDenied += """
                            
                            $per
                            """.trimIndent()
                        }
                    }
                }
                return
            }
        }
    }

    companion object {
        private const val MULTIPLE_PERMISSIONS = 10
    }

}