package ru.landyrev.howtodraw

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import kotlinx.android.synthetic.main.activity_camera.*
import ru.landyrev.howtodraw.data.Level
import ru.landyrev.howtodraw.data.LevelsData
import ru.landyrev.howtodraw.util.Analytics
import ru.landyrev.howtodraw.util.Background
import ru.landyrev.howtodraw.util.Camera

class CameraActivity : Activity() {

    private val CAMERA_PERMISSION: Int = 0
    private lateinit var camera: Camera
    private lateinit var level: Level
    private lateinit var mainHandler: Handler
    private var solved = false

    private lateinit var viewParams: HashMap<String, Any>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainHandler = Handler(this.mainLooper)
        level = LevelsData.levelBy(intent.getStringExtra("level_name"))
        setContentView(R.layout.activity_camera)
        cameraLayout.background = Background.background
        camera = Camera(this, findViewById(R.id.previewView), level).apply {
            onSuccess = {
                onSuccess()
                Analytics.logEvent("success_recognized", viewParams)
            }
            onCapture = {
                mainHandler.post {
                    cameraHint.text = getString(R.string.found_something)
                    cameraFrame.background = ContextCompat.getDrawable(
                            this@CameraActivity,
                            R.drawable.camera_success_background
                    )
                }
                Analytics.logEvent("found_something", viewParams)
            }
            onLost = {
                mainHandler.post { cameraHint.text = "" }
                cameraFrame.setBackgroundColor(Color.TRANSPARENT)
                Analytics.logEvent("lost_something", viewParams)
            }
        }

        cameraToolbar.setNavigationOnClickListener {
            this.finish()
        }

        viewParams = hashMapOf(
                "name" to level.name,
                "difficulty" to level.difficulty,
                "rating" to level.rating
        )
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
        cameraStart()
    }

    override fun onPause() {
        super.onPause()
        camera.stop()
    }

    private fun cameraStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION)
            Analytics.logEvent("ask_for_permission", viewParams)
        } else {
            camera.start()
            Analytics.logEvent("start_recognizing", viewParams)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    camera.start()
                    Analytics.logEvent("permissions_granted", viewParams)
                } else {
                    Analytics.logEvent("permissions_not_granted", viewParams)
                }
            }
        }
        print("granted?")
    }

}
