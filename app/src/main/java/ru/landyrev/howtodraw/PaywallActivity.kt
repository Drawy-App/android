package ru.landyrev.howtodraw

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.afollestad.materialdialogs.MaterialDialog
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.Constants
import com.anjlab.android.iab.v3.SkuDetails
import com.anjlab.android.iab.v3.TransactionDetails
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_paywall.*
import ru.landyrev.howtodraw.util.Analytics
import ru.landyrev.howtodraw.util.Background
import ru.landyrev.howtodraw.util.UserData

class PaywallActivity: AppCompatActivity(), BillingProcessor.IBillingHandler {

    private val BILLING_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiwxXLbVBUGywdEG6a4zch4+15oS/0H2qsIJPNeU2gRohvW+zNakQblHGZUKoC5CP2kzULKJoXUsBy04MmVZWKdaaT1i1gnzNsm4uIbQmxIj5nkW3iHUR9JmGW+FNyU4XKtvwl3ayHlvf3GVHKIbLb6ucZ2y5JDEQ3OoRG7Zaj8dwFo3H7IEC0mgX6FhnEJrzU27MyD+Z0itYiQ6T4RXV29SnvM31i0Cbbui5Nc7Qi6sTe50Zs6EB5daFU+s2eUiVJru5Pij6Quth4EoiCNsWq2d6XAeuLSVfQwHJrTDgtAz64F02yEtt3QK+pbjujJo4bYzbDTtSxH5UuRAnyWFULwIDAQAB"
    val productId = "pro_mode"
    private var bp: BillingProcessor? = null
    private lateinit var userData: UserData
    private val TAG = "PAYWALL"
    var productDetails: SkuDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paywall)

        body.background = Background.background
        userData = UserData(this)

        bp = BillingProcessor(this, BILLING_KEY, this)
        payButton.setOnClickListener { pay() }
        restoreButton.setOnClickListener { restore() }

        Analytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, hashMapOf())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (bp != null) {
            if (!bp!!.handleActivityResult(requestCode, resultCode, data)) {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onDestroy() {
        if (bp != null) {
            bp!!.release()
        }
        super.onDestroy()
    }

    override fun onBillingInitialized() {
        productDetails = bp!!.getPurchaseListingDetails(productId)
        if (productDetails == null) {
            this.onBillingError(com.anjlab.android.iab.v3.Constants.BILLING_ERROR_OTHER_ERROR, null)
        } else {
            payButton.text = this.getString(R.string.PAY_BUTTON_W_PRICE, productDetails!!.priceText)
            payButton.isEnabled = true
            payButton.alpha = 1.0f
            restoreButton.alpha = 1.0f
            restoreButton.isEnabled = true
        }
    }

    private fun pay() {
        Analytics.logEvent(
                FirebaseAnalytics.Event.CHECKOUT_PROGRESS,
                hashMapOf<String, Any>(
                        FirebaseAnalytics.Param.CHECKOUT_STEP to 0
                )
        )
        bp!!.purchase(this, productId)
    }

    private fun restore() {
        bp!!.loadOwnedPurchasesFromGoogle()
        if (bp!!.isPurchased(productId)) {
            this.onProductPurchased(productId, null)
        } else {
            MaterialDialog.Builder(this)
                    .content(getString(R.string.RESTORE_FAILED_BODY))
                    .positiveText(R.string.CLOSE_BUTTON)
                    .show()
        }
    }

    override fun onPurchaseHistoryRestored() {
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        val params: HashMap<String, Any> = if (productDetails == null) hashMapOf() else hashMapOf(
                FirebaseAnalytics.Param.CURRENCY to productDetails!!.currency,
                FirebaseAnalytics.Param.PRICE to productDetails!!.priceValue,
                FirebaseAnalytics.Param.TRANSACTION_ID to if (details == null) 0 else details.purchaseInfo.purchaseData.orderId
        )
        Analytics.logEvent(
                FirebaseAnalytics.Event.ECOMMERCE_PURCHASE,
                params
        )
        if (productId == this.productId) {
            userData.proMode = true
            MaterialDialog.Builder(this)
                    .title(R.string.PAY_SUCCESS_TITLE)
                    .content(R.string.PAY_SUCCESS_BODY)
                    .positiveText(R.string.CONTINUE_BUTTON)
                    .onPositive { _, _ ->
                        Handler(this.mainLooper).post {
                            this.setResult(99)
                            this.finish()
                        }
                    }
                    .show()

            if (BuildConfig.DEBUG) {
                bp!!.consumePurchase(productId)
                Constants.BILLING_ERROR_BIND_PLAY_STORE_FAILED
            }
        }
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        MaterialDialog.Builder(this)
                .title(R.string.BILLING_ERROR_POPUP_TITLE)
                .content(
                        if (error != null) { error.localizedMessage }
                        else { this.getString(R.string.BILLING_ERROR_POPUP_BODY)}
                )
                .positiveText(getString(R.string.CLOSE_BUTTON))
                .show()
        Log.w(TAG, "OnBillingError " + errorCode.toString())
    }
}