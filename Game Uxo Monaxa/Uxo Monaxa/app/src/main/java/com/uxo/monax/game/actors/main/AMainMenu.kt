package com.uxo.monax.game.actors.main

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.uxo.monax.game.actors.*
import com.uxo.monax.game.actors.autoLayout.AVerticalGroup
import com.uxo.monax.game.actors.items.AItemsTable
import com.uxo.monax.game.screens.MenuScreen
import com.uxo.monax.game.utils.Acts
import com.uxo.monax.game.utils.TIME_ANIM_SCREEN
import com.uxo.monax.game.utils.actor.animDelay
import com.uxo.monax.game.utils.actor.animHide
import com.uxo.monax.game.utils.actor.animShow
import com.uxo.monax.game.utils.advanced.AdvancedMainGroup
import com.uxo.monax.game.utils.gdxGame

class AMainMenu(
    override val screen: MenuScreen,
): AdvancedMainGroup() {

    private val aBalance      = ABalance(screen)
    private val imgSeparator1 = Image(gdxGame.assetsAll.separator)
    private val imgSeparator2 = Image(gdxGame.assetsAll.separator)
    private val aTurnOffAds   = ATurnOffAds(screen)

    private val verticalGroup  = AVerticalGroup(screen, 78f, paddingTop = 78f, paddingBottom = 250f, isWrap = true)
    private val scroll         = AScrollPane(verticalGroup)
    private val aAudioSettings = AAudioSettings(screen)
    private val imgSeparator3  = Image(gdxGame.assetsAll.separator)
    private val aItemsTable    = AItemsTable(screen)

    //var blockPurchase: () -> Unit = {}

    override fun addActorsOnGroup() {
        addABalance()
        addImgSeparator1()
        addATurnOffAds()
        addImgSeparator2()

        addAScroll()

        animShowMain()
    }

    // Actors ------------------------------------------------------------------------

    private fun addABalance() {
        addActor(aBalance)
        aBalance.color.a = 0f
        aBalance.setBounds(401f, 1762f, 265f, 143f)
    }

    private fun addImgSeparator1() {
        addActor(imgSeparator1)
        imgSeparator1.color.a = 0f
        imgSeparator1.setBounds(79f, 1681f, 922f, 3f)
    }

    private fun addATurnOffAds() {
        addActor(aTurnOffAds)
        aTurnOffAds.setBounds(79f, 1173f, 922f, 458f)
    }

    private fun addImgSeparator2() {
        addActor(imgSeparator2)
        imgSeparator2.color.a = 0f
        imgSeparator2.setBounds(79f, 1121f, 922f, 3f)
    }

    private fun addAScroll() {
        addActor(scroll)
        scroll.setBounds(79f, 0f, 922f, 1121f)
        verticalGroup.setSize(922f, 1321f)

        verticalGroup.apply {
            addAAudioSettings()
            addImgSeparator3()
            addAItemsTable()
        }
    }

    // Actors VerticalGroup ------------------------------------------------------------------------

    private fun AVerticalGroup.addImgSeparator3() {
        imgSeparator3.color.a = 0f
        imgSeparator3.setSize(922f, 3f)
        addActor(imgSeparator3)
    }

    private fun AVerticalGroup.addAAudioSettings() {
        aAudioSettings.color.a = 0f
        aAudioSettings.setSize(922f, 396f)
        addActor(aAudioSettings)
    }

    private fun AVerticalGroup.addAItemsTable() {
        aItemsTable.setSize(922f, 1f)
        addActor(aItemsTable)
    }

    // Anim ------------------------------------------------

    override fun animShowMain(blockEnd: Runnable) {
        children.onEach { it.clearActions() }

        this.addAction(Actions.sequence(
            Acts.run { aBalance.animShow(TIME_ANIM_SCREEN) },
            Acts.delay(TIME_ANIM_SCREEN * 0.35f),
            Acts.run { imgSeparator1.animShow(TIME_ANIM_SCREEN) },
            Acts.delay(TIME_ANIM_SCREEN * 0.35f),
            Acts.run { imgSeparator2.animShow(TIME_ANIM_SCREEN) },
            Acts.delay(TIME_ANIM_SCREEN * 0.35f),
            Acts.run { imgSeparator3.animShow(TIME_ANIM_SCREEN) },
            Acts.delay(TIME_ANIM_SCREEN * 0.7f),
            Acts.run {
                aTurnOffAds.animShowItems()
                aAudioSettings.animShow(TIME_ANIM_SCREEN)
                aItemsTable.animShowItems()
            }
        ))

        this.animDelay(TIME_ANIM_SCREEN) { blockEnd.run() }
    }

    override fun animHideMain(blockEnd: Runnable) {
        children.onEach { it.clearActions() }

        this.addAction(Actions.sequence(
            Acts.run {
                aItemsTable.animHide(TIME_ANIM_SCREEN)
                aAudioSettings.animHide(TIME_ANIM_SCREEN)
                aTurnOffAds.animHide(TIME_ANIM_SCREEN)
            },
            Acts.delay(TIME_ANIM_SCREEN * 0.4f),
            Acts.run {
                imgSeparator3.animHide(TIME_ANIM_SCREEN)
                imgSeparator2.animHide(TIME_ANIM_SCREEN)
                imgSeparator1.animHide(TIME_ANIM_SCREEN)
            },
            Acts.delay(TIME_ANIM_SCREEN * 0.4f),
            Acts.run {
                aBalance.animHide(TIME_ANIM_SCREEN)
            },
            Acts.delay(TIME_ANIM_SCREEN),
            Acts.run {
                blockEnd.run()
            }
        ))
    }

}