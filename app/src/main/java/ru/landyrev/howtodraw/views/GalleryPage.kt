package ru.landyrev.howtodraw.views

import android.graphics.Bitmap
import android.media.Image
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.gallery_page.*
import ru.landyrev.howtodraw.R

class GalleryPage: Fragment() {
    private lateinit var imageView: ImageView
    var image: Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.gallery_page, container, false) as ViewGroup
        imageView = rootView.findViewById(R.id.galleryPageImageView)
        imageView.setImageBitmap(image)
        return rootView
    }
}