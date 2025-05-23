package com.uxo.monax.game.screens

import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.uxo.monax.game.actors.ABackground
import com.uxo.monax.game.actors.AParticleEffectActor
import com.uxo.monax.game.actors.main.AMainGame
import com.uxo.monax.game.utils.HEIGHT_UI
import com.uxo.monax.game.utils.TIME_ANIM_SCREEN
import com.uxo.monax.game.utils.WIDTH_UI
import com.uxo.monax.game.utils.advanced.AdvancedMainScreen
import com.uxo.monax.game.utils.advanced.AdvancedStage
import com.uxo.monax.game.utils.gdxGame

class GameScreen: AdvancedMainScreen() {

    private val aBackground         = ABackground(this, gdxGame.currentBackground)
    private val effectFallingLeaves = AParticleEffectActor(ParticleEffect(gdxGame.particleEffectUtil.FallingLeaves), false)

    override val aMain = AMainGame(this)

    override fun AdvancedStage.addActorsOnStageBack() {
        addBackground()
        addEffectLeaf()
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

        aBackground.animToNewTexture(gdxGame.assetsLoader.BACKGROUND, TIME_ANIM_SCREEN)
        gdxGame.currentBackground = gdxGame.assetsLoader.BACKGROUND
    }

    private fun AdvancedStage.addEffectLeaf() {
        val yPercent_20 = (viewportBack.screenHeight * 0.2f)
        val scale       = (viewportBack.screenWidth / 1080f)

        effectFallingLeaves.particleEffect.scaleEffect(scale)
        effectFallingLeaves.y = yPercent_20
        addActor(effectFallingLeaves)
        effectFallingLeaves.start()
    }

    // Actors UI------------------------------------------------------------------------

    override fun AdvancedStage.addMain() {
        addAndFillActor(aMain)

        aMain.apply {
            blockMenu        = { hideScreen { gdxGame.navigationManager.navigate(MenuScreen::class.java.name, GameScreen::class.java.name) } }
            blockLeaderboard = {
                gdxGame.activity.playGamesHelper.showLeaderboard()
                //hideScreen { gdxGame.navigationManager.navigate(LeaderboardScreen::class.java.name, GameScreen::class.java.name) }
            }
        }

    }

}