package ru.landyrev.howtodraw

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_details.*
import ru.landyrev.howtodraw.data.Level
import ru.landyrev.howtodraw.data.LevelsData

class DetailsActivity: AppCompatActivity() {
    lateinit var level: Level

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        level = LevelsData.levelBy(intent.getStringExtra("name"))
        detailsTitleView.text = level.title_ru
    }
}