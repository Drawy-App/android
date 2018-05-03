package ru.landyrev.howtodraw.views

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.view.View
import kotlinx.android.synthetic.main.image_card.view.*
import ru.landyrev.howtodraw.R

class ImageCardView(context: Context): ConstraintLayout(context) {
    var image: Bitmap? = null

    init {
        View.inflate(context, R.layout.image_card, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        imageCardPreview.setImageBitmap(image!!)
        imageCardPreview.maxHeight = this.width - 16 * 2
    }

    override fun setOnClickListener(l: OnClickListener?) {
        Handler().postDelayed({
            previewCard.setOnClickListener(l)
        }, 150)
    }
}