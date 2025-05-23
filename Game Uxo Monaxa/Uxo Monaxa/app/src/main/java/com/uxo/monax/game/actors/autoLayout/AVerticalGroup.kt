package com.uxo.monax.game.actors.autoLayout

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.uxo.monax.game.utils.advanced.AdvancedGroup
import com.uxo.monax.game.utils.advanced.AdvancedScreen

open class AVerticalGroup(
    override val screen: AdvancedScreen,

    private var space        : Float = 0f,
    private var paddingTop   : Float = 0f,
    private var paddingBottom: Float = 0f,
    private var alignmentHorizontal: AutoLayout.AlignmentHorizontal = AutoLayout.AlignmentHorizontal.LEFT,
    private var alignmentVertical  : AutoLayout.AlignmentVertical   = AutoLayout.AlignmentVertical.TOP,
    private var directionVertical  : AutoLayout.DirectionVertical   = AutoLayout.DirectionVertical.DOWN,
    private var isWrap: Boolean = false,
) : AdvancedGroup() {

    private val originalSize = Vector2()

    private var totalChildrenHeight = 0f
    private var childrenTotalHeight = 0f
    private var availableSpace      = 0f
    private var currentY            = 0f

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
        // Оновлюємо загальну висоту дітей
        childrenTotalHeight = 0f

        // Підраховуємо загальну висоту акторів без відступів
        children.forEach { actor ->
            childrenTotalHeight += actor.height
        }

        // Якщо режим AUTO, розраховуємо space для рівномірного розподілу
        if (alignmentVertical == AutoLayout.AlignmentVertical.AUTO) {
            availableSpace = height - (paddingBottom + paddingTop + childrenTotalHeight)
            space = if (children.size > 1) availableSpace / (children.size - 1) else 0f
        }

        // Загальна висота включає всі актори та відступи
        totalChildrenHeight = childrenTotalHeight + ((space * (children.size - 1)) + (paddingTop + paddingBottom))

        // Якщо isWrap == true, змінюємо висоту групи
        if (isWrap) {
            height = if (totalChildrenHeight > originalSize.y) totalChildrenHeight else originalSize.y
        }

        // Визначаємо початкову позицію Y для вирівнювання по вертикалі
        currentY = getVerticalPosition(totalChildrenHeight) // Початковий Y для дітей

        // Розміщуємо акторів з урахуванням напрямку
        actorsToPlace = when(directionVertical) {
            AutoLayout.DirectionVertical.UP   -> children // Залишаємо порядок для UP
            AutoLayout.DirectionVertical.DOWN -> children.reversed() // Реверсуємо порядок акторів для DOWN
        }

        actorsToPlace?.forEach { actor ->
            actor.setPosition(getHorizontalPosition(actor.width), currentY)
            currentY += actor.height + space // Додаємо висоту актора та відступ для наступного актора
        }

    }

    override fun childrenChanged() {
        layout()
        super.childrenChanged()
    }

    open fun setSpaceVertical(space: Float) {
        this.space = space
        layout()
    }

    open fun setTopPadding(padding: Float) {
        this.paddingTop = padding
        layout()
    }

    open fun setBottomPadding(padding: Float) {
        this.paddingBottom = padding
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

    open fun setDirectionVertical(direction: AutoLayout.DirectionVertical) {
        this.directionVertical = direction
        layout()
    }

    open fun setWrapVertical(wrap: Boolean) {
        this.isWrap = wrap
        layout()
    }

    // Метод для визначення горизонтальної позиції актора на основі вирівнювання
    private fun getHorizontalPosition(actorWidth: Float): Float {
        return when (alignmentHorizontal) {
            AutoLayout.AlignmentHorizontal.LEFT   -> 0f // Ліве вирівнювання
            AutoLayout.AlignmentHorizontal.CENTER -> (width - actorWidth) / 2f // Центр
            AutoLayout.AlignmentHorizontal.RIGHT  -> width - actorWidth // Праве вирівнювання
            AutoLayout.AlignmentHorizontal.AUTO   -> 0f // Можна додати логіку для AUTO, або залишити ліве вирівнювання
        }
    }

    // Метод для визначення вертикальної позиції групи на основі вирівнювання
    private fun getVerticalPosition(totalChildrenHeight: Float): Float {
        return when (alignmentVertical) {
            AutoLayout.AlignmentVertical.BOTTOM -> paddingBottom // Починаємо з нижнього падингу
            AutoLayout.AlignmentVertical.CENTER -> ((height - totalChildrenHeight) / 2f) + paddingBottom // Центр групи
            AutoLayout.AlignmentVertical.TOP    -> (height - totalChildrenHeight) + paddingBottom// Вирівнюємо по верху
            AutoLayout.AlignmentVertical.AUTO   -> paddingBottom // Логіка для AUTO (якщо потрібно)
        }
    }

}