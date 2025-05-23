package com.uxo.monax.game.utils.advanced.preRenderGroup

import com.badlogic.gdx.graphics.g2d.Batch

interface PreRenderable {

    fun preRender(batch: Batch, parentAlpha: Float)

}