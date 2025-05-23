package com.uxo.monax.game.actors.items

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.uxo.monax.game.actors.checkbox.ACheckBox
import com.uxo.monax.game.actors.checkbox.ACheckBoxGroup
import com.uxo.monax.game.screens.MenuScreen
import com.uxo.monax.game.utils.advanced.AdvancedGroup
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import com.uxo.monax.game.utils.gdxGame

class AItem(
    override val screen: AdvancedScreen,
    val indexItem: Int,
    val cbg      : ACheckBoxGroup,
    val ls30     : Label.LabelStyle,
    val price    : Int,
    val isOpen   : Boolean = false
): AdvancedGroup() {

    private val imgItem = Image(gdxGame.assetsAll.listItems[indexItem])

    private var boxItem : ACheckBox? = null
    private var imgClose: Image?     = null
    private var aPrice  : APrice?    = null

    override fun addActorsOnGroup() {
        addImgItem()
        if (isOpen) {
            addBoxItem()
        } else {
            addImgClose()
            addPrice()
        }
    }

    private fun addImgItem() {
        addActor(imgItem)
        imgItem.setBounds(5f, 5f, 200f, 200f)
    }

    private fun addBoxItem() {
        boxItem = ACheckBox(screen, ACheckBox.Type.ITEM).also { box ->
            addAndFillActor(box)

            box.checkBoxGroup = cbg
            if (indexItem == MenuScreen.SELECTED_ITEM_INDEX) box.check(false)

            box.setOnCheckListener { isCheck ->
                if (isCheck) MenuScreen.SELECTED_ITEM_INDEX = indexItem
            }
        }
    }

    private fun addImgClose() {
        imgClose = Image(gdxGame.assetsAll.item_close).also { img ->
            addAndFillActor(img)
        }
    }

    private fun addPrice() {
        aPrice = APrice(screen, ls30, price).also { aPrice ->
            addActor(aPrice)
            aPrice.setBounds(3f, 5f, 203f, 40f)
        }
    }

}