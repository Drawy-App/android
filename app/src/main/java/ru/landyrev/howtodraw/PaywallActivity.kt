package ru.landyrev.howtodraw

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import kotlinx.android.synthetic.main.activity_paywall.*
import ru.landyrev.howtodraw.util.Background
import ru.landyrev.howtodraw.util.UserData

class PaywallActivity: AppCompatActivity(), BillingProcessor.IBillingHandler {

    private val BILLING_KEY = "badMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiwxXLbVBUGywdEG6a4zch4+15oS/0H2qsIJPNeU2gRohvW+zNakQblHGZUKoC5CP2kzULKJoXUsBy04MmVZWKdaaT1i1gnzNsm4uIbQmxIj5nkW3iHUR9JmGW+FNyU4XKtvwl3ayHlvf3GVHKIbLb6ucZ2y5JDEQ3OoRG7Zaj8dwFo3H7IEC0mgX6FhnEJrzU27MyD+Z0itYiQ6T4RXV29SnvM31i0Cbbui5Nc7Qi6sTe50Zs6EB5daFU+s2eUiVJru5Pij6Quth4EoiCNsWq2d6XAeuLSVfQwHJrTDgtAz64F02yEtt3QK+pbjujJo4bYzbDTtSxH5UuRAnyWFULwIDAQAB"
    val productId = "pro_mode"
    private var bp: BillingProcessor? = null
    private lateinit var userData: UserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paywall)

        body.background = Background.background
        userData = UserData(this)

        bp = BillingProcessor(this, BILLING_KEY, this)
        payButton.setOnClickListener { pay() }
        restoreButton.setOnClickListener { restore() }
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
        val details = bp!!.getPurchaseListingDetails(productId)
        if (details == null) {
            this.onBillingError(com.anjlab.android.iab.v3.Constants.BILLING_ERROR_OTHER_ERROR, null)
        } else {
            payButton.text = this.getString(R.string.PAY_BUTTON_W_PRICE, details.priceText)
            payButton.isEnabled = true
            payButton.alpha = 1.0f
            restoreButton.alpha = 1.0f
            restoreButton.isEnabled = true
        }
    }

    private fun pay() {
        bp!!.purchase(this, productId)
    }

    private fun restore() {
        bp!!.loadOwnedPurchasesFromGoogle()
        onPurchaseHistoryRestored()
    }

    override fun onPurchaseHistoryRestored() {
        if (bp!!.isPurchased(productId)) {
            this.onProductPurchased(productId, null)
        } else {
            MaterialDialog.Builder(this)
                    .content(getString(R.string.RESTORE_FAILED_BODY))
                    .positiveText(R.string.CLOSE_BUTTON)
                    .show()
        }
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        userData.proMode = true
        MaterialDialog.Builder(this)
                .title(R.string.PAY_SUCCESS_TITLE)
                .content(R.string.PAY_SUCCESS_BODY)
                .positiveText(R.string.CONTINUE_BUTTON)
                .onPositive { _, _ -> this.finish() }
                .show()
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
    }
}