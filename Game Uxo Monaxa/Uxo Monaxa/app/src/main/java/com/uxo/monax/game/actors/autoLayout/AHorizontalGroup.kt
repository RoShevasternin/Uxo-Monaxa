package com.uxo.monax.game.actors.autoLayout

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.uxo.monax.game.utils.advanced.AdvancedGroup
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import kotlin.math.max

open class AHorizontalGroup(
    override val screen: AdvancedScreen,
    private var space: Float = 0f,
    private var paddingLeft: Float = 0f,
    private var paddingRight: Float = 0f,
    private var alignmentVertical: AutoLayout.AlignmentVertical = AutoLayout.AlignmentVertical.BOTTOM,
    private var alignmentHorizontal: AutoLayout.AlignmentHorizontal = AutoLayout.AlignmentHorizontal.LEFT,
    private var directionHorizontal: AutoLayout.DirectionHorizontal = AutoLayout.DirectionHorizontal.RIGHT,
    private var isWrapHorizontal: Boolean = false,
    private var isWrapVertical: Boolean = false,
) : AdvancedGroup() {

    private val originalSize = Vector2()

    private var totalChildrenWidth = 0f
    private var childrenTotalWidth = 0f
    private var availableSpace     = 0f
    private var currentX           = 0f
    private var maxChildHeight     = 0f

    private var actorsToPlace: Iterable<Actor>? = null

    override fun getPrefWidth(): Float {
        return width
    }

    override fun getPrefHeight(): Float {
        return height
    }

    override fun addActorsOnGroup() {
        originalSize.set(width, height)
    }

    override fun layout() {
        // Оновлюємо загальну ширину дітей
        childrenTotalWidth = 0f

        // Підраховуємо загальну ширину акторів без відступів
        children.forEach { actor ->
            childrenTotalWidth += actor.width
            maxChildHeight = max(maxChildHeight, actor.height)
        }

        // Якщо режим AUTO, розраховуємо space для рівномірного розподілу
        if (alignmentHorizontal == AutoLayout.AlignmentHorizontal.AUTO) {
            availableSpace = width - (paddingLeft + paddingRight + childrenTotalWidth)
            space = if (children.size > 1) availableSpace / (children.size - 1) else 0f
        }

        // Загальна висота включає всі актори та відступи
        totalChildrenWidth = childrenTotalWidth + ((space * (children.size - 1)) + (paddingRight + paddingLeft))

        // Якщо isWrap == true, змінюємо ширину групи
        if (isWrapHorizontal) {
            width = if (totalChildrenWidth > originalSize.x) totalChildrenWidth else originalSize.x
        }
        if (isWrapVertical) {
            height = if (maxChildHeight > originalSize.y) maxChildHeight else originalSize.y
        }

        // Визначаємо початкову позицію X для вирівнювання по горизонталі
        currentX = getHorizontalPosition(totalChildrenWidth) // Початковий X для дітей

        // Розміщуємо акторів з урахуванням напрямку
        actorsToPlace = when (directionHorizontal) {
            AutoLayout.DirectionHorizontal.RIGHT -> children // Залишаємо порядок для UP
            AutoLayout.DirectionHorizontal.LEFT -> children.reversed() // Реверсуємо порядок акторів для DOWN
        }

        actorsToPlace?.forEach { actor ->
            actor.setPosition(currentX, getVerticalPosition(actor.height))
            currentX += actor.width + space // Додаємо висоту актора та відступ для наступного актора
        }
    }

    override fun childrenChanged() {
        layout()
        super.childrenChanged()
    }

    open fun setSpaceHorizontal(space: Float) {
        this.space = space
        layout()
    }

    open fun setLeftPadding(padding: Float) {
        this.paddingLeft = padding
        layout()
    }

    open fun setRightPadding(padding: Float) {
        this.paddingRight = padding
        layout()
    }

    open fun setAlignmentHorizontal(alignment: AutoLayout.AlignmentHorizontal) {
        this.alignmentHorizontal = alignment
        layout()
    }

    open fun setAlignmentVertical(alignment: AutoLayout.AlignmentVertical) {
        this.alignmentVertical = alignment
        layout()
    }

    open fun setDirectionHorizontal(direction: AutoLayout.DirectionHorizontal) {
        this.directionHorizontal = direction
        layout()
    }

    open fun setWrapHorizontal(wrap: Boolean) {
        this.isWrapHorizontal = wrap
        layout()
    }

    open fun setWrapVertical(wrap: Boolean) {
        this.isWrapVertical = wrap
        layout()
    }

    private fun getHorizontalPosition(totalChildrenWidth: Float): Float {
        return when (alignmentHorizontal) {
            AutoLayout.AlignmentHorizontal.LEFT -> paddingLeft
            AutoLayout.AlignmentHorizontal.CENTER -> ((width - totalChildrenWidth) / 2f) + paddingLeft
            AutoLayout.AlignmentHorizontal.RIGHT -> (width - totalChildrenWidth) + paddingLeft
            AutoLayout.AlignmentHorizontal.AUTO -> paddingLeft
        }
    }

    private fun getVerticalPosition(actorHeight: Float): Float {
        return when (alignmentVertical) {
            AutoLayout.AlignmentVertical.BOTTOM -> 0f
            AutoLayout.AlignmentVertical.CENTER -> (height - actorHeight) / 2f
            AutoLayout.AlignmentVertical.TOP -> height - actorHeight
            AutoLayout.AlignmentVertical.AUTO -> 0f
        }
    }

}