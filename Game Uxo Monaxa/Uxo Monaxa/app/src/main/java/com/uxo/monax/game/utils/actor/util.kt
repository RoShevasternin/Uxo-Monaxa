package com.uxo.monax.game.utils.actor

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.uxo.monax.game.actors.button.AButton
import com.uxo.monax.game.manager.util.SoundUtil
import com.uxo.monax.game.utils.Acts
import com.uxo.monax.game.utils.SizeScaler
import kotlin.math.round

fun Actor.setOnClickListener(
    soundUtil: SoundUtil? = null,
    block: (Actor) -> Unit
) {

    addListener(object : InputListener() {
        var isEnter = false

        override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
            isEnter = true
        }
        override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
            isEnter = false
        }

        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            touchDragged(event, x, y, pointer)
            return true
        }

        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
            if (isEnter) {
                soundUtil?.apply { play(click) }
                isEnter = false
                block(this@setOnClickListener)
            }
        }
    })
}

fun Actor.setOnTouchListener(radius: Int = 1, block: (Actor) -> Unit) {
    val touchPointDown = Vector2()
    val touchPointUp   = Vector2()
    addListener(object : InputListener() {
        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            touchPointDown.set(round(x), round(y))
            return true
        }
        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
            touchPointUp.set(round(x), round(y))
            if (touchPointDown.x in (touchPointUp.x - radius..touchPointUp.x + radius) &&
                touchPointDown.y in (touchPointUp.y - radius..touchPointUp.y + radius)) block(this@setOnTouchListener)
        }
    })
}

fun Actor.setOnClickListenerWithBlock(
    soundUtil: SoundUtil? = null,
    touchDownBlock   : Actor.(x: Float, y: Float) -> Unit = { _, _ -> },
    touchDraggedBlock: Actor.(x: Float, y: Float) -> Unit = { _, _ -> },
    touchUpBlock     : Actor.(x: Float, y: Float) -> Unit = { _, _ -> },
    block: Actor.() -> Unit = {},
) {

    addListener(object : InputListener() {
        var isWithin = false

        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            touchDownBlock(x, y)
            touchDragged(event, x, y, pointer)
            soundUtil?.apply { play(click) }
            return true
        }

        override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
            touchDraggedBlock(x, y)
            isWithin = x in 0f..width && y in 0f..height
        }

        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
            touchUpBlock(x, y)
            if (isWithin) {
                isWithin = false
                block()
            }
        }
    })
}

fun Actor.getTopParent(root: Group): Group {
    var top = this.parent

    if (top == root) return root

    while (top.parent != root) {
        top = top.parent
    }

    return top
}

fun Actor.disable() = when(this) {
    is AButton -> disable()
    else       -> touchable = Touchable.disabled
}

fun Actor.enable() = when(this) {
    is AButton -> enable()
    else       -> touchable = Touchable.enabled
}

fun List<Actor>.setFillParent() {
    onEach { actor ->
        when (actor) {
            is Widget      -> actor.setFillParent(true)
            is WidgetGroup -> actor.setFillParent(true)
        }
    }
}

fun Actor.setBounds(position: Vector2, size: Vector2) {
    setBounds(position.x, position.y, size.x, size.y)
}

fun Actor.setBoundsScaled(sizeScaler: SizeScaler, x: Float, y: Float, width: Float, height: Float) {
    setBounds(sizeScaler.scaled(x), sizeScaler.scaled(y), sizeScaler.scaled(width), sizeScaler.scaled(height))
}

fun Actor.setBoundsScaled(sizeScaler: SizeScaler, position: Vector2, size: Vector2) {
    setBoundsScaled(sizeScaler, position.x, position.y, size.x, size.y)
}

fun Actor.setSizeScaled(sizeScaler: SizeScaler, width: Float, height: Float) {
    setSize(sizeScaler.scaled(width), sizeScaler.scaled(height))
}

fun Actor.setPosition(position: Vector2) {
    setPosition(position.x, position.y)
}

fun Actor.setOrigin(vector: Vector2) {
    setOrigin(vector.x, vector.y)
}

fun Actor.animShow(time: Float=0f, block: () -> Unit = {}) {
    addAction(Actions.sequence(
        Actions.fadeIn(time),
        Actions.run(block)
    ))
}
fun Actor.animHide(time: Float=0f, block: () -> Unit = {}) {
    addAction(Actions.sequence(
        Actions.fadeOut(time),
        Actions.run(block)
    ))
}

fun Actor.animMoveTo(
    x: Float, y: Float,
    time: Float = 0f,
    interpolation: Interpolation = Interpolation.linear,
    block: () -> Unit = {}
) {
    addAction(
        Actions.sequence(
        Actions.moveTo(x, y, time, interpolation),
        Actions.run { block() }
    ))
}

fun Actor.animDelay(time: Float, block: () -> Unit = {}) {
    addAction(Acts.sequence(
        Acts.delay(time),
        Acts.run { block.invoke() }
    ))
}