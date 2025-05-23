//package com.uxo.monax.game.screens.test.blur
//
//import com.badlogic.gdx.graphics.Color
//import com.badlogic.gdx.graphics.GL20
//import com.badlogic.gdx.graphics.OrthographicCamera
//import com.badlogic.gdx.graphics.Pixmap
//import com.badlogic.gdx.graphics.g2d.Batch
//import com.badlogic.gdx.graphics.g2d.TextureRegion
//import com.badlogic.gdx.graphics.glutils.FrameBuffer
//import com.badlogic.gdx.math.Vector2
//import com.badlogic.gdx.utils.ScreenUtils
//import com.uxo.monax.game.utils.advanced.AdvancedScreen
//import com.uxo.monax.game.utils.disposeAll
//
//class ABackgroundBlurGroupTest(
//    override val screen: AdvancedScreen,
//): AdvancedFBOGroup() {
//
//    private val aBlurGroup = ABlurGroupTest(screen)
//    //private val aMaskGroup = AMaskGroup(screen, texture)
//
//    private var fboSceneBack: FrameBuffer?   = null
//    private var fboSceneUI  : FrameBuffer?   = null
//    private var fboScene    : FrameBuffer?   = null
//
//    private var textureSceneBack: TextureRegion? = null
//    private var textureSceneUI  : TextureRegion? = null
//    private var textureScene    : TextureRegion? = null
//
//    private var camera = OrthographicCamera()
//
//    private val stagePosition = Vector2()
//    private val tmpVector2    = Vector2(0f, 0f)
//
//    var radiusBlur = 0f
//        set(value) {
//            aBlurGroup.radiusBlur = value
//            field = value
//        }
//
//    override fun addActorsOnGroup() {
//        createFrameBuffer()
//        addAndFillActor(aBlurGroup)
//
//        //aMaskGroup.addAndFillActor(aBlurGroup)
//    }
//
//    override fun draw(batch: Batch?, parentAlpha: Float) {
//        if (batch         == null ||
//            fboSceneBack  == null || fboSceneUI == null || fboScene == null
//        ) return
//
//        batch.end()
//
//        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE)
//        captureScreenBack(batch)
//        captureScreenUI(batch)
//        captureScreenAll(batch)
//
//        // 3. Малюємо розмитий фон під групою та саму групу
//        batch.begin()
//
//        batch.projectionMatrix = stage.camera.combined
//        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)
//
//        //batch.draw(textureScene, x, y, width, height)
//
////        batch.draw(
////            textureScene,
////            x, y,
////            //originX, originY,
////            width, height,
////            //scaleX, scaleY,
////            //rotation,
////        )
//        aBlurGroup.textureRegionBlur = textureScene
//
//        super.draw(batch, parentAlpha) // Звичайний контент групи поверх
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
//        disposeAll(fboSceneBack, fboSceneUI, fboScene)
//    }
//
//    // Logic ------------------------------------------------------------------------
//
//    private fun createFrameBuffer() {
//        camera = OrthographicCamera(width, height)
//        camera.update()
//
//        fboSceneBack = FrameBuffer(Pixmap.Format.RGBA8888, width.toInt(), height.toInt(), false)
//        fboSceneUI   = FrameBuffer(Pixmap.Format.RGBA8888, width.toInt(), height.toInt(), false)
//        fboScene     = FrameBuffer(Pixmap.Format.RGBA8888, width.toInt(), height.toInt(), false)
//
//        textureSceneBack = TextureRegion(fboSceneBack!!.colorBufferTexture).apply { flip(false, true) }
//        textureSceneUI   = TextureRegion(fboSceneUI!!.colorBufferTexture).apply { flip(false, true) }
//        textureScene     = TextureRegion(fboScene!!.colorBufferTexture).apply { flip(false, true) }
//    }
//
//    private fun captureScreenBack(batch: Batch) {
//        screen.viewportBack.apply()
//        stagePosition.set(localToStageCoordinates(tmpVector2.set(0f, 0f)))
//
//        // 1. Захоплюємо всю сцену до рендеру групи
//        fboSceneBack!!.begin()
//        ScreenUtils.clear(Color.CLEAR)
//
//        // Отримуємо екранні координати (перетворюємо їх у пікселі)
//        val bottomLeft = Vector2(stagePosition.x, stagePosition.y)
//        val topRight   = Vector2(stagePosition.x + width, stagePosition.y + height)
//
//        // Конвертуємо координати з FitViewport у ScreenViewport
//        screen.viewportUI.project(bottomLeft)
//        screen.viewportUI.project(topRight)
//
//        // Отримуємо екранні значення
//        val screenX      = bottomLeft.x
//        val screenY      = bottomLeft.y
//        val screenWidth  = topRight.x - bottomLeft.x
//        val screenHeight = topRight.y - bottomLeft.y
//
//        camera.setToOrtho(false, screenWidth, screenHeight)
//        camera.position.set(screenX + screenWidth / 2f, screenY + screenHeight / 2f, 0f)
//        camera.update()
//
//        batch.projectionMatrix = camera.combined
//
//        batch.begin()
//
//        isVisible = false
//        screen.stageBack.root.draw(batch, 1f)
//        isVisible = true
//
//        batch.end()
//        fboSceneBack!!.end()
//        screen.stageUI.viewport.apply()
//
//        batch.color = Color.WHITE
//    }
//
//    private fun captureScreenUI(batch: Batch) {
//        screen.stageUI.viewport.apply()
//        stagePosition.set(localToStageCoordinates(tmpVector2.set(0f, 0f)))
//
//        // 1. Захоплюємо всю сцену до рендеру групи
//        fboSceneUI!!.begin()
//        ScreenUtils.clear(Color.CLEAR)
//
//        camera.setToOrtho(false, width, height)
//        camera.position.set(stagePosition.x + (width / 2f), stagePosition.y + (height / 2f), 0f)
//        camera.update()
//
//        batch.projectionMatrix = camera.combined
//
//        batch.begin()
//
//        isVisible = false
//        screen.stageUI.root.draw(batch, 1f)
//        isVisible = true
//
//        batch.end()
//        fboSceneUI!!.end()
//        screen.stageUI.viewport.apply()
//
//        batch.color = Color.WHITE
//    }
//
//    private fun captureScreenAll(batch: Batch) {
//        fboScene!!.begin()
//        ScreenUtils.clear(Color.CLEAR)
//
//        camera.setToOrtho(false, width, height)
//        camera.position.set(width / 2f, height / 2f, 0f)
//        camera.update()
//
//        batch.projectionMatrix = camera.combined
//
//        tmpTransformMatrix.set(batch.transformMatrix)
//        batch.transformMatrix = identityMatrix
//
//        batch.begin()
//
//        batch.draw(textureSceneBack, 0f, 0f, width, height)
//        batch.draw(textureSceneUI, 0f, 0f, width, height)
//
//        batch.transformMatrix = tmpTransformMatrix
//
//        batch.end()
//        fboScene!!.end()
//        screen.stageUI.viewport.apply()
//
//        batch.color = Color.WHITE
//    }
//
//}