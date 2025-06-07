package com.uxo.monax.game.screens.test

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import com.uxo.monax.game.actors.shader.AMaskGroup
import com.uxo.monax.game.screens.test.blur.ABackgroundBlurGroupTest
import com.uxo.monax.game.screens.test.blur.ABlurGroupTest
import com.uxo.monax.game.screens.test.blur.AMaskBlurBackgroundGroup
import com.uxo.monax.game.utils.Acts
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import com.uxo.monax.game.utils.advanced.AdvancedStage
import com.uxo.monax.game.utils.font.FontParameter
import com.uxo.monax.game.utils.gdxGame
import com.uxo.monax.game.utils.runGDX
import kotlinx.coroutines.launch

class TestScreen_BlurBackground2: AdvancedScreen() {

    private val parameter = FontParameter().setCharacters(FontParameter.CharType.NUMBERS.chars + "FPS:")
    private val font70    = fontGenerator_SansitaOne.generateFont(parameter.setSize(70))

    private val labelStyle70 = LabelStyle(font70, Color.valueOf("A82800"))

    private val progress = AProgressDefault(this)
    private val lblFPS   = Label("", labelStyle70)
    private val imgBtn   = Image(gdxGame.assetsAll.menu_press)
    private val imgTest  = Image(gdxGame.assetsAll.purchase_def)

    private val shaderGroup = /*ABlurGroupTest(this)*/ AMaskBlurBackgroundGroup(this, gdxGame.assetsAll.MASK)

    override fun show() {
        //setBackBackground(gdxGame.assetsLoader.BACKGROUND.region)
        //stageBack.addAndFillActor(Image(drawerUtil.getRegion(Color.DARK_GRAY)))
        stageUI.addAndFillActor(Image(drawerUtil.getRegion(Color.DARK_GRAY)))

        super.show()
    }

    override fun render(delta: Float) {
        super.render(delta)
        lblFPS.setText("FPS: ${Gdx.graphics.framesPerSecond}")
    }

    override fun AdvancedStage.addActorsOnStageBack() {
        //addShaderBack()
    }

    override fun AdvancedStage.addActorsOnStageUI() {
        addImgBtn()
        addTools()
        //addShader()

        //val child = Image(gdxGame.assetsAll.LENNA)
        //this.addActor(child)
        //child.setBounds(190f, 829f, 700f, 700f)
        //child.color.a = 0.5f

        fun inputListener() = object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                touchDragged(event, x, y, pointer)
                return true
            }

            override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                shaderGroup.setPosition(-200 + x, -300 + y)
            }
        }
        this.addListener(inputListener())

        addShaderBack()
    }

    private fun AdvancedStage.addTools() {
        addActor(lblFPS)
        lblFPS.setBounds(394f, 1650f, 291f, 87f)
        lblFPS.setAlignment(Align.center)

        addActor(progress)
        progress.setBounds(190f, 1757f, 700f, 100f)

        //progress.color.a = 0.5f
    }

    private fun AdvancedStage.addImgBtn() {
        addActor(imgBtn)
        imgBtn.setBounds(314f, 909f, 451f, 451f)
        //root.color.a = 0.55f

        addActor(imgTest)
        imgTest.setBounds(65f, 65f, 500f, 500f)
        //imgTest.color.a = 0.5f
    }

    private fun AdvancedStage.addShaderBack() {
        val maskGroup = AMaskGroup(this@TestScreen_BlurBackground2)
        addActor(maskGroup)
        maskGroup.setBounds(50f, 50f, 700f, 300f)
        //tmpG.color.a = 0.25f
        maskGroup.debug()
        //shaderGroup.setBounds(0f, 0f, 400f, 400f)
        //maskGroup.addActor(shaderGroup)

        addActor(shaderGroup)
        shaderGroup.setBounds(190f, 65f, 700f, 700f)
        shaderGroup.debug()
        //shaderGroup.color.a = 0.5f

        //tmpG.setOrigin(Align.center)
        //tmpG.rotation = 10f

        //maskGroup.animationTEST()

        val child = Image(gdxGame.assetsAll.purchase_def)
        //shaderGroup.addAndFillActor(child)

        val child2 = Image(gdxGame.assetsAll.purchase_def)
        addActor(child2)
        child2.setSize(200f, 200f)
        child2.setPosition(450f, 600f)
        //child2.color.a = 0.5f

        //shaderGroup.addActor(child)
        //child.setBounds(100f, 100f, 200f, 200f)

        child.color.a = 0.5f
        //imgBtn.color.a = 0.5f

        coroutine?.launch {
            progress.progressPercentFlow.collect { progress ->
                runGDX {
                    shaderGroup.radiusBlur = progress
                }
            }
        }
    }

    override fun hideScreen(block: Runnable) {}

    private fun Actor.animationTEST() {
        this.setOrigin(Align.center)
        this.addAction(Acts.forever(
            Actions.rotateBy(-360f, 5f)
        ))
        this.addAction(Acts.forever(
            Acts.sequence(
                Acts.scaleTo(0.5f, 0.5f, 2.5f),
                Acts.scaleTo(1f, 1f, 2.5f),
            )
        ))
    }

}