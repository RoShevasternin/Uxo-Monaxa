package com.uxo.monax.game.actors

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.uxo.monax.MainActivity
import com.uxo.monax.game.actors.button.AButton
import com.uxo.monax.game.utils.Acts
import com.uxo.monax.game.utils.TIME_ANIM_SCREEN
import com.uxo.monax.game.utils.actor.animDelay
import com.uxo.monax.game.utils.actor.animShow
import com.uxo.monax.game.utils.actor.disable
import com.uxo.monax.game.utils.advanced.AdvancedGroup
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import com.uxo.monax.game.utils.gdxGame
import com.uxo.monax.util.billingManager.InAppProduct

class ATurnOffAds(
    override val screen: AdvancedScreen,
): AdvancedGroup() {

    private val imgTurnOffAds        = Image(gdxGame.assetsAll.panel_ads_turn_off)

    private val imgArrowSubscription = Image(gdxGame.assetsAll.arrow_subscription)
    private val imgArrowPurchase     = Image(gdxGame.assetsAll.arrow_purchase)

    private val btnSubscription      = AButton(screen, AButton.Type.Subscription)
    private val btnPurchase          = AButton(screen, AButton.Type.Purchase)

    private val imgCostSubscription  = Image(gdxGame.assetsAll.cost_subscription)
    private val imgCostPurchase      = Image(gdxGame.assetsAll.cost_purchase)

    private val imgBlurSubscription  = Image(gdxGame.assetsAll.ads_btn_blur)
    private val imgBlurPurchase      = Image(gdxGame.assetsAll.ads_btn_blur)

    private val imgBuyStatus_Sub1Month    = Image(gdxGame.assetsAll.ads_check)
    private val imgBuyStatus_NoAdsForever = Image(gdxGame.assetsAll.ads_check)

    override fun addActorsOnGroup() {
        addImgTurnOffAds()
        addImgArrows()
        addImgBlurs()
        addBtnSubscription()
        addBtnPurchase()
        addImgCosts()
        addImgAdCheck()
    }

    // Actors ------------------------------------------------------------------------

    private fun addImgTurnOffAds() {
        addActor(imgTurnOffAds)
        imgTurnOffAds.color.a = 0f
        imgTurnOffAds.setBounds(0f, 124f, 424f, 210f)
    }

    private fun addImgArrows() {
        addActors(imgArrowSubscription, imgArrowPurchase)
        imgArrowSubscription.color.a = 0f
        imgArrowSubscription.setOrigin(Align.center)

        imgArrowPurchase.color.a = 0f
        imgArrowPurchase.setOrigin(Align.center)

        imgArrowSubscription.setBounds(424f, 332f, 74f, 36f)
        imgArrowPurchase.setBounds(424f, 90f, 74f, 36f)
    }

    private fun addImgBlurs() {
        addActors(imgBlurSubscription, imgBlurPurchase)
        imgBlurSubscription.color.a = 0f
        imgBlurSubscription.setOrigin(Align.center)
        imgBlurSubscription.setScale(0.5f)

        imgBlurPurchase.color.a = 0f
        imgBlurPurchase.setOrigin(Align.center)
        imgBlurPurchase.setScale(0.5f)

        imgBlurSubscription.setBounds(479f, 244f, 238f, 238f)
        imgBlurPurchase.setBounds(479f, -24f, 238f, 238f)
    }

    private fun addBtnSubscription() {
        addActor(btnSubscription)
        btnSubscription.color.a = 0f
        btnSubscription.setBounds(503f, 268f, 190f, 190f)
        btnSubscription.setOnClickListener {
            gdxGame.activity.buyProduct(InAppProduct.SUB_1_MONTH)
        }
    }

    private fun addBtnPurchase() {
        addActor(btnPurchase)
        btnPurchase.color.a = 0f
        btnPurchase.setBounds(503f, 0f, 190f, 190f)
        btnPurchase.setOnClickListener {
            gdxGame.activity.buyProduct(InAppProduct.NO_ADS_FOREVER)
        }
    }

    private fun addImgCosts() {
        addActors(imgCostSubscription, imgCostPurchase)
        imgCostSubscription.color.a = 0f
        imgCostPurchase.color.a     = 0f

        imgCostSubscription.setBounds(709f, 325f, 213f, 132f)
        imgCostPurchase.setBounds(709f, 63f, 213f, 127f)
    }

    private fun addImgAdCheck() {
        addActors(imgBuyStatus_Sub1Month, imgBuyStatus_NoAdsForever)
        imgBuyStatus_Sub1Month.color.a = 0f
        imgBuyStatus_Sub1Month.disable()
        imgBuyStatus_Sub1Month.setBounds(653f, 268f, 80f, 80f)

        imgBuyStatus_NoAdsForever.color.a = 0f
        imgBuyStatus_NoAdsForever.disable()
        imgBuyStatus_NoAdsForever.setBounds(653f, 0f, 80f, 80f)

        gdxGame.activity.apply {
            blockBuyNoAdsForever = { imgBuyStatus_NoAdsForever.color.a = 1f }
            blockBuySub1Month    = { imgBuyStatus_Sub1Month.color.a = 1f }
        }
    }

    // Anim ------------------------------------------------

    private fun animArrowsAndBlurs() {
        val scaleArrow = 0.6f
        val scaleBlur  = 0.5f

        val time       = 0.6f

        fun getActionArrows() = Actions.forever(Actions.sequence(
            Actions.scaleTo(scaleArrow, scaleArrow, time, Interpolation.sine),
            Actions.scaleTo(1f, 1f, time, Interpolation.sine),
        ))

        fun getActionBlurs() = Actions.forever(Actions.sequence(
            Actions.scaleTo(1f, 1f, time, Interpolation.sine),
            Actions.scaleTo(scaleBlur, scaleBlur, time, Interpolation.sine),
        ))

        addAction(Actions.sequence(
            Actions.run { imgArrowSubscription.addAction(getActionArrows()) },
            Actions.delay(time),
            Actions.run {
                imgBlurSubscription.addAction(getActionBlurs())
                imgArrowPurchase.addAction(getActionArrows())
            },
            Actions.delay(time),
            Actions.run {
                imgBlurPurchase.addAction(getActionBlurs())
            }
        ))
    }

    fun animShowItems(blockEnd: Runnable = Runnable {}) { this.addAction(Actions.sequence(
        Acts.run { imgTurnOffAds.animShow(TIME_ANIM_SCREEN) },
        Acts.delay(TIME_ANIM_SCREEN * 0.4f),
        Acts.run {
            imgArrowSubscription.animShow(TIME_ANIM_SCREEN)
            imgArrowPurchase.animShow(TIME_ANIM_SCREEN)
        },
        Acts.delay(TIME_ANIM_SCREEN * 0.4f),
        Acts.run {
            btnSubscription.animShow(TIME_ANIM_SCREEN)
            btnPurchase.animShow(TIME_ANIM_SCREEN)
            if (MainActivity.isBuyedSub1Month) imgBuyStatus_Sub1Month.animShow(TIME_ANIM_SCREEN)
            if (MainActivity.isBuyedNoAdsForever) imgBuyStatus_NoAdsForever.animShow(TIME_ANIM_SCREEN)
        },
        Acts.delay(TIME_ANIM_SCREEN * 0.4f),
        Acts.run {
            imgBlurSubscription.animShow(TIME_ANIM_SCREEN)
            imgBlurPurchase.animShow(TIME_ANIM_SCREEN)

            this.animDelay(TIME_ANIM_SCREEN * 0.4f) {
                imgCostSubscription.animShow(TIME_ANIM_SCREEN)
                imgCostPurchase.animShow(TIME_ANIM_SCREEN)
            }
        },
        Acts.delay(TIME_ANIM_SCREEN),
        Acts.run {
            animArrowsAndBlurs()
            blockEnd.run()
        },
    )) }

}