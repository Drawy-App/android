package ru.landyrev.howtodraw

import android.app.Application
import android.support.text.emoji.EmojiCompat
import android.support.text.emoji.FontRequestEmojiCompatConfig
import android.support.v4.provider.FontRequest
import ru.landyrev.howtodraw.data.LevelsData
import ru.landyrev.howtodraw.util.Analytics
import ru.landyrev.howtodraw.util.Background



@Suppress("unused")
class MainApp: Application() {
    override fun onCreate() {
        super.onCreate()
        LevelsData.loadImages(this)
        Background.load(this)
        Analytics.init(this)

        val fontRequest = FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                ru.landyrev.howtodraw.R.array.com_google_android_gms_fonts_certs
        )
        val config = FontRequestEmojiCompatConfig(this, fontRequest)
        EmojiCompat.init(config)
    }
}