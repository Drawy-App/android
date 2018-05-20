package ru.landyrev.howtodraw.util

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import ru.landyrev.howtodraw.R

object Background {
    lateinit var background: Drawable

    fun load(context: Context) {
//        val backgroundDrawable = BitmapDrawable(
//                context.resources,
//                BitmapFactory.decodeStream(
//                        context.resources.assets.open("backgrounds/retro.jpg")
//                )
//        )
        val backgroundDrawable = BitmapDrawable(
                context.resources,
                BitmapFactory.decodeResource(context.resources, R.drawable.back1)
        )
        backgroundDrawable.tileModeX = Shader.TileMode.REPEAT
        backgroundDrawable.tileModeY = Shader.TileMode.REPEAT
        background = backgroundDrawable
    }
}