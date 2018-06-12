package ru.landyrev.howtodraw

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_try_it.*
import ru.landyrev.howtodraw.util.Background

class TryItActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_try_it)

        layout.background = Background.background

        label.text = this.getString(R.string.tryItLabel, "\uD83E\uDD14")

        checkButton.setOnClickListener { openCamera() }

        headerToolbar.setNavigationOnClickListener {
            this.finish()
        }
    }

    private fun openCamera() {
        val intent = Intent(this, CameraActivity::class.java).apply {
            putExtra("level_name", "generic_square")
        }
        this.startActivity(intent)
    }
}