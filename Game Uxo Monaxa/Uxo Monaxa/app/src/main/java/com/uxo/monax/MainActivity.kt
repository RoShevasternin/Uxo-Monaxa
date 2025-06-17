package com.uxo.monax

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowMetrics
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.uxo.monax.databinding.ActivityMainBinding
import com.uxo.monax.util.OneTime
import com.uxo.monax.util.PlayGamesHelper
import com.uxo.monax.util.billingManager.BillingManager
import com.uxo.monax.util.billingManager.InAppProduct
import com.uxo.monax.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(), AndroidFragmentApplication.Callbacks {

    companion object {
        var statusBarHeight = 0
        var navBarHeight    = 0

        var isBuyedNoAdsForever = false
            private set
        var isBuyedSub1Month = false
            private set

    }

    private val coroutine  = CoroutineScope(Dispatchers.Default)
    private val onceExit   = OneTime()

    private val onceSystemBarHeight = OneTime()

    private lateinit var binding : ActivityMainBinding

    val windowInsetsController by lazy { WindowCompat.getInsetsController(window, window.decorView) }

    // Play Games Services
    lateinit var playGamesHelper: PlayGamesHelper

    // Billing
    lateinit var billingManager: BillingManager

    // Ads
    private val adSize: AdSize
        get() {
            val displayMetrics = resources.displayMetrics
            val adWidthPixels =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val windowMetrics: WindowMetrics = this.windowManager.currentWindowMetrics
                    windowMetrics.bounds.width()
                } else {
                    displayMetrics.widthPixels
                }
            val density = displayMetrics.density
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }
    private val adView by lazy { AdView(this) }
    private var isShowAds = true

    // Block
    var blockBuyNoAdsForever = {}
    var blockBuySub1Month    = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        initialize()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            onceSystemBarHeight.use {
                statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

                // hide Status or Nav bar (після встановлення їх розмірів)
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

            WindowInsetsCompat.CONSUMED
        }

        playGamesHelper = PlayGamesHelper(this)

        handleBillingPurchase()
        initializeAdMob()
    }

    override fun exit() {
        onceExit.use {
            log("exit")
            billingManager.close()

            coroutine.launch(Dispatchers.Main) {
                finishAndRemoveTask()
                delay(100)
                exitProcess(0)
            }
        }
    }

    private fun initialize() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    // Ads -----------------------------------------------------------------------------------------

    private fun initializeAdMob() {
        coroutine.launch(Dispatchers.Main) {
            delay(5_000)
            if (isShowAds.not()) return@launch

            MobileAds.initialize(this@MainActivity)
            addBannerAd()
        }
    }

    private fun addBannerAd() {
        adView.adUnitId = getString(R.string.ad_banner_id)
        adView.setAdSize(adSize)
        adView.id = View.generateViewId()

        binding.root.addView(adView)

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.root)

        constraintSet.connect(adView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        constraintSet.connect(adView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(adView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)

        constraintSet.applyTo(binding.root)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    // Billing ------------------------------------------------------------------------------------

    fun buyProduct(product: InAppProduct) {
        log("click buyProduct = ${product.name}")
        billingManager.buyProduct(this, product)
    }

    private fun handleBillingPurchase() {
        billingManager = BillingManager(this) { purchasedProduct ->
            when (purchasedProduct) {
                InAppProduct.NO_ADS_FOREVER -> handleNoAdsForever()
                InAppProduct.SUB_1_MONTH    -> handleSub1Month()
            }
        }
    }

    private fun handleNoAdsForever() {
        log("🎉 handleNoAdsForever: NO_ADS_FOREVER")
        isBuyedNoAdsForever = true
        blockBuyNoAdsForever()
        hideAds()
    }

    private fun handleSub1Month() {
        log("🎉 handleSub1Month: SUB_1_MONTH")
        isBuyedSub1Month = true
        blockBuySub1Month()
        hideAds()
    }

    private fun hideAds() {
        runOnUiThread {
            isShowAds = false
            adView.pause()
            adView.visibility = View.GONE
        }
    }

}