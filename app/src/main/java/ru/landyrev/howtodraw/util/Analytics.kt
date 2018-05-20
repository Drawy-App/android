package ru.landyrev.howtodraw.util

import android.app.Application
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import ru.landyrev.howtodraw.data.LevelsData

object Analytics {
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    fun init(app: Application) {
        val context = app.applicationContext
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val yandexMetricaConfig = YandexMetricaConfig.newConfigBuilder(
                "8d6b75c1-2467-44e7-a36c-653ff1b689b4"
        ).build()
        YandexMetrica.activate(context, yandexMetricaConfig)
        YandexMetrica.enableActivityAutoTracking(app)
        collectUserProperties()
    }

    fun logEvent(eventName: String, params: HashMap<String, Any>) {
        val bundle = Bundle()
        params.forEach { k, v ->
            run {
                if (v is String) {
                    bundle.putString(k, v)
                }
                if (v is Int) {
                    bundle.putInt(k, v)
                }
                if (v is Float) {
                    bundle.putFloat(k, v)
                }
            }
        }
        logEvent(eventName, bundle)
    }

    fun logEvent(eventName: String, params: Bundle) {
        mFirebaseAnalytics.logEvent(eventName, params)
    }

    fun collectUserProperties() {
        mFirebaseAnalytics.setUserProperty(
                "unlockedStages",
                LevelsData.unlockedStagesCount.toString()
        )
        mFirebaseAnalytics.setUserProperty(
                "totalStages",
                LevelsData.stagesCount.toString()
        )
    }
}