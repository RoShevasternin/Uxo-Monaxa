package com.uxo.monax.game.actors

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Disposable

class AParticleEffectActor(
    val particleEffect: ParticleEffect,
    val resetOnStart  : Boolean
): Actor(), Disposable {

    var isRunning = true
        private set

    private val originalAngles: List<AngleRange> = particleEffect.emitters.map { emitter ->
        AngleRange(
            emitter.angle.lowMin,
            emitter.angle.lowMax,
            emitter.angle.highMin,
            emitter.angle.highMax
        )
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (isRunning) particleEffect.update(delta)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        if (isRunning) particleEffect.draw(batch)
    }

    fun start() {
        if (resetOnStart) {
            particleEffect.reset(false)
        }
        particleEffect.start()
    }

    override fun scaleChanged() {
        super.scaleChanged()
        particleEffect.scaleEffect(scaleX, scaleY, scaleY)
    }

    override fun positionChanged() {
        super.positionChanged()
        particleEffect.setPosition(x, y)
    }

    override fun rotationChanged() {
        super.rotationChanged()

        particleEffect.emitters.forEachIndexed { index, emitter ->
            originalAngles[index].apply {
                emitter.angle.setLow(lowMin + rotation, lowMax + rotation)
                emitter.angle.setHigh(highMin + rotation, highMax + rotation)
            }
        }
    }

    override fun dispose() {
        particleEffect.dispose()
    }

    fun allowCompletion() {
        particleEffect.allowCompletion()
    }

    fun pause() {
        isRunning = false
    }

    fun resume() {
        isRunning = true
    }

    data class AngleRange(
        val lowMin: Float,
        val lowMax: Float,
        val highMin: Float,
        val highMax: Float
    )
}