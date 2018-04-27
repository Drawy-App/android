package ru.landyrev.howtodraw.data;

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory

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
}