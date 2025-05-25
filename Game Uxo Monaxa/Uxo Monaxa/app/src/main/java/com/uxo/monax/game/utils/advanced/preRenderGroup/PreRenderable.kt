package com.uxo.monax.game.utils.advanced.preRenderGroup

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group

interface PreRenderable {
    fun preRender(batch: Batch, parentAlpha: Float)
}

fun renderPreRenderables(actor: Actor, batch: Batch, parentAlpha: Float) {
    when(actor) {
        is PreRenderable -> {
            actor.preRender(batch, parentAlpha)
        }
        is Group -> {
            actor.children.forEach { renderPreRenderables(it, batch, it.color.a) }
        }
    }
}