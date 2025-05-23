package com.uxo.monax.game.utils

import com.badlogic.gdx.audio.Music

class MusicPlayer {

    private val musicFiles = gdxGame.musicUtil.listMusic

    private var musicQueue = mutableListOf<Music>()
    private var lastMusic: Music? = null

    fun startPlayMusic() {
        gdxGame.musicUtil.coff = 0.125f
        shuffleMusic()
        playNext()
    }

    private fun shuffleMusic() {
        musicQueue = musicFiles.shuffled().toMutableList()
        if (musicQueue.first() == lastMusic) {
            musicQueue.add(musicQueue.removeAt(0)) // Пересуваємо перший елемент в кінець, щоб уникнути повтору
        }
    }

    private fun playNext() {
        if (musicQueue.isEmpty()) shuffleMusic() // Якщо список закінчився, перемішуємо заново

        val nextMusic = musicQueue.removeAt(0) // Беремо наступний файл
        lastMusic = nextMusic // Запам'ятовуємо останній зіграний файл

        gdxGame.musicUtil.currentMusic = nextMusic.apply {
            setOnCompletionListener { playNext() } // Запускаємо наступний після завершення
        }
    }
}