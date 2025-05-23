//package com.uxo.monax.game.screens.test
//
//import com.badlogic.gdx.Gdx
//import com.badlogic.gdx.graphics.Color
//import com.badlogic.gdx.scenes.scene2d.Actor
//import com.badlogic.gdx.scenes.scene2d.actions.Actions
//import com.badlogic.gdx.scenes.scene2d.ui.Image
//import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
//import com.badlogic.gdx.utils.Align
//import com.uxo.monax.game.actors.ATmpGroup
//import com.uxo.monax.game.actors.label.ALabel
//import com.uxo.monax.game.actors.shader.ABlurGroup
//import com.uxo.monax.game.utils.Acts
//import com.uxo.monax.game.utils.actor.disable
//import com.uxo.monax.game.utils.advanced.AdvancedScreen
//import com.uxo.monax.game.utils.advanced.AdvancedStage
//import com.uxo.monax.game.utils.font.FontParameter
//import com.uxo.monax.game.utils.gdxGame
//import com.uxo.monax.game.utils.runGDX
//import kotlinx.coroutines.launch
//
//class TestScreen_Blur: AdvancedScreen() {
//
//    private val parameter = FontParameter().setCharacters(FontParameter.CharType.ALL)
//    private val font70    = fontGenerator_SansitaOne.generateFont(parameter.setSize(70))
//    private val font50    = fontGenerator_SansitaOne.generateFont(parameter.setSize(50))
//
//    private val labelStyle70 = LabelStyle(font70, Color.valueOf("A82800"))
//    private val labelStyle50 = LabelStyle(font50, Color.valueOf("05B9C6"))
//
//    private val progress = AProgressDefault(this)
//    private val lblFPS   = ALabel(this, "", labelStyle70)
//    private val imgBtn   = Image(gdxGame.assetsAll.menu_press)
//    private val imgTest  = Image(gdxGame.assetsAll.purchase_def)
//
//    private val shaderGroup = ABlurGroup(this)
//
//    private val lblOriginal = ALabel(this, "Original", labelStyle50)
//    private val lblBlur     = ALabel(this, "Blur", labelStyle50)
//
//    override fun show() {
//        //setBackBackground(gdxGame.assetsLoader.BACKGROUND.region)
//        stageBack.addAndFillActor(Image(drawerUtil.getRegion(Color.DARK_GRAY)))
//
//        super.show()
//    }
//
//    override fun render(delta: Float) {
//        super.render(delta)
//        lblFPS.label.setText("FPS: ${Gdx.graphics.framesPerSecond}")
//    }
//
//    override fun AdvancedStage.addActorsOnStageBack() {
//        //addShader()
//        //addShaderBack()
//    }
//
//    override fun AdvancedStage.addActorsOnStageUI() {
//        addTools()
//        //addImgBtn()
//        addShader()
//
//        val child = Image(gdxGame.assetsAll.LENNA)
//        addActor(child)
//        child.setBounds(40f, 992f, 700f, 700f)
//        child.setOrigin(Align.center)
//        child.rotation = -90f
//    }
//
//    private fun AdvancedStage.addTools() {
//        addActor(lblFPS)
//        lblFPS.setBounds(707f, 916f, 291f, 87f)
//        lblFPS.label.setAlignment(Align.center)
//        lblFPS.setOrigin(Align.center)
//        lblFPS.rotation = -90f
//
//        addActor(progress)
//        progress.setBounds(643f, 910f, 700f, 100f)
//        progress.setOrigin(Align.center)
//        progress.rotation = -90f
//
//        addActors(lblOriginal, lblBlur)
//        lblOriginal.setBounds(628f, 1648f, 311f, 87f)
//        lblBlur.setBounds(701f, 184f, 165f, 87f)
//        lblOriginal.label.setAlignment(Align.center)
//        lblBlur.label    .setAlignment(Align.center)
//        lblOriginal.setOrigin(Align.center)
//        lblBlur    .setOrigin(Align.center)
//        lblOriginal.rotation = -90f
//        lblBlur.rotation     = -90f
//
//    }
//
//    private fun AdvancedStage.addImgBtnForBackground() {
//        addActor(imgBtn)
//        imgBtn.setBounds(314f, 909f, 451f, 451f)
//        //root.color.a = 0.55f
//
//        addActor(imgTest)
//        imgTest.setBounds(65f, 65f, 500f, 500f)
//        //imgTest.color.a = 0.5f
//    }
//
//    private fun AdvancedStage.addShader() {
//        val tmpG = ATmpGroup(this@TestScreen_Blur)
//        addActor(tmpG)
//        tmpG.setBounds(40f, 228f, 700f, 700f)
//        //tmpG.debug()
//        tmpG.addActor(shaderGroup)
//        shaderGroup.setBounds(0f, 0f, 700f, 700f)
//        tmpG.setOrigin(Align.center)
//        tmpG.rotation = -90f
//
////        fun inputListener() = object : InputListener() {
////            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
////                touchDragged(event, x, y, pointer)
////                return true
////            }
////
////            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
////                shaderGroup.setPosition(x, y)
////            }
////        }
//        //tmpG.addListener(inputListener())
//
//        //addActor(shaderGroup)
//        //shaderGroup.setBounds(244f, 1032f, 591f, 591f)
////        shaderGroup.debug()
//        shaderGroup.disable()
//
//        val child = Image(gdxGame.assetsAll.LENNA)
//        //shaderGroup.addAndFillActor(child)
//        shaderGroup.addActor(child)
//        child.setSize(shaderGroup.width, shaderGroup.height)
//
//        //child.color.a = 0.5f
//        //shaderGroup.color.a = 0.5f
//
//        coroutine?.launch {
//            progress.progressPercentFlow.collect { progress ->
//                runGDX {
//                    shaderGroup.radiusBlur = progress
//                }
//            }
//        }
//    }
//
//    private fun AdvancedStage.addShaderBack() {
//        addActor(lblFPS)
//        lblFPS.setBounds(394f, 850f, 291f, 87f)
//        lblFPS.label.setAlignment(Align.center)
//
//        addActor(progress)
//        progress.setBounds(20f, 1057f, 700f, 100f)
//
//        addActor(shaderGroup)
//        shaderGroup.setBounds(0f, 150f, 591f, 1591f)
//        shaderGroup.debug()
//        shaderGroup.disable()
//
//        val child = Image(gdxGame.assetsAll.purchase_def)
//        //shaderGroup.addAndFillActor(child)
//        shaderGroup.addActor(child)
//        child.setBounds(200f, 195f, 190f, 190f)
//        //child.color.a = 0.5f
//        //imgBtn.color.a = 0.5f
//
//        shaderGroup.setOrigin(Align.center)
////        shaderGroup.addAction(Acts.forever(
////            Actions.rotateBy(-360f, 5f)
////        ))
////        shaderGroup.addAction(Acts.forever(
////            Acts.sequence(
////                Acts.moveBy(10f, -100f, 1f),
////                Acts.moveBy(-10f, 100f, 1f),
////            )
////        ))
//        imgBtn.addAction(Acts.forever(Actions.rotateBy(360f, 5f)))
//
//        //SpriteBatch()
//
//        coroutine?.launch {
//            progress.progressPercentFlow.collect { progress ->
//                runGDX {
//                    shaderGroup.radiusBlur = progress
//                }
//            }
//        }
//    }
//
//    override fun hideScreen(block: Runnable) {}
//
//    private fun Actor.animationTEST() {
//        this.setOrigin(Align.center)
//        this.addAction(Acts.forever(
//            Actions.rotateBy(-360f, 5f)
//        ))
//        this.addAction(Acts.forever(
//            Acts.sequence(
//                Acts.scaleTo(0.5f, 0.5f, 2.5f),
//                Acts.scaleTo(1f, 1f, 2.5f),
//            )
//        ))
//    }
//
//
//}