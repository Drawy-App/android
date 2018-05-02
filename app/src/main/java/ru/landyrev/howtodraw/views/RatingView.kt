package ru.landyrev.howtodraw.views

import android.content.Context
import android.graphics.Canvas
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.widget.ImageView
import ru.landyrev.howtodraw.R


class RatingView(context: Context, attrs: AttributeSet): ConstraintLayout(context, attrs) {
    private lateinit var stars: List<ImageView>
    var rating: Int = 2

    var solved: Boolean = false

    fun redraw() {
        (0 until rating).forEach { i ->
            stars[i].setImageResource( if (solved) R.drawable.star else R.drawable.star_empty)
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