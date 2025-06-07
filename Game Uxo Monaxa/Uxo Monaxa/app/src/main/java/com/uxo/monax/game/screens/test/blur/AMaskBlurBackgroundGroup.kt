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
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.uxo.monax.game.utils.actor.getTopParent
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import com.uxo.monax.game.utils.advanced.preRenderGroup.FboPreRender
import com.uxo.monax.game.utils.advanced.preRenderGroup.PreRenderableGroup

class AMaskBlurBackgroundGroup(
    override val screen: AdvancedScreen,
    private val maskTexture: Texture? = null
): PreRenderableGroup() {

    companion object {
        private var vertexShader       = Gdx.files.internal("shader/defaultVS.glsl").readString()
        private var fragmentShaderBlur = Gdx.files.internal("shader/gaussianBlurFS.glsl").readString()
        private var fragmentShaderMask = Gdx.files.internal("shader/maskFS.glsl").readString()
    }

    private var shaderProgramBlur: ShaderProgram? = null
    private var shaderProgramMask: ShaderProgram? = null

    private var fboSceneBack: FrameBuffer?   = null
    private var fboSceneUI  : FrameBuffer?   = null
    private var fboScene    : FrameBuffer?   = null

    private var textureSceneBack: TextureRegion? = null
    private var textureSceneUI  : TextureRegion? = null
    private var textureScene    : TextureRegion? = null

    private var isBlurEnabled = false

    var radiusBlur = 0f
        set(value) {
            isBlurEnabled = (value != 0f)
            field = value
        }

    private val groupPosition = Vector2()
    private val tmpVector2    = Vector2(0f, 0f)

    override fun addActorsOnGroup() {
        createShaders()
        createFrameBuffer()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (isBlurEnabled) super.draw(batch, parentAlpha)
    }

    override fun getFboPreRender() = object : FboPreRender {
        override fun renderFboGroup(batch: Batch, combinedAlpha: Float) {}

        override fun applyEffect(batch: Batch, combinedAlpha: Float) {
            if (isBlurEnabled.not()) return

            batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)

            batch.applyBlur(fboSceneBack, textureScene, 1f, 0f)
            batch.applyBlur(fboSceneUI, textureSceneBack, 0f, 1f)

            batch.applyBlur(fboSceneBack, textureSceneUI, 0.707f, 0.707f)
            batch.applyBlur(fboSceneUI, textureSceneBack, -0.707f, -0.707f)

            batch.applyBlur(fboSceneBack, textureSceneUI, 0.383f, 0.924f)
            batch.applyBlur(fboSceneUI, textureSceneBack, 0.924f, 0.383f)

            //batch.applyBlur(fboBlurH, textureBlurV, 1f, 0f)
            //batch.applyBlur(fboBlurV, textureBlurH, 0f, 1f)

            if (maskTexture != null) batch.applyMask(fboSceneBack!!)
        }

        override fun renderFboResult(batch: Batch, combinedAlpha: Float) {
            batch.draw(if (maskTexture == null) textureSceneUI else textureSceneBack, 0f, 0f, width, height)
        }
    }

    override fun preRender(batch: Batch, parentAlpha: Float) {
        if (isBlurEnabled.not()) return

        if (fboSceneBack == null || fboSceneUI == null || fboScene == null) throw Exception("Error preRender: ${this::class.simpleName}")

        batch.end()

        captureScreenBack(batch)
        captureScreenUI(batch)
        captureScreenAll(batch)

        batch.begin()

        super.preRender(batch, parentAlpha)
    }

    override fun dispose() {
        super.dispose()
        //disposeAll(fboSceneBack, fboSceneUI, fboScene)
    }

    // Logic ------------------------------------------------------------------------

    private fun createShaders() {
        ShaderProgram.pedantic = true

        shaderProgramBlur = ShaderProgram(vertexShader, fragmentShaderBlur)
        shaderProgramMask = ShaderProgram(vertexShader, fragmentShaderMask)

        fun throwException(shaderProgram: ShaderProgram?) {
            throw IllegalStateException("shader compilation failed:\n" + shaderProgram?.log)
        }

        when {
            shaderProgramBlur?.isCompiled == false -> throwException(shaderProgramBlur)
            shaderProgramMask?.isCompiled == false -> throwException(shaderProgramMask)
        }
    }

    override fun createFrameBuffer() {
        super.createFrameBuffer()

        fboSceneBack = FrameBuffer(Pixmap.Format.RGBA8888, width.toInt(), height.toInt(), false)
        fboSceneUI   = FrameBuffer(Pixmap.Format.RGBA8888, width.toInt(), height.toInt(), false)
        fboScene     = FrameBuffer(Pixmap.Format.RGBA8888, width.toInt(), height.toInt(), false)

        textureSceneBack = TextureRegion(fboSceneBack!!.colorBufferTexture).apply { flip(false, true) }
        textureSceneUI   = TextureRegion(fboSceneUI!!.colorBufferTexture).apply { flip(false, true) }
        textureScene     = TextureRegion(fboScene!!.colorBufferTexture).apply { flip(false, true) }
    }

    private fun captureScreenBack(batch: Batch) {
        screen.viewportBack.apply()
        groupPosition.set(localToStageCoordinates(tmpVector2.set(0f, 0f)))

        // 1. Захоплюємо всю сцену до рендеру групи
        fboSceneBack!!.begin()
        ScreenUtils.clear(Color.CLEAR, true)

        // Отримуємо екранні координати (перетворюємо їх у пікселі)
        val bottomLeft = Vector2(groupPosition.x, groupPosition.y)
        val topRight   = Vector2(groupPosition.x + width, groupPosition.y + height)

        // Конвертуємо координати з FitViewport у ScreenViewport
        screen.viewportUI.project(bottomLeft)
        screen.viewportUI.project(topRight)

        // Отримуємо екранні значення
        val screenX      = bottomLeft.x
        val screenY      = bottomLeft.y
        val screenWidth  = topRight.x - bottomLeft.x
        val screenHeight = topRight.y - bottomLeft.y

        camera.setToOrtho(false, screenWidth, screenHeight)
        camera.position.set(screenX + screenWidth / 2f, screenY + screenHeight / 2f, 0f)
        camera.update()

        batch.begin()
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE)

        batch.withMatrix(camera.combined, identityMatrix) {
            isVisible = false
            screen.stageBack.root.draw(batch, 1f)
            isVisible = true
        }

        batch.end()
        fboSceneBack!!.endAdvanced(batch)
    }

    private fun captureScreenUI(batch: Batch) {
        screen.stageUI.viewport.apply()
        groupPosition.set(localToStageCoordinates(tmpVector2.set(0f, 0f)))

        // 1. Захоплюємо всю сцену до рендеру групи
        fboSceneUI!!.begin()
        ScreenUtils.clear(Color.CLEAR, true)

        camera.setToOrtho(false, width, height)
        camera.position.set(groupPosition.x + (width / 2f), groupPosition.y + (height / 2f), 0f)
        camera.update()

        batch.begin()
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE)

        batch.withMatrix(camera.combined, identityMatrix) {
            val currentTopParent = getTopParent(screen.stageUI.root)

            if (currentTopParent == screen.stageUI.root) {
                isVisible = false
                screen.stageUI.root.draw(batch, 1f)
                isVisible = true
            } else {
                currentTopParent.isVisible = false
                screen.stageUI.root.draw(batch, 1f)
                currentTopParent.isVisible = true
            }
        }

        batch.end()
        fboSceneUI!!.endAdvanced(batch)
    }

    private fun captureScreenAll(batch: Batch) {
        fboScene!!.begin()
        ScreenUtils.clear(Color.CLEAR, true)

        camera.setToOrtho(false, width, height)
        camera.position.set(width / 2f, height / 2f, 0f)
        camera.update()

        batch.begin()
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)

        batch.withMatrix(camera.combined, identityMatrix) {
            batch.draw(textureSceneBack, 0f, 0f, width, height)
            batch.draw(textureSceneUI, 0f, 0f, width, height)
        }

        batch.end()
        fboScene!!.endAdvanced(batch)
    }

    private fun Batch.applyBlur(fbo: FrameBuffer?, textureRegion: TextureRegion?, dH: Float, dV: Float) {
        fbo!!.begin()
        ScreenUtils.clear(Color.CLEAR, true)
        begin()

        shader = shaderProgramBlur
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        textureRegion!!.texture.bind(0)
        shaderProgramBlur!!.setUniformi("u_texture", 0)
        shaderProgramBlur!!.setUniformf("u_groupSize", fbo.width.toFloat(), fbo.height.toFloat())
        shaderProgramBlur!!.setUniformf("u_blurAmount", radiusBlur)
        shaderProgramBlur!!.setUniformf("u_direction", dH, dV)

        withMatrix(camera.combined, identityMatrix) {
            draw(textureRegion, 0f, 0f, fbo.width.toFloat(), fbo.height.toFloat())
        }

        end()
        fbo.endAdvanced(this)
    }

    private fun Batch.applyMask(fbo: FrameBuffer) {
        fbo.begin()
        ScreenUtils.clear(Color.CLEAR, true)
        begin()

        setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        shader = shaderProgramMask

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1)
        maskTexture!!.bind(1)
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
        textureSceneUI!!.texture.bind(0)

        shaderProgramMask!!.setUniformi("u_mask", 1)
        shaderProgramMask!!.setUniformi("u_texture", 0)

        withMatrix(camera.combined, identityMatrix) {
            //draw(textureSceneUI, 0f, 0f, fbo.width.toFloat(), fbo.height.toFloat())

            draw(
                textureSceneUI,
                0f, 0f,
                originX, originY,
                width, height,
                scaleX, scaleY,
                -45f,
            )
        }

        end()
        fbo.endAdvanced(this)
    }

}