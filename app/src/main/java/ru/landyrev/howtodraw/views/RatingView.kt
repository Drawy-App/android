package ru.landyrev.howtodraw.views

import android.content.Context
import android.graphics.Canvas
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.widget.ImageView
import ru.landyrev.howtodraw.R


class RatingView(context: Context, attrs: AttributeSet): ConstraintLayout(context, attrs) {
    private var stars: List<ImageView>? = null
    var rating: Int = 0

    var solved: Boolean = false

    fun redraw() {
        if (stars == null) {
            return
        }
        (0 until rating).forEach { i ->
            stars!![i].setImageResource( if (solved) R.drawable.star else R.drawable.star_empty)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stars!!.forEach { star ->
            star.setImageDrawable(null)
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        stars = listOf(
                findViewById(R.id.ratingStar0),
                findViewById(R.id.ratingStar1),
                findViewById(R.id.ratingStar2)
        )
    }


    init {
        inflate(context, R.layout.rating, this)
        this.setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        redraw()
    }
}