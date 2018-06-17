package ru.landyrev.howtodraw.util

import android.app.Application
import android.os.Bundle
import com.amplitude.api.Amplitude
import com.applovin.sdk.AppLovinSdk
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.google.firebase.analytics.FirebaseAnalytics
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import io.fabric.sdk.android.Fabric
import org.json.JSONObject
import ru.landyrev.howtodraw.BuildConfig
import ru.landyrev.howtodraw.data.LevelsData

object Analytics {
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics

    fun init(app: Application) {
        val context = app.applicationContext
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context)
        val yandexMetricaConfig = YandexMetricaConfig.newConfigBuilder(
                "8d6b75c1-2467-44e7-a36c-653ff1b689b4"
        ).build()

        val crashlyticsCore = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
        Fabric.with(context, Crashlytics.Builder().core(crashlyticsCore).build())

        Amplitude.getInstance().initialize(app.applicationContext, "db2abc611179520932bdce78fce99602").enableForegroundTracking(app)

        YandexMetrica.activate(context, yandexMetricaConfig)
        YandexMetrica.enableActivityAutoTracking(app)
        collectUserProperties()

        AppLovinSdk.initializeSdk(context)
    }

    fun logEvent(eventName: String, params: HashMap<String, Any>) {
        val bundle = Bundle()
        params.forEach { (k, v) ->
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
        YandexMetrica.reportEvent(eventName, params)
        logEvent(eventName, bundle)
        Amplitude.getInstance().logEvent(eventName, JSONObject(params))
    }

    fun logEvent(eventName: String, params: Bundle) {
        mFirebaseAnalytics.logEvent(eventName, params)
    }

    fun collectUserProperties() {
        val userProperties = hashMapOf(
                "unlockedStages" to LevelsData.unlockedStagesCount.toString(),
                "totalStages" to LevelsData.stagesCount.toString(),
                "isPro" to LevelsData.proMode.toString()
        )
        Amplitude.getInstance().clearUserProperties()
        Amplitude.getInstance().setUserProperties(JSONObject(userProperties))
        userProperties.forEach { (t, u) ->
            mFirebaseAnalytics.setUserProperty(t, u)
        }

    }
}