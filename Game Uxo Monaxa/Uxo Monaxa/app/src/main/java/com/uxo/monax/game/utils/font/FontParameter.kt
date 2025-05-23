package com.uxo.monax.game.utils.font

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter

class FontParameter : FreeTypeFontParameter() {

    init {
        setLinear()
    }

    fun setLinear(): FontParameter {
        minFilter = Texture.TextureFilter.Linear
        magFilter = Texture.TextureFilter.Linear
        return this
    }
    fun setSize(size: Int): FontParameter {
        this.size = size
        return this
    }
    fun setCharacters(characters: CharType): FontParameter {
        this.characters = characters.chars
        return this
    }
    fun setCharacters(chars: String): FontParameter {
        this.characters = chars
        return this
    }
    fun setBorder(width: Float, color: Color): FontParameter {
        this.borderWidth = width
        this.borderColor = color
        return this
    }
    fun setShadow(offsetX: Int, offsetY: Int, color: Color): FontParameter {
        this.shadowOffsetX = offsetX
        this.shadowOffsetY = offsetY
        this.shadowColor   = color
        return this
    }

    enum class CharType(val chars: String) {
        SYMBOLS       ("\"!`?'•.,;:()[]{}<>|/@\\^\$€—%-+=#_&~*’…«»❤°\""                    ),
        NUMBERS       ("1234567890"                                                        ),
        LATIN         ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"              ),
        CYRILLIC      ("АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЄЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэєюяІЇії"),

        LATIN_CYRILLIC(LATIN.chars.plus(CYRILLIC.chars)                                         ),
        ALL           (SYMBOLS.chars.plus(NUMBERS.chars).plus(LATIN.chars).plus(CYRILLIC.chars) ),
    }

}