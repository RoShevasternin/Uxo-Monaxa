package com.uxo.monax.game.actors

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import com.uxo.monax.game.actors.label.ALabelAutoFont
import com.uxo.monax.game.utils.GameColor
import com.uxo.monax.game.utils.actor.disable
import com.uxo.monax.game.utils.advanced.AdvancedGroup
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import com.uxo.monax.game.utils.currentTimeMinus
import com.uxo.monax.game.utils.font.FontParameter
import com.uxo.monax.game.utils.gdxGame
import com.uxo.monax.game.utils.toMS
import java.util.concurrent.atomic.AtomicBoolean

class AClickCounter(
    override val screen: AdvancedScreen,
): AdvancedGroup() {

    companion object {
        private const val CLICK_COUNT = 10
        private const val TIME_ANIM   = 0.5f
    }

    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS)
        .setSize(200)

    private val font200 = screen.fontGenerator_RubikDoodleShadow.generateFont(parameter)

    private val ls200 = LabelStyle(font200, GameColor.black_2b)

    private val listImgClick    = List(CLICK_COUNT) { Image(gdxGame.assetsAll.click) }
    private val lblClickCounter = ALabelAutoFont(screen, "", ls200, minFontScale = 0.1f)

    // Field
    private var currentClickIndex = 0
    private var currentClickTime  = 0L
    private val isFixed           = AtomicBoolean(false)
    private var clickCount        = 0

    var blockFinishClickFixed: (Int) -> Unit = {}

    override fun addActorsOnGroup() {
        setOrigin(Align.center)
        addListImgClick()
        addLblClickCounter()

        children.onEach { it.disable() }
    }

    // Actors ------------------------------------------------------------------------

    private fun addListImgClick() {
        listImgClick.onEach { img ->
            addAndFillActor(img)
            img.color.a = 0f
            img.setOrigin(Align.center)
            img.setScale(0f)
        }
    }

    private fun addLblClickCounter() {
        addActor(lblClickCounter)
        lblClickCounter.setBounds(47f, 47f, 226f, 226f)

        lblClickCounter.apply {
            label.setAlignment(Align.center)
            setOrigin(Align.center)
            setScale(0f)
        }
    }

    // Logic ------------------------------------------------------------------------

    fun click() {
        if (currentTimeMinus(currentClickTime) <= TIME_ANIM.toMS) {
            updateClickCount(clickCount.inc())

            if (isFixed.getAndSet(true).not()) {
                setLastCurrentClickIndex()
                animClickFixed()
            } else {
                animClickGroup()
            }
        } else {
            updateClickCount(1)
            isFixed.set(false)
            animClick()
        }
        currentClickTime = System.currentTimeMillis()
    }

    private fun setNextCurrentClickIndex() = if ((currentClickIndex + 1) == CLICK_COUNT) currentClickIndex = 0 else currentClickIndex++
    private fun setLastCurrentClickIndex() = if ((currentClickIndex - 1) >= 0) currentClickIndex-- else currentClickIndex = CLICK_COUNT.dec()

    private fun updateClickCount(value: Int) {
        clickCount = value
        lblClickCounter.setText(clickCount.toString())
        animLblClickCount()
    }

    // Anim -----------------------------------------------------------------------------

    private fun animClick() {
        listImgClick[currentClickIndex].also { img ->
            img.addAction(Actions.sequence(
                Actions.parallel(
                    Actions.scaleTo(1f, 1f, TIME_ANIM),
                    Actions.fadeIn(TIME_ANIM),
                ),
                Actions.parallel(
                    Actions.scaleTo(0f, 0f, TIME_ANIM),
                    Actions.sequence(Actions.fadeOut(TIME_ANIM),)
                ),
            ))
        }

        setNextCurrentClickIndex()
    }

    private fun animClickFixed() {
        listImgClick[currentClickIndex].also { img ->
            img.clearActions()
            img.addAction(Actions.sequence(
                Actions.parallel(
                    Actions.scaleTo(1f, 1f, TIME_ANIM),
                    Actions.fadeIn(TIME_ANIM),
                ),

                // Безкінечно кожні 0.5 секунди перевіряємо чи був КЛІК і якщо ні то закриваємо анімацію
                Actions.forever(Actions.sequence(
                    Actions.delay(TIME_ANIM),
                    Actions.run { if (currentTimeMinus(currentClickTime) >= TIME_ANIM.toMS) {
                        setNextCurrentClickIndex()
                        img.clearActions()
                        img.addAction(Actions.parallel(
                            Actions.scaleTo(0f, 0f, TIME_ANIM),
                            Actions.fadeOut(TIME_ANIM),
                        ))

                        blockFinishClickFixed(clickCount)
                    } }
                ))

            ))
        }
    }

    private fun animClickGroup() {
        addAction(Actions.sequence(
            Actions.scaleTo(0.9f, 0.9f, 0.075f),
            Actions.scaleTo(1f, 1f, 0.075f),
        ))
    }

    private fun animLblClickCount() {
        lblClickCounter.clearActions()
        lblClickCounter.addAction(Actions.sequence(
            Actions.parallel(
                Actions.scaleTo(1f, 1f, TIME_ANIM),
                Actions.fadeIn(TIME_ANIM),
            ),
            Actions.parallel(
                Actions.scaleTo(0f, 0f, TIME_ANIM),
                Actions.sequence(Actions.fadeOut(TIME_ANIM),)
            ),
        ))
    }

}