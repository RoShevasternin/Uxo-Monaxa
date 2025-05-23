package com.uxo.monax.game.screens.test

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.uxo.monax.game.utils.advanced.AdvancedGroup
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import com.uxo.monax.game.utils.disposeAll

class EtalonMaskGroup(
    override val screen: AdvancedScreen,
    private val maskTexture: Texture = screen.drawerUtil.getTexture(Color.BLACK),
): AdvancedGroup() {

    companion object {
        private var vertexShader   = Gdx.files.internal("shader/defaultVS.glsl").readString()
        private var fragmentShader = Gdx.files.internal("shader/maskFS.glsl").readString()
    }

    private var shaderProgram: ShaderProgram? = null

    private var fboGroup    : FrameBuffer? = null
    private var textureGroup: TextureRegion? = null

    private var camera = OrthographicCamera()

    private var screenXInPixels      = 0
    private var screenYInPixels      = 0
    private var screenWidthInPixels  = 0
    private var screenHeightInPixels = 0
    private var screenWidthInWorld   = 0f
    private var screenHeightInWorld  = 0f

    private val globalPosition = Vector2()
    private val tmpVector2     = Vector2(0f, 0f)

    override fun addActorsOnGroup() {
        createShaders()
        createFrameBuffer()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (batch         == null ||
            shaderProgram == null ||
            fboGroup      == null
        ) return

        batch.end()

        globalPosition.set(localToStageCoordinates(tmpVector2.set(0f, 0f)))
        camera.position.set(
            globalPosition.x + width / 2f,
            globalPosition.y + height / 2f,
            0f
        )
        camera.update()

        saveViewportSize()

        // draw fboGroup -------------------------------

        //SpriteBatch().setBlendFunction()
        //batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)
        //batch.setBlendFunction(GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        //batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_SRC_ALPHA, GL20.GL_ONE)

        fboGroup!!.begin()
        ScreenUtils.clear(Color.CLEAR)
        GL20.GL_TEXTURE0
        batch.begin()
        batch.projectionMatrix = camera.combined

        batch.setBlendFunction(GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        super.draw(batch, parentAlpha)

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch.color = Color.WHITE

        batch.projectionMatrix = stage.camera.combined

        batch.end()
        fboGroup!!.end(screenXInPixels, screenYInPixels, screenWidthInPixels, screenHeightInPixels)

        // draw Result -------------------------------

        batch.begin()

        batch.shader = shaderProgram

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1)
        maskTexture.bind(1)
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        textureGroup!!.texture.bind(0)

        shaderProgram!!.setUniformi("u_mask", 1)
        shaderProgram!!.setUniformi("u_texture", 0)

        batch.draw(
            textureGroup,
            x, y,
            originX, originY,
            width, height,
            scaleX, scaleY,
            rotation,
        )

        batch.shader = null

        batch.end()

        batch.begin()
    }

    override fun dispose() {
        super.dispose()
        disposeAll(
            shaderProgram,
            fboGroup,
        )
    }

    // Logic ------------------------------------------------------------------------

    private fun createShaders() {
        ShaderProgram.pedantic = false
        shaderProgram = ShaderProgram(vertexShader, fragmentShader)

        if (shaderProgram?.isCompiled == false) {
            throw IllegalStateException("shader compilation failed:\n" + shaderProgram?.log)
        }
    }

    private fun createFrameBuffer() {
        //camera = OrthographicCamera(width, height)
        //camera.setToOrtho(false, width, height)

        camera = OrthographicCamera(width, height)
        camera.position.set(x + (width / 2f), y + (height / 2f), 0f)
        camera.update()

        fboGroup = FrameBuffer(Pixmap.Format.RGBA8888, width.toInt(), height.toInt(), false)

        textureGroup = TextureRegion(fboGroup!!.colorBufferTexture)
        textureGroup!!.flip(false, true)
    }

    private fun saveViewportSize() {
        stage.viewport.apply {
            screenXInPixels      = screenX
            screenYInPixels      = screenY
            screenWidthInPixels  = screenWidth
            screenHeightInPixels = screenHeight
            screenWidthInWorld   = worldWidth
            screenHeightInWorld  = worldHeight
        }
    }

}