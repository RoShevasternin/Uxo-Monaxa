package com.uxo.monax.game.actors.main

import com.badlogic.gdx.math.Interpolation
import com.uxo.monax.game.actors.ABalance
import com.uxo.monax.game.actors.AClickCounter
import com.uxo.monax.game.actors.AClicker
import com.uxo.monax.game.actors.button.AButton
import com.uxo.monax.game.screens.GameScreen
import com.uxo.monax.game.utils.TIME_ANIM_SCREEN
import com.uxo.monax.game.utils.actor.animDelay
import com.uxo.monax.game.utils.actor.animHide
import com.uxo.monax.game.utils.actor.animMoveTo
import com.uxo.monax.game.utils.actor.animShow
import com.uxo.monax.game.utils.advanced.AdvancedMainGroup
import com.uxo.monax.game.utils.runGDX
import kotlinx.coroutines.launch

class AMainGame(
    override val screen: GameScreen,
): AdvancedMainGroup() {

    private val aBalance       = ABalance(screen)
    private val btnMenu        = AButton(screen, AButton.Type.Menu)
    private val btnLeaderboard = AButton(screen, AButton.Type.Leaderboard)
    private val aClicker       = AClicker(screen)
    private val aClickCounter  = AClickCounter(screen)

    var blockMenu       : () -> Unit = {}
    var blockLeaderboard: () -> Unit = {}

    override fun addActorsOnGroup() {
        coroutine?.launch {
            runGDX {
                addABalance()
                addBtnMenu()
                addBtnLeaderboard()
                addAClicker()
                addAClickCounter()
            }

            animShowMain()
        }
    }

    // Actors ------------------------------------------------------------------------

    private fun addABalance() {
        addActor(aBalance)
        aBalance.color.a = 0f
        aBalance.setBounds(401f, 1762f, 265f, 143f)
    }

    private fun addBtnMenu() {
        addActor(btnMenu)
        btnMenu.setBounds(1080f, 449f, 153f, 171f)
        btnMenu.setOnClickListener { blockMenu() }
    }

    private fun addBtnLeaderboard() {
        addActor(btnLeaderboard)
        btnLeaderboard.setBounds(-153f, 449f, 153f, 171f)
        btnLeaderboard.setOnClickListener { blockLeaderboard() }
    }

    private fun addAClicker() {
        addActor(aClicker)
        aClicker.setBounds(314f, -451f, 451f, 451f)
        aClicker.blockClick = {
            aClickCounter.click()
        }
    }

    private fun addAClickCounter() {
        addActor(aClickCounter)
        aClickCounter.setBounds(378f, 1055f, 323f, 323f)

        aClickCounter.blockFinishClickFixed = { aBalance.showClickCount(it) }
    }

    // Anim ------------------------------------------------

    override fun animShowMain(blockEnd: Runnable) {
        aBalance.animShow(TIME_ANIM_SCREEN)
        btnMenu.animMoveTo(927f, 449f, TIME_ANIM_SCREEN, Interpolation.sineOut)
        btnLeaderboard.animMoveTo(0f, 449f, TIME_ANIM_SCREEN, Interpolation.sineOut)
        aClicker.animMoveTo(314f, 309f, TIME_ANIM_SCREEN, Interpolation.sineOut)

        this.animDelay(TIME_ANIM_SCREEN) { blockEnd.run() }
    }

    override fun animHideMain(blockEnd: Runnable) {
        aBalance.animHide(TIME_ANIM_SCREEN)
        btnMenu.animMoveTo(1080f, 449f, TIME_ANIM_SCREEN, Interpolation.sineIn)
        btnLeaderboard.animMoveTo(-153f, 449f, TIME_ANIM_SCREEN, Interpolation.sineIn)
        aClicker.animMoveTo(314f, -451f, TIME_ANIM_SCREEN, Interpolation.sineIn)

        this.animDelay(TIME_ANIM_SCREEN) { blockEnd.run() }
    }

}