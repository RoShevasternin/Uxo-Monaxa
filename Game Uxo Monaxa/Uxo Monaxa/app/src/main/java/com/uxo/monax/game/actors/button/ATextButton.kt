package com.uxo.monax.game.actors.button

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.uxo.monax.game.utils.actor.disable
import com.uxo.monax.game.utils.advanced.AdvancedScreen

open class ATextButton(
    override val screen: AdvancedScreen,
    text: String,
    labelStyle: Label.LabelStyle,
    type: Type = Type.None,
) : AButton(screen, type) {

    val label = Label(text, labelStyle)

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()

        addAndFillActor(label)

        label.disable()
        label.setAlignment(Align.center)
    }

}