package ru.landyrev.howtodraw

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_details.*
import ru.landyrev.howtodraw.data.Level
import ru.landyrev.howtodraw.data.LevelsData
import ru.landyrev.howtodraw.util.Background

class DetailsActivity: AppCompatActivity() {
    private lateinit var level: Level

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        detailsToolbar.navigationIcon!!.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)

        detailsBackgroundView.background = Background.background

        level = LevelsData.levelBy(intent.getStringExtra("name"))

        detailsRatingView.rating = level.difficulty
        detailsRatingView.solved = level.rating > 0

        detailsStepsCount.text = getString(R.string.steps, level.tutorials.size)

        detailsPreviewTile.setImageBitmap(level.getPreview(this))
        detailsTitle.text = level.title
        detailsToolbar.setNavigationOnClickListener {
            this.finish()
        }

        tutorialsGallery.adapter = level.ImageAdapter(this)

        detailsCaptureButton.setOnClickListener {
            openCamera()
        }
    }

    override fun onResume() {
        super.onResume()
        detailsRatingView.solved = level.rating > 0
    }

    private fun openCamera() {
        val intent = Intent(this, CameraActivity::class.java).apply {
            putExtra("level_name", level.name)
        }
        this.startActivity(intent)
    }
}