package com.uxo.monax.game.utils.advanced.preRenderGroup

import com.badlogic.gdx.graphics.g2d.Batch

interface FboPreRender {

    fun renderFboGroup(batch: Batch, combinedAlpha: Float)
    fun applyEffect(batch: Batch, combinedAlpha: Float)
    fun renderFboResult(batch: Batch, combinedAlpha: Float)

}