package com.uxo.monax.game.utils.advanced.preRenderGroup

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.utils.ScreenUtils
import com.uxo.monax.game.utils.advanced.AdvancedGroup
import com.uxo.monax.game.utils.disposeAll

abstract class PreRenderableGroup: AdvancedGroup(), PreRenderable {

    protected var fboGroup  : FrameBuffer? = null
    protected var fboResult : FrameBuffer? = null

    var textureGroup : TextureRegion? = null
        private set
    var textureResult: TextureRegion? = null
        private set

    protected val identityMatrix: Matrix4 = Matrix4().idt()

    protected var camera = OrthographicCamera()

    protected var combinedAlpha: Float = 1f

    private val fboPreRender: FboPreRender = getFboPreRender()


    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (isVisible.not()) return
        if (batch == null) throw Exception("Error draw: ${this::class.simpleName}")

        batch.end()
        batch.begin()

        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)

        batch.draw(
            textureResult,
            x, y,
            originX, originY,
            width, height,
            scaleX, scaleY,
            rotation,
        )

        batch.end()
        batch.begin()
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun dispose() {
        super.dispose()
        disposeAll(fboGroup, fboResult)
    }

    abstract fun getFboPreRender(): FboPreRender

    override fun preRender(batch: Batch, parentAlpha: Float) {
        if (fboGroup == null || fboResult == null) throw Exception("Error preRender: ${this::class.simpleName}")

        batch.end()

        combinedAlpha = this.color.a * parentAlpha

        // Викликаємо PreRender спочатку у дітей
        batch.begin()
        children.begin()
        for (i in 0 until children.size) {
            val child = children[i]
            renderPreRenderables(child, batch, combinedAlpha)
        }
        children.end()
        batch.end()

        /** RENDER fboGroup - Рендеримо в fboGroup */
        fboGroup!!.begin()
        ScreenUtils.clear(Color.CLEAR, true)
        batch.begin()

        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE)

        batch.withMatrix(camera.combined, identityMatrix) {
            fboPreRender.renderFboGroup(batch, combinedAlpha)
        }

        batch.end()
        fboGroup!!.endAdvanced(batch)

        /** RENDER FBO - Рендеримо в інші FrameBuffer */
        fboPreRender.applyEffect(batch, combinedAlpha)

        /** RENDER fboResult - Рендеримо в fboResult */
        fboResult!!.begin()
        ScreenUtils.clear(Color.CLEAR, true)
        batch.begin()

        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)

        batch.withMatrix(camera.combined, identityMatrix) {
            fboPreRender.renderFboResult(batch, combinedAlpha)
        }

        batch.end()
        fboResult!!.endAdvanced(batch)

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        batch.begin()
    }

    protected open fun drawChildrenSimple(batch: Batch, parentAlpha: Float) {
        children.begin()
        for (i in 0 until children.size) {
            val child = children[i]
            if (child.isVisible) child.draw(batch, parentAlpha)
        }
        children.end()
    }

    protected open fun createFrameBuffer() {
        camera = OrthographicCamera(width, height)
        camera.position.set(width / 2f, height / 2f, 0f)
        camera.update()

        fboGroup  = FrameBuffer(Pixmap.Format.RGBA8888, (width).toInt(), (height).toInt(), false)
        fboResult = FrameBuffer(Pixmap.Format.RGBA8888, (width).toInt(), (height).toInt(), false)

        textureGroup  = TextureRegion(fboGroup!!.colorBufferTexture)
        textureResult = TextureRegion(fboResult!!.colorBufferTexture)

        textureGroup!!.flip(false, true)
        textureResult!!.flip(false, true)
    }

    protected inline fun Batch.withMatrix(newProjectionMatrix: Matrix4, newTransformMatrix: Matrix4, block: () -> Unit) {
        val oldProj  = projectionMatrix
        val oldTrans = transformMatrix
        projectionMatrix = newProjectionMatrix
        transformMatrix  = newTransformMatrix
        block()
        projectionMatrix = oldProj
        transformMatrix  = oldTrans
    }

    open fun FrameBuffer.endAdvanced(batch: Batch) {
        end()
        stage.viewport.apply()

        batch.color  = Color.WHITE
        batch.shader = null
    }

}