    package com.uxo.monax.game.actors.shader

    import com.badlogic.gdx.Gdx
    import com.badlogic.gdx.graphics.*
    import com.badlogic.gdx.graphics.g2d.Batch
    import com.badlogic.gdx.graphics.glutils.ShaderProgram
    import com.uxo.monax.game.utils.advanced.AdvancedScreen
    import com.uxo.monax.game.utils.advanced.preRenderGroup.FboPreRender
    import com.uxo.monax.game.utils.advanced.preRenderGroup.PreRenderableGroup
    import com.uxo.monax.game.utils.disposeAll

    class AMaskGroup(
        override val screen: AdvancedScreen,
        private val maskTexture: Texture? = null
    ): PreRenderableGroup() {

        companion object {
            private var vertexShader   = Gdx.files.internal("shader/defaultVS.glsl").readString()
            private var fragmentShader = Gdx.files.internal("shader/maskFS.glsl").readString()
        }

        private var shaderProgram: ShaderProgram? = null

        override fun addActorsOnGroup() {
            createShaders()
            createFrameBuffer()
        }

        override fun getFboPreRender() = object : FboPreRender {
            override fun renderFboGroup(batch: Batch, combinedAlpha: Float) {
                drawChildrenSimple(batch, combinedAlpha)
            }

            override fun applyEffect(batch: Batch, combinedAlpha: Float) {}

            override fun renderFboResult(batch: Batch, combinedAlpha: Float) {
                if (maskTexture != null) {
                    batch.shader = shaderProgram

                    Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1)
                    maskTexture.bind(1)
                    Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0)
                    textureGroup!!.texture.bind(0)

                    shaderProgram!!.setUniformi("u_mask", 1)
                    shaderProgram!!.setUniformi("u_texture", 0)
                }

                batch.draw(textureGroup, 0f, 0f, width, height)
            }
        }

        override fun preRender(batch: Batch, parentAlpha: Float) {
            if (shaderProgram == null) return

            super.preRender(batch, parentAlpha)
        }

        override fun dispose() {
            super.dispose()
            disposeAll(shaderProgram)
        }

        // Logic ------------------------------------------------------------------------

        private fun createShaders() {
            ShaderProgram.pedantic = false
            shaderProgram = ShaderProgram(vertexShader, fragmentShader)

            if (shaderProgram?.isCompiled == false) {
                throw IllegalStateException("shader compilation failed:\n" + shaderProgram?.log)
            }
        }

        //globalPosition.set(localToStageCoordinates(tmpVector2.set(0f, 0f)))
        //camera.position.set(globalPosition.x + width / 2f, globalPosition.y + height / 2f, 0f)
        //camera.update()

        //SpriteBatch().setBlendFunction()
        //batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA)
        //batch.setBlendFunction(GL20.GL_ONE_MINUS_DST_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        //batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_SRC_ALPHA, GL20.GL_ONE)

    }