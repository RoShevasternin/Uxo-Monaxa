package com.uxo.monax.game.manager

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.g2d.ParticleEffect

class ParticleEffectManager(var assetManager: AssetManager) {

    private val params         = ParticleEffectLoader.ParticleEffectParameter()
    private val particleLoader = ParticleEffectLoader(InternalFileHandleResolver())

    var loadableParticleEffectList = mutableListOf<ParticleEffectData>()

    fun load() {
        assetManager.setLoader(ParticleEffect::class.java, ".p", particleLoader)
        loadableParticleEffectList.onEach { pData -> assetManager.load(pData.path, ParticleEffect::class.java, params.apply {
            atlasFile = pData.pathAtlas
        }) }
    }

    fun init() {
        loadableParticleEffectList.onEach { pData -> pData.effect = assetManager[pData.path, ParticleEffect::class.java] }
        loadableParticleEffectList.clear()
    }

    enum class EnumParticleEffect(val data: ParticleEffectData) {
        FallingLeaves(ParticleEffectData("particle/FallingLeaves.p", "particle/leaf.atlas")),
        test         (ParticleEffectData("particle/test.p", "particle/leaf.atlas")),
    }

    data class ParticleEffectData(
        val path     : String,
        val pathAtlas: String,
    ) {
        lateinit var effect: ParticleEffect
    }

}