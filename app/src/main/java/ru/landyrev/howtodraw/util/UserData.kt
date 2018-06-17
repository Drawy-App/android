package ru.landyrev.howtodraw.util

import android.content.Context
import ru.landyrev.howtodraw.data.LevelsData

class UserData (val context: Context) {
    private val sharedPreferences = context.getSharedPreferences(
            "userData",
            Context.MODE_PRIVATE
    )

    var tutorialPassed: Boolean
        get() {
//            return !BuildConfig.DEBUG &&
//                    (sharedPreferences.getBoolean("tutorialPassed", false) || LevelsData.totalRating > 0)
            return (sharedPreferences.getBoolean("tutorialPassed", false) || LevelsData.totalRating > 0)
        }
        set(value) {
            with(sharedPreferences.edit()) {
                putBoolean("tutorialPassed", true)
                apply()
            }
        }

    var proMode: Boolean
        get() {
            return sharedPreferences.getBoolean("proMode", false)
        }
        set(value) {
            with(sharedPreferences.edit()) {
                putBoolean("proMode", value)
                apply()
            }
            LevelsData.proMode = value
            Analytics.collectUserProperties()
        }
}