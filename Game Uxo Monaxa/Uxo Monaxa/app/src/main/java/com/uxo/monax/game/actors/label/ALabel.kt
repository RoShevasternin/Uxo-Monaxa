package com.uxo.monax.game.actors.label

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.uxo.monax.game.utils.advanced.AdvancedGroup
import com.uxo.monax.game.utils.advanced.AdvancedScreen

class ALabel(
    override val screen: AdvancedScreen,
    text: CharSequence,
    labelStyle: LabelStyle
): AdvancedGroup() {

    val label = Label(text, labelStyle)

    override fun addActorsOnGroup() {
        addActor(label)
        label.setSize(width, height)
    }

}