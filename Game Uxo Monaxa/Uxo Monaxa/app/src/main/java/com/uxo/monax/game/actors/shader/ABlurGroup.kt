//package com.uxo.monax.game.actors.shader
//
//import com.badlogic.gdx.Gdx
//import com.badlogic.gdx.graphics.Color
//import com.badlogic.gdx.graphics.GL20
//import com.badlogic.gdx.graphics.OrthographicCamera
//import com.badlogic.gdx.graphics.Pixmap
//import com.badlogic.gdx.graphics.g2d.Batch
//import com.badlogic.gdx.graphics.g2d.TextureRegion
//import com.badlogic.gdx.graphics.glutils.FrameBuffer
//import com.badlogic.gdx.graphics.glutils.ShaderProgram
//import com.badlogic.gdx.utils.ScreenUtils
//import com.uxo.monax.game.utils.advanced.AdvancedScreen
//import com.uxo.monax.game.utils.disposeAll
//
//class ABlurGroup(
//    override val screen: AdvancedScreen,
//): AdGr() {
//
//    companion object {
//        private var vertexShader   = Gdx.files.internal("shader/defaultVS.glsl").readString()
//        private var fragmentShader = Gdx.files.internal("shader/gaussianBlurFS.glsl").readString()
//    }
//
//    private var shaderProgram: ShaderProgram? = null
//
//    private var fboGroup    : FrameBuffer?   = null
//    private var fboBlurH    : FrameBuffer?   = null
//    private var fboBlurV    : FrameBuffer?   = null
//    private var textureGroup: TextureRegion? = null
//    private var textureBlurV: TextureRegion? = null
//    private var textureBlurH: TextureRegion? = null
//
//    private var camera = OrthographicCamera()
//
//    var radiusBlur = 0f
//
//    override fun addActorsOnGroup() {
//        createShaders()
//        createFrameBuffer()
//    }
//
//    override fun draw(batch: Batch?, parentAlpha: Float) {
//        if (batch         == null ||
//            shaderProgram == null ||
//            fboGroup      == null || fboBlurH == null || fboBlurV == null
//        ) return
//
//        batch.end()
//
//        // 1. Рендеримо акторів у fboGroup
//        fboGroup!!.begin()
//        ScreenUtils.clear(Color.CLEAR, true)
//
//        batch.begin()
//
//        batch.projectionMatrix = camera.combined
//        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE)
//
//        tmpTransformMatrix.set(batch.transformMatrix)
//        batch.transformMatrix = identityMatrix
//
//        drawChildrenToFbo(batch, parentAlpha)
//
//        batch.transformMatrix = tmpTransformMatrix
//
//        batch.end()
//        fboGroup!!.end()
//        stage.viewport.apply()
//        batch.color = Color.WHITE
//
//        // 2. Горизонтальне розмиття у fboBlur
//        batch.applyBlur(fboBlurH, textureGroup, 1f, 0f)
//        batch.applyBlur(fboBlurV, textureBlurH, 0f, 1f)
//
//        //batch.applyBlur(fboBlurH, textureBlurV, 0.707f, 0.707f)
//        //batch.applyBlur(fboBlurV, textureBlurH, -0.707f, -0.707f)
//
//        batch.applyBlur(fboBlurH, textureBlurV, 0.383f, 0.924f)
//        batch.applyBlur(fboBlurV, textureBlurH, 0.924f, 0.383f)
//
//        //batch.applyBlur(fboBlurH, textureBlurV, 1f, 0f)
//        //batch.applyBlur(fboBlurV, textureBlurH, 0f, 1f)
//
//        // 3. Вертикальне розмиття та фінальний рендер на екран
//        batch.begin()
//
//        batch.projectionMatrix = stage.camera.combined
//        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)
//
//        batch.shader = null
//
//        batch.draw(
//            textureBlurV,
//            x, y,
//            originX, originY,
//            width, height,
//            scaleX, scaleY,
//            rotation,
//        )
//
//        batch.end()
//        batch.begin()
//
//        batch.shader = null
//        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
//    }
//
//    override fun dispose() {
//        super.dispose()
//        disposeAll(
//            shaderProgram,
//            fboGroup, fboBlurH, fboBlurV
//        )
//    }
//
//    // Logic ------------------------------------------------------------------------
//
//    private fun createShaders() {
//        ShaderProgram.pedantic = true
//        shaderProgram = ShaderProgram(vertexShader, fragmentShader)
//
//        if (shaderProgram?.isCompiled == false) {
//            throw IllegalStateException("shader compilation failed:\n" + shaderProgram?.log)
//        }
//    }
//
//    private fun createFrameBuffer() {
//        camera = OrthographicCamera(width, height)
//        camera.position.set(width / 2f, height / 2f, 0f)
//        camera.update()
//
//        fboGroup = FrameBuffer(Pixmap.Format.RGBA8888, (width).toInt(), (height).toInt(), false)
//        fboBlurH = FrameBuffer(Pixmap.Format.RGBA8888, (width).toInt(), (height).toInt(), false)
//        fboBlurV = FrameBuffer(Pixmap.Format.RGBA8888, (width).toInt(), (height).toInt(), false)
//
//        textureGroup = TextureRegion(fboGroup!!.colorBufferTexture)
//        textureBlurH = TextureRegion(fboBlurH!!.colorBufferTexture)
//        textureBlurV = TextureRegion(fboBlurV!!.colorBufferTexture)
//
//        textureGroup!!.flip(false, true)
//        textureBlurH!!.flip(false, true)
//        textureBlurV!!.flip(false, true)
//    }
//
//    private fun Batch.applyBlur(fbo: FrameBuffer?, textureRegion: TextureRegion?, dH: Float, dV: Float) {
//        fbo!!.begin()
//        ScreenUtils.clear(Color.CLEAR, true)
//
//        begin()
//
//        setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)
//
//        shader = shaderProgram
//        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
//        textureRegion!!.texture.bind(0)
//        shaderProgram!!.setUniformi("u_texture", 0)
//        shaderProgram!!.setUniformf("u_groupSize", fbo.width.toFloat(), fbo.height.toFloat())
//        shaderProgram!!.setUniformf("u_blurAmount", radiusBlur)
//        shaderProgram!!.setUniformf("u_direction", dH, dV)
//
//        tmpTransformMatrix.set(transformMatrix)
//        transformMatrix = identityMatrix
//
//        draw(textureRegion, 0f, 0f, fbo.width.toFloat(), fbo.height.toFloat())
//
//        transformMatrix = tmpTransformMatrix
//
//        end()
//        fbo.end()
//        stage.viewport.apply()
//        color = Color.WHITE
//    }
//
//}