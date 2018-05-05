package ru.landyrev.howtodraw.data;

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.CardView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import ru.landyrev.howtodraw.DetailsActivity
import ru.landyrev.howtodraw.GalleryActivity
import ru.landyrev.howtodraw.views.ImageCardView

class Level(val difficulty: Int, val level: Int, val name: String, val title_en: String,
            val title_ru: String, val tutorials: List<String>, val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            "levels",
            Context.MODE_PRIVATE
    )

    val rating: Int
        get() {
            return sharedPreferences.getInt(name, 0)
        }

    fun solve() {
        with(sharedPreferences.edit()) {
            putInt(name, difficulty)
            apply()
        }
    }

    fun getPreview(context: Context): Bitmap {
        return BitmapFactory.decodeStream(
                context.assets.open("Images.bundle/$name/${name}_preview.png")
        )
    }

    inner class ImageAdapter(private val context: Context): BaseAdapter() {
        override fun getCount(): Int {
            return tutorials.size
        }

        override fun getItemId(p0: Int): Long {
            return 0L
        }

        override fun getView(number: Int, convertView: View?, p2: ViewGroup?): View {
            val cardView: ImageCardView

            if (convertView == null) {
                cardView = ImageCardView(this.context)
                cardView.image = BitmapFactory.decodeStream(
                        this.context.assets.open(
                                tutorials[number]
                        )
                )
                cardView.setOnClickListener {
                    openGallery(number)
                }
            } else {
                cardView = convertView as ImageCardView
            }
            return cardView
        }

        override fun getItem(p0: Int): Any? {
            return null
        }

        private fun openGallery(pageNumber: Int) {
            val intent = Intent(context, GalleryActivity::class.java).apply {
                putExtra("level_name", name)
                putExtra("position", pageNumber)
            }
            context.startActivity(intent)
        }

    }
}