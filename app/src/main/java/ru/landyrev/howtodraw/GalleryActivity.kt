package ru.landyrev.howtodraw

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.widget.Button
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

        nextButton.setOnClickListener {
            galleryViewPager.setCurrentItem(galleryViewPager.currentItem + 1, true)
        }

        recognizeButton.setOnClickListener {
            openCamera()
        }

        galleryToolbar.title = level.title
        galleryToolbar.setNavigationOnClickListener {
            this.finish()
        }
        galleryViewPager.adapter = GalleryPagerAdapter(supportFragmentManager, level, this)
        galleryViewPager.addOnPageChangeListener(OnPageChange())
        galleryViewPager.setCurrentItem(intent.getIntExtra("position", 0), false)
    }

    inner class OnPageChange: ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int) {
//            galleryToolbar.title = "${level.title} (${position + 1} из ${level.tutorials.size})"
            if (position + 1 == level.tutorials.size) {
                nextButton.visibility = Button.INVISIBLE
                recognizeButton.visibility = Button.VISIBLE
            } else {
                nextButton.visibility = Button.VISIBLE
                recognizeButton.visibility = Button.INVISIBLE
            }
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageScrollStateChanged(state: Int) {}

    }

    private fun openCamera() {
        val intent = Intent(this, CameraActivity::class.java).apply {
            putExtra("level_name", level.name)
        }
        this.startActivity(intent)
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