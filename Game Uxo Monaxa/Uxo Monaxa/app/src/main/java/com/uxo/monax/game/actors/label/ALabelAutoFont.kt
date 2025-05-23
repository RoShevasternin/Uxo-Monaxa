package com.uxo.monax.game.actors.label

import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.uxo.monax.game.utils.advanced.AdvancedGroup
import com.uxo.monax.game.utils.advanced.AdvancedScreen

class ALabelAutoFont(
    override val screen: AdvancedScreen,
    text: String,
    private val labelStyle: LabelStyle,
    private val maxFontScale: Float = 1f,  // Максимальний розмір шрифту
    private val minFontScale: Float = 0.5f, // Мінімальний розмір шрифту
) : AdvancedGroup() {

    val label = Label(text, labelStyle)

    private val glyphLayout = GlyphLayout()

    override fun addActorsOnGroup() {
        addActor(label)
        label.setSize(width, height)

        adjustFontSize()
    }

    fun setText(newText: CharSequence?) {
        label.setText(newText)
        adjustFontSize()
    }

    private fun adjustFontSize() {
        // Початковий масштаб шрифту
        var fontScale = maxFontScale

        while (fontScale >= minFontScale) {
            labelStyle.font.data.setScale(fontScale)
            glyphLayout.setText(labelStyle.font, label.text)

            if (glyphLayout.width <= width) break

            fontScale -= 0.01f // Поступове зменшення масштабу
        }

        label.style = labelStyle
    }
}