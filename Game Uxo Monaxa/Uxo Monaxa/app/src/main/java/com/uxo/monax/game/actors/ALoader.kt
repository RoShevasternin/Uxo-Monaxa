package com.uxo.monax.game.actors

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import com.uxo.monax.game.actors.label.ALabel
import com.uxo.monax.game.utils.GameColor
import com.uxo.monax.game.utils.advanced.AdvancedGroup
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import com.uxo.monax.game.utils.font.FontParameter
import com.uxo.monax.game.utils.gdxGame

class ALoader(override val screen: AdvancedScreen): AdvancedGroup() {

    private val parameter = FontParameter()
    private val font70    = screen.fontGenerator_RubikDoodleShadow.generateFont(parameter.setCharacters(FontParameter.CharType.NUMBERS.chars + "%").setSize(70))

    private val ls70 = LabelStyle(font70, GameColor.black_2b)

    private val imgCircleIn  = Image(gdxGame.assetsLoader.circle_in)
    private val imgCircleOut = Image(gdxGame.assetsLoader.circle_out)
    private val lblPercent   = ALabel(screen, "", ls70)


    override fun addActorsOnGroup() {
        addImgCircleIn()
        addImgCircleOut()
        addLblPercent()
    }

    // Actors ------------------------------------------------------------------------

    private fun addImgCircleIn() {
        addActor(imgCircleIn)
        imgCircleIn.apply {
            setBounds(48f, 48f, 226f, 226f)
            setOrigin(Align.center)
            addAction(Actions.forever(Actions.rotateBy(360f, 5f, Interpolation.linear)))
        }
    }

    private fun addImgCircleOut() {
        addAndFillActor(imgCircleOut)
        imgCircleOut.apply {
            setOrigin(Align.center)
            addAction(Actions.forever(Actions.rotateBy(-360f, 5f, Interpolation.linear)))
        }
    }

    private fun addLblPercent() {
        addActor(lblPercent)
        lblPercent.apply {
            setBounds(62f, 119f, 199f, 83f)
            label.setAlignment(Align.center)
            setOrigin(Align.center)

            val scale = 0.2f
            addAction(Actions.forever(Actions.sequence(
                Actions.scaleBy(-scale, -scale, 0.25f, Interpolation.sineOut),
                Actions.scaleBy(scale, scale, 0.25f, Interpolation.sineIn),
            )))
        }
    }

    // Logic -------------------------------------------------------------------------

    fun setPercent(percent: Int) {
        lblPercent.label.setText("${percent}%")
    }

}