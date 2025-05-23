package com.uxo.monax.game.actors.items

import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.uxo.monax.game.actors.autoLayout.ATableGroup
import com.uxo.monax.game.actors.checkbox.ACheckBoxGroup
import com.uxo.monax.game.utils.GameColor
import com.uxo.monax.game.utils.TIME_ANIM_SCREEN
import com.uxo.monax.game.utils.actor.animDelay
import com.uxo.monax.game.utils.actor.animShow
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import com.uxo.monax.game.utils.font.FontParameter
import com.uxo.monax.game.utils.gdxGame

class AItemsTable(
    override val screen: AdvancedScreen,
): ATableGroup(screen, 65f, 27f, isWrap = true) {

    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS.chars + ".")
        .setSize(30)

    private val font30 = screen.fontGenerator_SansitaOne.generateFont(parameter)

    private val ls30 = LabelStyle(font30, GameColor.yellow_f9)

    private val listItemPrice = listOf(
        0, 100, 500, 1_000,
        2_500, 5_000, 10_000, 100_000,
        1_000_000
    )

    private val cbg     = ACheckBoxGroup()
    private val balance = gdxGame.ds_Balance.flow.value

    private val listItem = List(9) { index -> AItem(
        screen = screen,
        indexItem = index,
        cbg       = cbg,
        ls30      = ls30,
        price     = listItemPrice[index],
        isOpen    = balance >= listItemPrice[index]
    ) }

    override fun addActorsOnGroup() {
        super.addActorsOnGroup()
        addItems()
    }

    // Actors ------------------------------------------------------------------------

    private fun addItems() {
        listItem.onEach { item ->
            item.color.a = 0f
            item.setSize(210f, 210f)
            addActorToTable(item)
        }
    }

    // Anim ------------------------------------------------

    fun animShowItems(blockEnd: Runnable = Runnable {}) {
        listItem.onEachIndexed { index, item ->
            item.animDelay(((TIME_ANIM_SCREEN * 0.4f) + 0.020f) * index.inc()) {
                item.animShow(TIME_ANIM_SCREEN)
            }
        }
        this.animDelay(listItem.size * TIME_ANIM_SCREEN + 0.020f) {
            blockEnd.run()
        }

    }

}