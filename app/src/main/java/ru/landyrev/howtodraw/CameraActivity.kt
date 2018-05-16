package ru.landyrev.howtodraw

import android.Manifest
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.support.annotation.MainThread
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import ru.landyrev.howtodraw.data.Level
import ru.landyrev.howtodraw.data.LevelsData
import ru.landyrev.howtodraw.util.Camera
import kotlinx.android.synthetic.main.activity_camera.*
import ru.landyrev.howtodraw.util.Background

class CameraActivity : Activity() {

    private val CAMERA_PERMISSION: Int = 0
    private lateinit var camera: Camera
    private lateinit var level: Level
    private lateinit var mainHandler: Handler
    private var solved = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainHandler = Handler(this.mainLooper)
        level = LevelsData.levelBy(intent.getStringExtra("level_name"))
        setContentView(R.layout.activity_camera)
        checkPermissions()
        cameraLayout.background = Background.background
        camera = Camera(this, findViewById(R.id.previewView), level).apply {
            onSuccess = {
                onSuccess()
            }
            onCapture = {
                mainHandler.post {
                    cameraHint.text = getString(R.string.found_something)
                    cameraFrame.background = ContextCompat.getDrawable(
                            this@CameraActivity,
                            R.drawable.camera_success_background
                    )
                }
            }
            onLost = {
                mainHandler.post { cameraHint.text = "" }
                cameraFrame.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        cameraToolbar.setNavigationOnClickListener {
            this.finish()
        }
    }

    fun onSuccess() {
        level.solve()
        if (!solved) {
            solved = true
            mainHandler.post {
                val intent = Intent(this, SuccessActivity::class.java).apply {
                    putExtra("difficulty", level.difficulty)
                }
                this.startActivity(intent)
            }
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
