package ru.landyrev.howtodraw

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_galley.*
import ru.landyrev.howtodraw.views.GalleryPage

class GalleryActivity: FragmentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_galley)

        galleryViewPager.adapter = GalleryPagerAdapter(supportFragmentManager)
    }
}

class GalleryPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 5
    }

    override fun getItem(position: Int): Fragment {
        return GalleryPage()
    }
}