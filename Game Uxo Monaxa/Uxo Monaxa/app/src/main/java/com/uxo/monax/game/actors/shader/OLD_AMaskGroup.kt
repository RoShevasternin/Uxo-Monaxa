//package com.uxo.monax.game.actors.shader
//
//import com.badlogic.gdx.Gdx
//import com.badlogic.gdx.graphics.*
//import com.badlogic.gdx.graphics.g2d.Batch
//import com.badlogic.gdx.graphics.g2d.SpriteBatch
//import com.badlogic.gdx.graphics.g2d.TextureRegion
//import com.badlogic.gdx.graphics.glutils.FrameBuffer
//import com.badlogic.gdx.graphics.glutils.ShaderProgram
//import com.badlogic.gdx.utils.ScreenUtils
//import com.uxo.monax.game.utils.advanced.AdvancedGroup
//import com.uxo.monax.game.utils.advanced.AdvancedScreen
//import com.uxo.monax.game.utils.disposeAll
//
//class AMaskGroup(
//    override val screen: AdvancedScreen,
//    private val maskTexture: Texture = screen.drawerUtil.getTexture(Color.BLACK),
//): AdvancedGroup() {
//
//    companion object {
//        private var vertexShader   = Gdx.files.internal("shader/defaultVS.glsl").readString()
//        private var fragmentShader = Gdx.files.internal("shader/gaussianBlurFS.glsl").readString()
//    }
//
//    private var shaderProgram: ShaderProgram? = null
//
//    private var fboMask : FrameBuffer? = null
//    private var fboGroup: FrameBuffer? = null
//
//    private var textureMask : TextureRegion? = null
//    private var textureGroup: TextureRegion? = null
//
//    private var camera = OrthographicCamera()
//
//    private var screenXInPixels      = 0
//    private var screenYInPixels      = 0
//    private var screenWidthInPixels  = 0
//    private var screenHeightInPixels = 0
//    private var screenWidthInWorld   = 0f
//    private var screenHeightInWorld  = 0f
//
//    private var oldGroupAlpha = 0f
//    //private var oldChildAlpha = 0f
//
//    override fun addActorsOnGroup() {
//        createShaders()
//        createFrameBuffer()
//    }
//
//    init {
//        /*addListener(object : InputListener() {
//            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
//                touchDragged(event, x, y, pointer)
//                return true
//            }
//
//            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
//                super.touchDragged(event, x, y, pointer)
//                children.last().also {
//                    it.x = x
//                    it.y = y
//                }
//            }
//        })*/
//    }
//
//    private val batchFrameMask  = SpriteBatch()
//    private val batchFrameGroup = SpriteBatch()
//    private val batchResult     = SpriteBatch()
//
//    override fun draw(batch: Batch?, parentAlpha: Float) {
//        if (batch         == null ||
//            shaderProgram == null ||
//            fboGroup      == null || fboMask     == null ||
//            textureGroup  == null || textureMask == null
//        ) return
//
//        batch.end()
//
//        // draw fboMask -------------------------------
//
//        fboMask!!.begin()
//        ScreenUtils.clear(Color.CLEAR)    //GREEN.apply { a = 0.5f })
//        batchFrameMask.begin()
//        batchFrameMask.projectionMatrix = camera.combined
//
//        batchFrameMask.draw(maskTexture, 0f, 0f, width, height)
//
//        batchFrameMask.end()
//        fboMask!!.end(screenXInPixels, screenYInPixels, screenWidthInPixels, screenHeightInPixels)
//
//        // draw fboGroup -------------------------------
//
//        fboGroup!!.begin()
//        ScreenUtils.clear(Color.CLEAR)    //PINK.apply { a = 0.5f })
//        batchFrameGroup.begin()
//        batchFrameGroup.projectionMatrix = camera.combined
//        children.onEach { child ->
//            //oldChildAlpha = child.color.a
//            //child.color.a = 1f
//            batchFrameGroup.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA) //pre-multiplied alpha ShaderProgram
//            child.draw(batchFrameGroup, 1f)
//            batchFrameGroup.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA); //default blend mode
//            //child.color.a = oldChildAlpha
//        }
//
//        batchFrameGroup.end()
//        fboGroup!!.end(screenXInPixels, screenYInPixels, screenWidthInPixels, screenHeightInPixels)
//
//        // draw Result -------------------------------
//
//        batchResult.begin()
//        batchResult.projectionMatrix = batch.projectionMatrix
//        batchResult.transformMatrix  = batch.transformMatrix
//
//        batchResult.shader = shaderProgram
//
//        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1)
//        textureMask!!.texture.bind(1)
//        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
//        textureGroup!!.texture.bind(0)
//
//        shaderProgram!!.setUniformi("u_mask", 1)
//        shaderProgram!!.setUniformi("u_texture", 0)
//
//        oldGroupAlpha = color.a
//        batchResult.color = color.apply { a *= parentAlpha }
//        color.a = oldGroupAlpha
//
//        batchResult.draw(
//            textureGroup,
//            x, y,
//            originX, originY,
//            width, height,
//            scaleX, scaleY,
//            rotation,
//        )
//
//        batchResult.end()
//
//        batch.begin()
//    }
//
//    override fun dispose() {
//        super.dispose()
//        disposeAll(
//            shaderProgram,
//            fboGroup, fboMask,
//            batchFrameMask, batchFrameGroup, batchResult,
//        )
//    }
//
//    // Logic ------------------------------------------------------------------------
//
//    private fun createShaders() {
//        ShaderProgram.pedantic = false
//        shaderProgram = ShaderProgram(vertexShader, fragmentShader)
//
//        if (shaderProgram?.isCompiled == false) {
//            throw IllegalStateException("shader compilation failed:\n" + shaderProgram?.log)
//        }
//    }
//
//    private fun createFrameBuffer() {
//        stage.viewport.apply {
//            screenXInPixels      = screenX
//            screenYInPixels      = screenY
//            screenWidthInPixels  = screenWidth
//            screenHeightInPixels = screenHeight
//            screenWidthInWorld   = worldWidth
//            screenHeightInWorld  = worldHeight
//        }
//
//        camera = OrthographicCamera(width, height)
//        camera.setToOrtho(false, width, height)
//
//        // cameraGroup = OrthographicCamera(width, height)
//        // cameraGroup.position.set(x + (width / 2f), y + (height / 2f), 0f)
//        // cameraGroup.update()
//
//        fboMask  = FrameBuffer(Pixmap.Format.RGBA8888, width.toInt(), height.toInt(), false)
//        fboGroup = FrameBuffer(Pixmap.Format.RGBA8888, width.toInt(), height.toInt(), false)
//
//        textureMask = TextureRegion(fboMask!!.colorBufferTexture)
//        textureMask!!.flip(false, true)
//        textureGroup = TextureRegion(fboGroup!!.colorBufferTexture)
//        textureGroup!!.flip(false, true)
//    }
//
//}