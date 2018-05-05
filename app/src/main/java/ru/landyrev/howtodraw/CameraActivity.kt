package ru.landyrev.howtodraw

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import ru.landyrev.howtodraw.data.Level
import ru.landyrev.howtodraw.data.LevelsData
import ru.landyrev.howtodraw.util.Camera
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : Activity() {

    private val CAMERA_PERMISSION: Int = 0
    private lateinit var camera: Camera
    private lateinit var level: Level


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        level = LevelsData.levelBy(intent.getStringExtra("level_name"))
        setContentView(R.layout.activity_camera)
        checkPermissions()
        camera = Camera(this, findViewById(R.id.previewView), level)

        cameraToolbar.setNavigationOnClickListener {
            this.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        camera.start()
    }

    override fun onPause() {
        super.onPause()
        camera.stop()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        print("granted?")
    }
}
