package ru.landyrev.howtodraw.data;

import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Level(val difficulty: String, val level: String, val name: String, val title_en: String,
            val title_ru: String, val tutorials: List<String>) {
    val preview: Bitmap
        get() {
            return BitmapFactory.decodeFile("Images.bundle/$name/${name}_preview.png")
        }
}