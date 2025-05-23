package com.uxo.monax.game.actors.items

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.uxo.monax.game.actors.autoLayout.AHorizontalGroup
import com.uxo.monax.game.actors.autoLayout.AutoLayout
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import com.uxo.monax.game.utils.gdxGame
import com.uxo.monax.game.utils.toSeparateWithSymbol

class APrice(
    override val screen: AdvancedScreen,
    val ls30 : Label.LabelStyle,
    val price: Int,
): AHorizontalGroup(
    screen,
    6f,
    alignmentHorizontal = AutoLayout.AlignmentHorizontal.CENTER,
    alignmentVertical   = AutoLayout.AlignmentVertical.CENTER,
) {

    private val imgHand  = Image(gdxGame.assetsAll.hand)
    private val lblPrice = Label(price.toSeparateWithSymbol('.'), ls30)

    override fun addActorsOnGroup() {
        addImgHand()
        addLblPrice()
    }

    private fun addImgHand() {
        imgHand.setSize(40f, 40f)
        addActor(imgHand)
    }

    private fun addLblPrice() {
        addActor(lblPrice)
    }

}