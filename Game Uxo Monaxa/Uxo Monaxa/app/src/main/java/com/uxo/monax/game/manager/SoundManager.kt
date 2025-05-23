package com.uxo.monax.game.manager

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound

class SoundManager(var assetManager: AssetManager) {

    var loadableSoundList = mutableListOf<SoundData>()

    fun load() {
        loadableSoundList.onEach { assetManager.load(it.path, Sound::class.java) }
    }

    fun init() {
        loadableSoundList.onEach { it.sound = assetManager[it.path, Sound::class.java] }
        loadableSoundList.clear()
    }

    enum class EnumSound(val data: SoundData) {
        click     (SoundData("sound/click.mp3")),
        clicker   (SoundData("sound/clicker.mp3")),

        boar (SoundData("sound/animals/boar.mp3")),
        cat_1 (SoundData("sound/animals/cat_1.mp3")),
        cat_2 (SoundData("sound/animals/cat_2.mp3")),
        cat_3 (SoundData("sound/animals/cat_3.mp3")),
        cat_4 (SoundData("sound/animals/cat_4.mp3")),
        cat_5 (SoundData("sound/animals/cat_5.mp3")),
        dog_1 (SoundData("sound/animals/dog_1.mp3")),
        dog_2 (SoundData("sound/animals/dog_2.mp3")),
        dog_3 (SoundData("sound/animals/dog_3.mp3")),
        bear_1 (SoundData("sound/animals/bear_1.mp3")),
        bear_2 (SoundData("sound/animals/bear_2.mp3")),
        bear_3 (SoundData("sound/animals/bear_3.mp3")),
        turkey_1 (SoundData("sound/animals/turkey_1.mp3")),
        turkey_2 (SoundData("sound/animals/turkey_2.mp3")),
        turkey_3 (SoundData("sound/animals/turkey_3.mp3")),
        wind_1 (SoundData("sound/animals/wind_1.mp3")),
        wind_2 (SoundData("sound/animals/wind_2.mp3")),
        forest_1(SoundData("sound/animals/forest_1.mp3")),
        forest_2(SoundData("sound/animals/forest_2.mp3")),
        forest_3(SoundData("sound/animals/forest_3.mp3")),
        forest_4(SoundData("sound/animals/forest_4.mp3")),
        forest_5(SoundData("sound/animals/forest_5.mp3")),
        forest_6(SoundData("sound/animals/forest_6.mp3")),
        forest_7(SoundData("sound/animals/forest_7.mp3")),
        forest_8(SoundData("sound/animals/forest_8.mp3")),
        duck_1(SoundData("sound/animals/duck_1.mp3")),
        duck_2(SoundData("sound/animals/duck_2.mp3")),
        duck_3(SoundData("sound/animals/duck_3.mp3")),
        wheat_1(SoundData("sound/animals/wheat_1.mp3")),
        wheat_2(SoundData("sound/animals/wheat_2.mp3")),
        wheat_3(SoundData("sound/animals/wheat_3.mp3")),
        wheat_4(SoundData("sound/animals/wheat_4.mp3")),
        wheat_5(SoundData("sound/animals/wheat_5.mp3")),
        wheat_6(SoundData("sound/animals/wheat_6.mp3")),
    }

    data class SoundData(
        val path: String,
    ) {
        lateinit var sound: Sound
    }

}