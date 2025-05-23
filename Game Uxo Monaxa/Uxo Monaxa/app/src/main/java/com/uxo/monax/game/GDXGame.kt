package com.uxo.monax.game

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.ScreenUtils
import com.uxo.monax.MainActivity
import com.uxo.monax.game.dataStore.DS_Balance
import com.uxo.monax.game.manager.*
import com.uxo.monax.game.manager.util.MusicUtil
import com.uxo.monax.game.manager.util.ParticleEffectUtil
import com.uxo.monax.game.manager.util.SoundUtil
import com.uxo.monax.game.manager.util.SpriteUtil
import com.uxo.monax.game.screens.LoaderScreen
import com.uxo.monax.game.utils.GameColor
import com.uxo.monax.game.utils.advanced.AdvancedGame
import com.uxo.monax.game.utils.disposeAll
import com.uxo.monax.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class GDXGame(val activity: MainActivity) : AdvancedGame() {

    lateinit var assetManager     : AssetManager      private set
    lateinit var navigationManager: NavigationManager private set
    lateinit var spriteManager    : SpriteManager     private set
    lateinit var musicManager     : MusicManager      private set
    lateinit var soundManager     : SoundManager      private set
    lateinit var particleEffectManager: ParticleEffectManager private set

    val assetsLoader by lazy { SpriteUtil.Loader() }
    val assetsAll    by lazy { SpriteUtil.All() }

    val musicUtil by lazy { MusicUtil()    }
    val soundUtil by lazy { SoundUtil()    }

    val particleEffectUtil by lazy { ParticleEffectUtil() }

    var backgroundColor = GameColor.background
    val disposableSet   = mutableSetOf<Disposable>()

    val coroutine = CoroutineScope(Dispatchers.Default)

    lateinit var currentBackground: Texture

    val ds_Balance = DS_Balance(coroutine)

    override fun create() {
        navigationManager = NavigationManager()
        assetManager      = AssetManager()
        spriteManager     = SpriteManager(assetManager)

        musicManager      = MusicManager(assetManager)
        soundManager      = SoundManager(assetManager)

        particleEffectManager = ParticleEffectManager(assetManager)

        navigationManager.navigate(LoaderScreen::class.java.name)

    }

    override fun render() {
        ScreenUtils.clear(backgroundColor)
        super.render()
    }

    override fun dispose() {
        try {
            log("dispose LibGDXGame")
            coroutine.cancel()
            disposableSet.disposeAll()
            disposeAll(assetManager, musicUtil)
            super.dispose()
        } catch (e: Exception) { log("exception: ${e.message}") }
    }

}