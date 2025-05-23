package com.uxo.monax.game.actors.main

import com.uxo.monax.game.actors.ALoader
import com.uxo.monax.game.screens.LoaderScreen
import com.uxo.monax.game.utils.advanced.AdvancedGroup

class AMainLoader(
    override val screen: LoaderScreen,
): AdvancedGroup() {

    val aLoader = ALoader(screen)

    override fun addActorsOnGroup() {
        addImgCircleIn()

    }

    // Actors ------------------------------------------------------------------------

    private fun addImgCircleIn() {
        addActor(aLoader)
        aLoader.setBounds(378f, 798f, 323f, 323f)
    }

}