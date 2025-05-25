package com.uxo.monax.game.screens.test.blur

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.ScreenUtils
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import com.uxo.monax.game.utils.advanced.preRenderGroup.FboPreRender
import com.uxo.monax.game.utils.advanced.preRenderGroup.PreRenderableGroup
import com.uxo.monax.game.utils.disposeAll
import com.uxo.monax.util.log

open class ABlurGroupTest(
    override val screen: AdvancedScreen,
    var textureRegionBlur: TextureRegion? = null,
): PreRenderableGroup() {

    companion object {
        private var vertexShader   = Gdx.files.internal("shader/defaultVS.glsl").readString()
        private var fragmentShader = Gdx.files.internal("shader/gaussianBlurFS.glsl").readString()
    }

    private var shaderProgram: ShaderProgram? = null

    private var fboBlurH    : FrameBuffer?   = null
    private var fboBlurV    : FrameBuffer?   = null

    private var textureBlurV : TextureRegion? = null
    private var textureBlurH : TextureRegion? = null

    var radiusBlur = 0f

    override fun addActorsOnGroup() {
        createShaders()
        createFrameBuffer()
    }

    override fun dispose() {
        super.dispose()
        disposeAll(
            shaderProgram,
            fboBlurH, fboBlurV
        )
    }

    override fun getFboPreRender() = object : FboPreRender {
        override fun renderFboGroup(batch: Batch, combinedAlpha: Float) {
            if (textureRegionBlur != null) {
                batch.draw(textureRegionBlur, 0f, 0f, width, height)
            } else {
                drawChildrenSimple(batch, combinedAlpha)
            }
        }

        override fun applyEffect(batch: Batch, combinedAlpha: Float) {
            batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)

            batch.applyBlur(fboBlurH, textureGroup, 1f, 0f)
            batch.applyBlur(fboBlurV, textureBlurH, 0f, 1f)

            batch.applyBlur(fboBlurH, textureBlurV, 0.707f, 0.707f)
            batch.applyBlur(fboBlurV, textureBlurH, -0.707f, -0.707f)

            batch.applyBlur(fboBlurH, textureBlurV, 0.383f, 0.924f)
            batch.applyBlur(fboBlurV, textureBlurH, 0.924f, 0.383f)

            //batch.applyBlur(fboBlurH, textureBlurV, 1f, 0f)
            //batch.applyBlur(fboBlurV, textureBlurH, 0f, 1f)
        }

        override fun renderFboResult(batch: Batch, combinedAlpha: Float) {
            batch.draw(textureBlurV, 0f, 0f, width, height)
        }
    }

    override fun preRender(batch: Batch, parentAlpha: Float) {
        if (shaderProgram == null ||
            fboBlurH      == null || fboBlurV == null
        ) throw Exception("Error preRender: ${this::class.simpleName}")

        super.preRender(batch, parentAlpha)
    }

    override fun createFrameBuffer() {
        super.createFrameBuffer()

        fboBlurH  = FrameBuffer(Pixmap.Format.RGBA8888, (width).toInt(), (height).toInt(), false)
        fboBlurV  = FrameBuffer(Pixmap.Format.RGBA8888, (width).toInt(), (height).toInt(), false)

        textureBlurH  = TextureRegion(fboBlurH!!.colorBufferTexture)
        textureBlurV  = TextureRegion(fboBlurV!!.colorBufferTexture)

        textureBlurH!!.flip(false, true)
        textureBlurV!!.flip(false, true)
    }

    // Logic ------------------------------------------------------------------------

    private fun createShaders() {
        ShaderProgram.pedantic = true
        shaderProgram = ShaderProgram(vertexShader, fragmentShader)

        if (shaderProgram?.isCompiled == false) {
            throw IllegalStateException("shader compilation failed:\n" + shaderProgram?.log)
        }
    }

    private fun Batch.applyBlur(fbo: FrameBuffer?, textureRegion: TextureRegion?, dH: Float, dV: Float) {
        fbo!!.begin()
        ScreenUtils.clear(Color.CLEAR, true)
        begin()

        shader = shaderProgram
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        textureRegion!!.texture.bind(0)
        shaderProgram!!.setUniformi("u_texture", 0)
        shaderProgram!!.setUniformf("u_groupSize", fbo.width.toFloat(), fbo.height.toFloat())
        shaderProgram!!.setUniformf("u_blurAmount", radiusBlur)
        shaderProgram!!.setUniformf("u_direction", dH, dV)

        withMatrix(camera.combined, identityMatrix) {
            draw(textureRegion, 0f, 0f, fbo.width.toFloat(), fbo.height.toFloat())
        }

        end()
        fbo.endAdvanced(this)
    }

}