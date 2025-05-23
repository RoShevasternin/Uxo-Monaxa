package com.uxo.monax.game.actors

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import com.uxo.monax.game.actors.label.ALabel
import com.uxo.monax.game.actors.label.ALabelSpinning
import com.uxo.monax.game.utils.GameColor
import com.uxo.monax.game.utils.advanced.AdvancedGroup
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import com.uxo.monax.game.utils.font.FontParameter
import com.uxo.monax.game.utils.gdxGame
import com.uxo.monax.game.utils.runGDX
import kotlinx.coroutines.launch

class ABalance(
    override val screen: AdvancedScreen,
): AdvancedGroup() {

    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + "+")
        .setBorder(1f, GameColor.brown_4f)
        .setShadow(3, 3, GameColor.brown_4f)
        .setSize(60)

    private val font60 = screen.fontGenerator_SansitaOne.generateFont(parameter)

    private val ls60       = LabelStyle(font60, GameColor.yellow_f9)

    private val imgPanel = Image(gdxGame.assetsAll.panel_click)
    private val lblCount = ALabelSpinning(screen, "", ls60, Align.center)

    private val lblShowClick = ALabel(screen, "", ls60)


    override fun addActorsOnGroup() {
        addLblShowClick()
        addImgPanel()
        addLblCount()
    }

    // Actors ------------------------------------------------------------------------

    private fun addImgPanel() {
        addAndFillActor(imgPanel)
    }

    private fun addLblCount() {
        addActor(lblCount)
        lblCount.setBounds(35f, 28f, 207f, 70f)

        coroutine?.launch {
            gdxGame.ds_Balance.flow.collect { balance ->
                runGDX { lblCount.setText(balance.toString()) }
            }
        }
    }

    private fun addLblShowClick() {
        addActor(lblShowClick)
        lblShowClick.setBounds(35f, 28f, 207f, 70f)
        lblShowClick.label.setAlignment(Align.center)
        lblShowClick.color.a = 0f
        lblShowClick.setOrigin(Align.center)
    }

    // Logic -------------------------------------------------------------------------

    fun showClickCount(count: Int) {
        lblShowClick.apply {
            label.setText("+$count")

            clearActions()
            addAction(Actions.sequence(
                Actions.parallel(
                    Actions.fadeIn(0.4f),
                    Actions.moveTo(35f, -105f, 0.5f, Interpolation.swingOut),
                ),
                Actions.delay(0.25f),
                Actions.scaleTo(1.5f, 1.5f, 0.15f),
                Actions.scaleTo(1f, 1f, 0.15f),
                Actions.parallel(
                    Actions.fadeOut(0.4f),
                    Actions.moveTo(35f, 28f, 0.5f, Interpolation.swingIn),
                ),
            ))
        }
    }

}