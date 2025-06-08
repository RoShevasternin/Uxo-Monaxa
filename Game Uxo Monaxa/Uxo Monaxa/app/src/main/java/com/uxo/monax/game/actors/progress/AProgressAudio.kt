package com.uxo.monax.game.actors.progress

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import com.uxo.monax.game.actors.shader.AMaskGroup
import com.uxo.monax.game.actors.shader.AMaskGroupForScrollPane
import com.uxo.monax.game.utils.advanced.AdvancedGroup
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import com.uxo.monax.game.utils.gdxGame
import com.uxo.monax.game.utils.runGDX
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class AProgressAudio(
    override val screen: AdvancedScreen,
    ls60: LabelStyle
): AdvancedGroup() {

    private val LENGTH = 668f

    private val imgProgress = Image(gdxGame.assetsAll.progress)
    private val lblPercent  = Label("", ls60)
    private val mask        = AMaskGroup(screen, gdxGame.assetsAll.PROGRESS_MASK)

    private val onePercentX = LENGTH / 100f

    // 0 .. 100 %
    val progressPercentFlow = MutableStateFlow(0f)


    override fun addActorsOnGroup() {
        addMask()
        addLblPercent()

        coroutine?.launch {
            progressPercentFlow.collect { percent ->
                runGDX {
                    imgProgress.x = (percent * onePercentX) - LENGTH
                    lblPercent.x  = imgProgress.x + LENGTH - 75f

                    lblPercent.setText(percent.roundToInt().toString())
                }
            }
        }

        addListener(inputListener())
    }

    // Actors ---------------------------------------------------

    private fun AdvancedGroup.addMask() {
        addAndFillActor(mask)
        mask.addAndFillActor(imgProgress)
    }

    private fun AdvancedGroup.addLblPercent() {
        addActor(lblPercent)
        lblPercent.setSize(128f, 70f)
        lblPercent.setAlignment(Align.right)
    }

    // ---------------------------------------------------
    // Logic
    // ---------------------------------------------------

    private fun inputListener() = object : InputListener() {
        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            touchDragged(event, x, y, pointer)
            return true
        }

        override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
            progressPercentFlow.value = when {
                x <= 0 -> 0f
                x >= LENGTH -> 100f
                else -> x / onePercentX
            }

            event?.stop()
        }
    }

}