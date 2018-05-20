package ru.landyrev.howtodraw

import android.app.Application
import ru.landyrev.howtodraw.data.LevelsData
import ru.landyrev.howtodraw.util.Analytics
import ru.landyrev.howtodraw.util.Background

class MainApp: Application() {
    override fun onCreate() {
        super.onCreate()
        LevelsData.loadImages(this)
        Background.load(this)
        Analytics.init(this)
    }
}