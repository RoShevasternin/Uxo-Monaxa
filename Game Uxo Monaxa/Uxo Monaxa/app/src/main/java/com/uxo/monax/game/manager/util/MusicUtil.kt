package com.uxo.monax.game.manager.util

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.utils.Disposable
import com.uxo.monax.game.manager.AudioManager
import com.uxo.monax.game.utils.runGDX
import com.uxo.monax.util.cancelCoroutinesAll
import com.uxo.monax.game.manager.MusicManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MusicUtil: Disposable {

    private val coroutine = CoroutineScope(Dispatchers.Default)

    private val music_1 = MusicManager.EnumMusic.music_1.data.music
    private val music_2 = MusicManager.EnumMusic.music_2.data.music
    private val music_3 = MusicManager.EnumMusic.music_3.data.music

    val listMusic = listOf(music_1, music_2, music_3)

    // 0..100
    val volumeLevelFlow = MutableStateFlow(AudioManager.volumeLevelPercent)

    var coff = 1f

    private var lastMusic: Music? = null
    var currentMusic: Music?
        get() = lastMusic
        set(value) { runGDX {
            if (value != null) {
                if (lastMusic != value) {
                    lastMusic?.stop()
                    lastMusic = value
                    lastMusic?.volume = (volumeLevelFlow.value / 100f) * coff
                    lastMusic?.play()
                }
            } else {
                lastMusic?.stop()
                lastMusic = null
            }
        } }

    init {
        coroutine.launch { volumeLevelFlow.collect { level -> runGDX { lastMusic?.volume = (level / 100f) * coff } } }
    }

    override fun dispose() {
        cancelCoroutinesAll(coroutine)
        lastMusic = null
        currentMusic  = null
    }

}