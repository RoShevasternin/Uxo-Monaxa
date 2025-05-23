package com.uxo.monax.game.screens

import com.uxo.monax.game.actors.ABackground
import com.uxo.monax.game.actors.main.AMainMenu
import com.uxo.monax.game.utils.HEIGHT_UI
import com.uxo.monax.game.utils.TIME_ANIM_SCREEN
import com.uxo.monax.game.utils.WIDTH_UI
import com.uxo.monax.game.utils.advanced.AdvancedMainScreen
import com.uxo.monax.game.utils.advanced.AdvancedStage
import com.uxo.monax.game.utils.gdxGame

class MenuScreen: AdvancedMainScreen() {

    companion object {
        var SELECTED_ITEM_INDEX = 0
    }

    private val aBackground = ABackground(this, gdxGame.currentBackground)

    override val aMain = AMainMenu(this)

    override fun AdvancedStage.addActorsOnStageBack() {
        addBackground()
    }

    override fun AdvancedStage.addActorsOnStageUI() {
        addMain()
    }

    override fun hideScreen(block: Runnable) {
        aMain.animHideMain { block.run() }
    }

    // Actors Back------------------------------------------------------------------------

    private fun AdvancedStage.addBackground() {
        addActor(aBackground)

        val screenRatio = viewportBack.screenWidth / viewportBack.screenHeight
        val imageRatio  = (WIDTH_UI / HEIGHT_UI)

        val scale = if (screenRatio > imageRatio) WIDTH_UI / viewportBack.screenWidth else HEIGHT_UI / viewportBack.screenHeight
        aBackground.setSize(WIDTH_UI / scale, HEIGHT_UI / scale)

        aBackground.animToNewTexture(gdxGame.assetsAll.BACKGROUND_BLURED, TIME_ANIM_SCREEN)
        gdxGame.currentBackground = gdxGame.assetsAll.BACKGROUND_BLURED
    }

    // Actors UI------------------------------------------------------------------------

    override fun AdvancedStage.addMain() {
        addAndFillActor(aMain)
    }

}