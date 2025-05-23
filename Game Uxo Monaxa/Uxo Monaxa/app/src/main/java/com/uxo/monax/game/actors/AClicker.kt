package com.uxo.monax.game.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.uxo.monax.game.screens.MenuScreen
import com.uxo.monax.game.utils.actor.disable
import com.uxo.monax.game.utils.actor.setOnClickListenerWithBlock
import com.uxo.monax.game.utils.advanced.AdvancedGroup
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import com.uxo.monax.game.utils.gdxGame

class AClicker(
    override val screen: AdvancedScreen,
): AdvancedGroup() {

    companion object {
        private const val CLICK_COUNT = 10
    }

    private val currentBlockSound = listOf(
        { gdxGame.soundUtil.boar },
        { gdxGame.soundUtil.cat },
        { gdxGame.soundUtil.dog },
        { gdxGame.soundUtil.bear },
        { gdxGame.soundUtil.turkey },
        { gdxGame.soundUtil.wind },
        { gdxGame.soundUtil.forest },
        { gdxGame.soundUtil.duck },
        { gdxGame.soundUtil.wheat },
    )[MenuScreen.SELECTED_ITEM_INDEX]

    private val imgButton    = Image(gdxGame.assetsAll.button_click)
    private val imgItem      = Image(gdxGame.assetsAll.listItems[MenuScreen.SELECTED_ITEM_INDEX])
    private val listImgClick = List(CLICK_COUNT) { Image(gdxGame.assetsAll.click) }

    private var currentClickIndex = 0

    var blockClick = {}

    override fun addActorsOnGroup() {
        addImgPanel()
        addImgItem()
        addListImgClick()

        children.onEach { it.disable() }

        setOnClickListenerWithBlock(
            touchDownBlock = { x, y ->
                gdxGame.soundUtil.apply { play(currentBlockSound()) }
                Gdx.input.vibrate(100)

                animClick(x, y)
                gdxGame.ds_Balance.update {
                    gdxGame.activity.playGamesHelper.submitScore((it + 1).toLong())
                    it + 1
                }

                blockClick()
            }
        ) {

        }
    }

    // Actors ------------------------------------------------------------------------

    private fun addImgPanel() {
        addAndFillActor(imgButton)
        imgButton.setOrigin(Align.center)
    }

    private fun addImgItem() {
        addActor(imgItem)
        imgItem.setBounds(78f, 77f, 295f, 295f)
        imgItem.setOrigin(Align.center)
    }

    private fun addListImgClick() {
        listImgClick.onEach { img ->
            addActor(img)
            img.color.a = 0f
            img.setSize(400f, 400f)
            img.setOrigin(Align.center)
            img.setScale(0f)
        }
    }

    // Logic ------------------------------------------------------------------------

    private fun animClick(x: Float, y: Float) {
        listImgClick[currentClickIndex].also { img ->
            img.setPosition(x - 200f, y - 200f)
            img.addAction(Actions.sequence(
                Actions.parallel(
                    Actions.scaleTo(1f, 1f, 0.5f),
                    Actions.sequence(
                        Actions.fadeIn(0.225f),
                        Actions.fadeOut(0.225f),
                    )
                ),
                Actions.scaleTo(0f, 0f)
            ))
        }
        if ((currentClickIndex + 1) == CLICK_COUNT) currentClickIndex = 0 else currentClickIndex++

        imgItem.apply {
            clearActions()
            addAction(Actions.sequence(
                Actions.scaleTo(0.5f, 0.5f, 0.075f),
                Actions.scaleTo(1f, 1f, 0.25f),
            ))
        }

        imgButton.apply {
            clearActions()
            addAction(Actions.sequence(
                Actions.scaleTo(1f, 1f, 0.05f),
                Actions.scaleTo(0.925f, 0.925f, 0.15f, Interpolation.swingOut),
                Actions.scaleTo(1f, 1f, 0.25f),
            ))
        }
    }

}