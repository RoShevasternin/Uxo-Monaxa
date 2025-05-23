package com.uxo.monax.game.manager.util

import com.badlogic.gdx.audio.Sound
import com.uxo.monax.game.manager.AudioManager
import com.uxo.monax.game.manager.SoundManager
import kotlin.random.Random

class SoundUtil {

    val click  = AdvancedSound(SoundManager.EnumSound.click.data.sound, 1f)

    private val clicker = AdvancedSound(SoundManager.EnumSound.clicker.data.sound, 0.9f)

    private val _boar      = AdvancedSound(SoundManager.EnumSound.boar.data.sound, 0.25f)
    private val _cat_1     = AdvancedSound(SoundManager.EnumSound.cat_1.data.sound, 0.8f)
    private val _cat_2     = AdvancedSound(SoundManager.EnumSound.cat_2.data.sound, 1.0f)
    private val _cat_3     = AdvancedSound(SoundManager.EnumSound.cat_3.data.sound, 0.9f)
    private val _cat_4     = AdvancedSound(SoundManager.EnumSound.cat_4.data.sound, 0.9f)
    private val _cat_5     = AdvancedSound(SoundManager.EnumSound.cat_5.data.sound, 0.9f)
    private val _dog_1     = AdvancedSound(SoundManager.EnumSound.dog_1.data.sound, 1.0f)
    private val _dog_2     = AdvancedSound(SoundManager.EnumSound.dog_2.data.sound, 1.0f)
    private val _dog_3     = AdvancedSound(SoundManager.EnumSound.dog_3.data.sound, 1.0f)
    private val _bear_1    = AdvancedSound(SoundManager.EnumSound.bear_1.data.sound, 1.0f)
    private val _bear_2    = AdvancedSound(SoundManager.EnumSound.bear_2.data.sound, 1.0f)
    private val _bear_3    = AdvancedSound(SoundManager.EnumSound.bear_3.data.sound, 1.0f)
    private val _turkey_1  = AdvancedSound(SoundManager.EnumSound.turkey_1.data.sound, 0.85f)
    private val _turkey_2  = AdvancedSound(SoundManager.EnumSound.turkey_2.data.sound, 0.85f)
    private val _turkey_3  = AdvancedSound(SoundManager.EnumSound.turkey_3.data.sound, 0.85f)
    private val _wind_1    = AdvancedSound(SoundManager.EnumSound.wind_1.data.sound, 1.0f)
    private val _wind_2    = AdvancedSound(SoundManager.EnumSound.wind_2.data.sound, 1.0f)
    private val _forest_1  = AdvancedSound(SoundManager.EnumSound.forest_1.data.sound, 1.0f)
    private val _forest_2  = AdvancedSound(SoundManager.EnumSound.forest_2.data.sound, 1.0f)
    private val _forest_3  = AdvancedSound(SoundManager.EnumSound.forest_3.data.sound, 1.0f)
    private val _forest_4  = AdvancedSound(SoundManager.EnumSound.forest_4.data.sound, 1.0f)
    private val _forest_5  = AdvancedSound(SoundManager.EnumSound.forest_5.data.sound, 1.0f)
    private val _forest_6  = AdvancedSound(SoundManager.EnumSound.forest_6.data.sound, 1.0f)
    private val _forest_7  = AdvancedSound(SoundManager.EnumSound.forest_7.data.sound, 1.0f)
    private val _forest_8  = AdvancedSound(SoundManager.EnumSound.forest_8.data.sound, 1.0f)
    private val _duck_1    = AdvancedSound(SoundManager.EnumSound.duck_1.data.sound, 1.0f)
    private val _duck_2    = AdvancedSound(SoundManager.EnumSound.duck_2.data.sound, 1.0f)
    private val _duck_3    = AdvancedSound(SoundManager.EnumSound.duck_3.data.sound, 1.0f)
    private val _wheat_1   = AdvancedSound(SoundManager.EnumSound.wheat_1.data.sound, 1.0f)
    private val _wheat_2   = AdvancedSound(SoundManager.EnumSound.wheat_2.data.sound, 1.0f)
    private val _wheat_3   = AdvancedSound(SoundManager.EnumSound.wheat_3.data.sound, 1.0f)
    private val _wheat_4   = AdvancedSound(SoundManager.EnumSound.wheat_4.data.sound, 1.0f)
    private val _wheat_5   = AdvancedSound(SoundManager.EnumSound.wheat_5.data.sound, 1.0f)
    private val _wheat_6   = AdvancedSound(SoundManager.EnumSound.wheat_6.data.sound, 1.0f)

    val boar    get() = getChancePercent(90, clicker, _boar)
    val cat     get() = getChancePercent(90, clicker, _cat_1, _cat_2, _cat_3, _cat_4, _cat_5)
    val dog     get() = getChancePercent(90, clicker, _dog_1, _dog_2, _dog_3)
    val bear    get() = getChancePercent(90, clicker, _bear_1, _bear_2, _bear_3)
    val turkey  get() = getChancePercent(90, clicker, _turkey_1, _turkey_2, _turkey_3)
    val wind    get() = getChancePercent(50, clicker, _wind_1, _wind_2)
    val forest  get() = getChancePercent(80, clicker, _forest_1, _forest_2, _forest_3, _forest_4, _forest_5, _forest_6, _forest_7, _forest_8)
    val duck    get() = getChancePercent(90, clicker, _duck_1, _duck_2, _duck_3)
    val wheat   get() = getChancePercent(90, clicker, _wheat_1, _wheat_2, _wheat_3, _wheat_4, _wheat_5, _wheat_6)
    

    // 0..100
    var volumeLevel = AudioManager.volumeLevelPercent

    var isPause = (volumeLevel <= 0f)

    fun play(advancedSound: AdvancedSound) {
        if (isPause.not()) {
            advancedSound.apply {
                sound.play((volumeLevel / 100f) * coff)
            }
        }
    }

    data class AdvancedSound(val sound: Sound, val coff: Float)

    // Logic --------------------------------------------------------------

    /**
    * result1 - Повертається якщо Random < percent
    * result2 - Повертається якщо Random > percent
    * */
    private fun getChancePercent(percent: Int, result1: AdvancedSound, result2: AdvancedSound): AdvancedSound {
        return if (Random.nextInt(100) < percent) result1 else result2
    }

    private fun getChancePercent(percent: Int, result1: AdvancedSound, vararg results: AdvancedSound): AdvancedSound {
        return if (Random.nextInt(100) < percent) result1 else results.random()
    }

}