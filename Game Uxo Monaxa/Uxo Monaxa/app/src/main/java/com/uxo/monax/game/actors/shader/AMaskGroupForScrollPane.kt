package com.uxo.monax.game.actors.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.uxo.monax.game.utils.advanced.AdvancedGroup
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import com.uxo.monax.game.utils.disposeAll

class AMaskGroupForScrollPane(
    override val screen: AdvancedScreen,
    private val maskTexture: Texture = screen.drawerUtil.getTexture(Color.BLACK),
): AdvancedGroup() {

    companion object {
        private var vertexShader   = Gdx.files.internal("shader/defaultVS.glsl").readString()
        private var fragmentShader = Gdx.files.internal("shader/maskFS.glsl").readString()
    }

    private var shaderProgram: ShaderProgram? = null

    private var fboGroup: FrameBuffer? = null
    private var fboMask : FrameBuffer? = null

    private var textureGroup: TextureRegion? = null
    private var textureMask : TextureRegion? = null

    private var camera = OrthographicCamera()

    private var screenXInPixels      = 0
    private var screenYInPixels      = 0
    private var screenWidthInPixels  = 0
    private var screenHeightInPixels = 0
    private var screenWidthInWorld   = 0f
    private var screenHeightInWorld  = 0f

    private var colorWhite     = Color.WHITE
    private val globalPosition = Vector2()

    override fun addActorsOnGroup() {
        localToStageCoordinates(globalPosition)

        createShaders()
        createFrameBuffer()
    }

    init {
        /*addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                touchDragged(event, x, y, pointer)
                return true
            }

            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                super.touchDragged(event, x, y, pointer)
                children.last().also {
                    it.x = x
                    it.y = y
                }
            }
        })*/
    }

    private val frameBatch = SpriteBatch()

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (batch         == null ||
            shaderProgram == null ||
            fboGroup      == null || fboMask     == null ||
            textureGroup  == null || textureMask == null
        ) return

        val oldGroupAlpha = color.a
        localToStageCoordinates(globalPosition.set(0f, 0f))

        batch.end()

        // draw fboMask -------------------------------

        fboMask!!.begin()
        ScreenUtils.clear(Color.CLEAR) //GREEN.apply { a = 0.5f })
        frameBatch.begin()
        frameBatch.projectionMatrix = camera.combined

        frameBatch.draw(
            maskTexture,
            globalPosition.x, globalPosition.y,
            originX, originY,
            width, height,
            scaleX, scaleY,
            rotation,
            0, 0,
            maskTexture.width, maskTexture.height,
            false, false
        )

        frameBatch.end()
        fboMask!!.end(screenXInPixels, screenYInPixels, screenWidthInPixels, screenHeightInPixels)

        // draw fboGroup -------------------------------

        fboGroup!!.begin()
        ScreenUtils.clear(Color.CLEAR) // PINK.apply { a = 0.5f })
        frameBatch.begin()
        frameBatch.projectionMatrix = camera.combined

        color.a = 1f
        frameBatch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA) //pre-multiplied alpha ShaderProgram
        super.draw(frameBatch, 1f)
        frameBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); //default blend mode
        color.a = oldGroupAlpha

        frameBatch.end()
        fboGroup!!.end(screenXInPixels, screenYInPixels, screenWidthInPixels, screenHeightInPixels)

        // draw Result -------------------------------

        frameBatch.begin()
        frameBatch.shader = shaderProgram

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1)
        textureMask!!.texture.bind(1)
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        textureGroup!!.texture.bind(0)

        shaderProgram!!.setUniformi("u_mask", 1)
        shaderProgram!!.setUniformi("u_texture", 0)

        frameBatch.color = color.apply { a *= parentAlpha }
        color.a = oldGroupAlpha

        frameBatch.draw(textureGroup, 0f, 0f, screenWidthInWorld, screenHeightInWorld)

        frameBatch.end()

        frameBatch.color = colorWhite
        frameBatch.shader = null

        batch.begin()
    }

    override fun dispose() {
        super.dispose()
        disposeAll(
            shaderProgram,
            fboGroup,
            fboMask,
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
        parent.stage.viewport.apply {
            screenXInPixels      = screenX
            screenYInPixels      = screenY
            screenWidthInPixels  = screenWidth
            screenHeightInPixels = screenHeight
            screenWidthInWorld   = worldWidth
            screenHeightInWorld  = worldHeight
        }

        camera = OrthographicCamera(screenWidthInWorld, screenHeightInWorld)
        camera.setToOrtho(false, screenWidthInWorld, screenHeightInWorld)

        // val pos = localToStageCoordinates(Vector2())
        // camera.position.set(pos.x + (screenWidthInWorld / 2), pos.y + (screenHeightInWorld / 2), 0f)
        // camera.update()

        fboGroup = FrameBuffer(Pixmap.Format.RGBA8888, screenWidthInPixels, screenHeightInPixels, false)
        fboMask  = FrameBuffer(Pixmap.Format.RGBA8888, screenWidthInPixels, screenHeightInPixels, false)

        textureGroup = TextureRegion(fboGroup!!.colorBufferTexture)
        textureGroup!!.flip(false, true)
        textureMask = TextureRegion(fboMask!!.colorBufferTexture)
        textureMask!!.flip(false, true)
    }

}