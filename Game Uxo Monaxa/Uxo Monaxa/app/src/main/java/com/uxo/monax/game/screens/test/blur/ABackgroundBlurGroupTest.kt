package com.uxo.monax.game.screens.test.blur

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import com.uxo.monax.game.utils.advanced.preRenderGroup.FboPreRender
import com.uxo.monax.game.utils.advanced.preRenderGroup.PreRenderableGroup
import com.uxo.monax.game.utils.disposeAll
import com.uxo.monax.util.log

class ABackgroundBlurGroupTest(
    override val screen: AdvancedScreen,
): ABlurGroupTest(screen) {

    private var fboSceneBack: FrameBuffer?   = null
    private var fboSceneUI  : FrameBuffer?   = null
    private var fboScene    : FrameBuffer?   = null

    private var textureSceneBack: TextureRegion? = null
    private var textureSceneUI  : TextureRegion? = null
    private var textureScene    : TextureRegion? = null

    private val groupPosition = Vector2()
    private val tmpVector2    = Vector2(0f, 0f)

    override fun addActorsOnGroup() {
        createFrameBuffer()
        super.addActorsOnGroup()
    }

    override fun preRender(batch: Batch, parentAlpha: Float) {
        if (fboSceneBack == null || fboSceneUI == null || fboScene == null) throw Exception("Error preRender: ${this::class.simpleName}")

        batch.end()

        captureScreenBack(batch)
        captureScreenUI(batch)
        captureScreenAll(batch)

        textureRegionBlur = textureScene

        batch.begin()

        super.preRender(batch, parentAlpha)
    }

    override fun dispose() {
        super.dispose()
        disposeAll(fboSceneBack, fboSceneUI, fboScene)
    }

    // Logic ------------------------------------------------------------------------

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
            log("parent = ${parent == screen.stageUI.root}")
            if (parent == screen.stageUI.root) {
                isVisible = false
                screen.stageUI.root.draw(batch, 1f)
                isVisible = true
            } else {
                parent.isVisible = false
                screen.stageUI.root.draw(batch, 1f)
                parent.isVisible = true
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

}