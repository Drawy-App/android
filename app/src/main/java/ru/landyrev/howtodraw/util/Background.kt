package ru.landyrev.howtodraw.util

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

object Background {
    lateinit var background: Drawable

    fun load(context: Activity) {
        val backgroundDrawable = BitmapDrawable(
                context.resources,
                BitmapFactory.decodeStream(
                        context.resources.assets.open("backgrounds/retro.jpg")
                )
        )
        backgroundDrawable.tileModeX = Shader.TileMode.REPEAT
        backgroundDrawable.tileModeY = Shader.TileMode.REPEAT
        background = backgroundDrawable
    }
}