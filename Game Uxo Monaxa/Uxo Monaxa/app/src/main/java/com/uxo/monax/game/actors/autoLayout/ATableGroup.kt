package com.uxo.monax.game.actors.autoLayout

import com.badlogic.gdx.scenes.scene2d.Actor
import com.uxo.monax.game.utils.advanced.AdvancedScreen

open class ATableGroup(
    override val screen: AdvancedScreen,

    private var spaceVertical  : Float = 0f,
    private var spaceHorizontal: Float = 0f,

    private var paddingTop   : Float = 0f,
    private var paddingBottom: Float = 0f,
    private var paddingLeft  : Float = 0f,
    private var paddingRight : Float = 0f,

    private var alignmentVertical  : AutoLayout.AlignmentVertical   = AutoLayout.AlignmentVertical.TOP,
    private var alignmentHorizontal: AutoLayout.AlignmentHorizontal = AutoLayout.AlignmentHorizontal.LEFT,
    private var directionVertical  : AutoLayout.DirectionVertical   = AutoLayout.DirectionVertical.DOWN,

    private var alignmentItemsVertical  : AutoLayout.AlignmentVertical   = AutoLayout.AlignmentVertical.BOTTOM,
    private var alignmentItemsHorizontal: AutoLayout.AlignmentHorizontal = AutoLayout.AlignmentHorizontal.LEFT,
    private var directionHorizontal     : AutoLayout.DirectionHorizontal = AutoLayout.DirectionHorizontal.RIGHT,

    private var isWrap: Boolean = false,
) : AVerticalGroup(
    screen              = screen,
    space               = spaceVertical,
    paddingTop          = paddingTop,
    paddingBottom       = paddingBottom,
    alignmentHorizontal = alignmentHorizontal,
    alignmentVertical   = alignmentVertical,
    directionVertical   = directionVertical,
    isWrap              = isWrap
) {

    private val listHorizontalGroup = mutableListOf<AHorizontalGroup>()

    private lateinit var currentHorizontalGroup: AHorizontalGroup

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()

        currentHorizontalGroup = getNewHorizontalGroup()
        addActor(currentHorizontalGroup)
    }

    private fun getNewHorizontalGroup() =  AHorizontalGroup(
        screen              = screen,
        space               = spaceHorizontal,
        paddingLeft         = paddingLeft,
        paddingRight        = paddingRight,
        alignmentVertical   = alignmentItemsVertical,
        alignmentHorizontal = alignmentItemsHorizontal,
        directionHorizontal = directionHorizontal,
        isWrapHorizontal    = true,
        isWrapVertical      = true
    ).also { listHorizontalGroup.add(it) }

    fun addActorToTable(actor: Actor) {
        // Перевіряємо, чи актор вміщається в поточну горизонтальну групу
        if (currentHorizontalGroup.width + actor.width > width - (paddingLeft + paddingRight)) {
            // Якщо не вміщається, створюємо нову горизонтальну групу
            currentHorizontalGroup = getNewHorizontalGroup()
            addActor(currentHorizontalGroup)
            //currentHorizontalGroup.debugAll()
        }

        // Додаємо актора до поточної горизонтальної групи
        currentHorizontalGroup.addActor(actor)
        currentHorizontalGroup.layout()
        layout()
    }

    fun setSpaceHorizontal(space: Float) {
        spaceHorizontal = space
        listHorizontalGroup.onEach { it.setSpaceHorizontal(space) }
    }

    fun setLeftPadding(padding: Float) {
        paddingLeft = padding
        listHorizontalGroup.onEach { it.setLeftPadding(padding) }
    }

    fun setRightPadding(padding: Float) {
        paddingRight = padding
        listHorizontalGroup.onEach { it.setRightPadding(padding) }
    }

    fun setAlignmentItemsHorizontal(alignment: AutoLayout.AlignmentHorizontal) {
        alignmentItemsHorizontal = alignment
        listHorizontalGroup.onEach { it.setAlignmentHorizontal(alignment) }
    }

    fun setAlignmentItemsVertical(alignment: AutoLayout.AlignmentVertical) {
        alignmentItemsVertical = alignment
        listHorizontalGroup.onEach { it.setAlignmentVertical(alignment) }
    }

    fun setDirectionHorizontal(direction: AutoLayout.DirectionHorizontal) {
        directionHorizontal = direction
        listHorizontalGroup.onEach { it.setDirectionHorizontal(direction) }
    }

}