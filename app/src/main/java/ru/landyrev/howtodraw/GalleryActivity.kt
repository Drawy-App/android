package ru.landyrev.howtodraw

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_galley.*
import ru.landyrev.howtodraw.data.Level
import ru.landyrev.howtodraw.data.LevelsData
import ru.landyrev.howtodraw.views.GalleryPage

class GalleryActivity: FragmentActivity()  {
    private lateinit var level: Level

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_galley)

        level = LevelsData.levelBy(
                intent.getStringExtra("level_name")
        )

        galleryToolbar.title = level.title_ru
        galleryToolbar.setNavigationOnClickListener {
            this.finish()
        }
        galleryViewPager.adapter = GalleryPagerAdapter(supportFragmentManager, level, this)
    }
}

class GalleryPagerAdapter(
        fm: FragmentManager,
        private val level: Level,
        private val context: Context
): FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return level.tutorials.size
    }

    override fun getItem(position: Int): Fragment {
        val page = GalleryPage()
        page.image = BitmapFactory.decodeStream(
                context.assets.open(level.tutorials[position])
        )
        return page
    }
}