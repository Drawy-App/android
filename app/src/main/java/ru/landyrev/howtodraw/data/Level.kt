package ru.landyrev.howtodraw.data;

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Level(val difficulty: Int, val level: Int, val name: String, val title_en: String,
            val title_ru: String, val tutorials: List<String>) {

    fun getPreview(context: Context): Bitmap {
        return BitmapFactory.decodeStream(
                context.assets.open("Images.bundle/$name/${name}_preview.png")
        )
    }
}