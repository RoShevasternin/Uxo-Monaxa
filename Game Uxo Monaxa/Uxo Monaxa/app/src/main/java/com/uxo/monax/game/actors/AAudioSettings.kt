package com.uxo.monax.game.actors

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.uxo.monax.game.actors.progress.AProgressAudio
import com.uxo.monax.game.utils.GameColor
import com.uxo.monax.game.utils.advanced.AdvancedGroup
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import com.uxo.monax.game.utils.font.FontParameter
import com.uxo.monax.game.utils.gdxGame
import kotlinx.coroutines.launch

class AAudioSettings(
    override val screen: AdvancedScreen,
): AdvancedGroup() {

    private val parameter = FontParameter()
        .setCharacters(FontParameter.CharType.NUMBERS)
        .setSize(60)

    private val font60 = screen.fontGenerator_SansitaOne.generateFont(parameter)

    private val ls60 = LabelStyle(font60, GameColor.brown_4f)

    private val imgPanel       = Image(gdxGame.assetsAll.panel_music_sound)
    private val aProgressMusic = AProgressAudio(screen, ls60)
    private val aProgressSound = AProgressAudio(screen, ls60)

    override fun addActorsOnGroup() {
        addPanel()
        addAProgressMusic()
        addAProgressSound()
    }

    // Actors ------------------------------------------------------------------------

    private fun addPanel() {
        addAndFillActor(imgPanel)
    }

    private fun addAProgressMusic() {
        addActor(aProgressMusic)
        aProgressMusic.setBounds(172f, 276f, 747f, 66f)

        aProgressMusic.progressPercentFlow.value = gdxGame.musicUtil.volumeLevelFlow.value

        coroutine?.launch {
            aProgressMusic.progressPercentFlow.collect { volume ->
                gdxGame.musicUtil.volumeLevelFlow.value = volume
            }
        }
    }

    private fun addAProgressSound() {
        addActor(aProgressSound)
        aProgressSound.setBounds(172f, 53f, 747f, 66f)

        aProgressSound.progressPercentFlow.value = gdxGame.soundUtil.volumeLevel

        coroutine?.launch {
            aProgressSound.progressPercentFlow.collect { volume ->
                gdxGame.soundUtil.volumeLevel = volume
            }
        }
    }

}