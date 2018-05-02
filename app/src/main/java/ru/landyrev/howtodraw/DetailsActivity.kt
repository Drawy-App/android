package ru.landyrev.howtodraw

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

        detailsBackgroundView.background = Background.background

        level = LevelsData.levelBy(intent.getStringExtra("name"))

        detailsRatingView.rating = level.difficulty
        detailsRatingView.solved = level.rating > 0

        detailsStepsCount.text = "Шагов: ${level.tutorials.size}"

        detailsPreviewTile.setImageBitmap(level.getPreview(this))
        detailsTitle.text = level.title_ru
        detailsToolbar.setNavigationOnClickListener {
            this.finish()
        }

        tutorialsGallery.adapter = level.ImageAdapter(this)
    }
}