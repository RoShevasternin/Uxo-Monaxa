package com.uxo.monax.game.manager.util

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.uxo.monax.game.manager.SpriteManager

class SpriteUtil {

     class Loader {
          private fun getRegion(name: String): TextureRegion = SpriteManager.EnumAtlas.LOADER.data.atlas.findRegion(name)

          val circle_in  = getRegion("circle_in")
          val circle_out = getRegion("circle_out")

          val BACKGROUND = SpriteManager.EnumTexture.LOADER_BACKGROUND.data.texture

     }

     class All {
          private fun getRegionAll(name: String): TextureRegion = SpriteManager.EnumAtlas.ALL.data.atlas.findRegion(name)
          private fun getRegionItems(name: String): TextureRegion = SpriteManager.EnumAtlas.ITEMS.data.atlas.findRegion(name)

          // atlas All ------------------------------------------------------------------------------

          val arrow_purchase     = getRegionAll("arrow_purchase")
          val arrow_subscription = getRegionAll("arrow_subscription")
          val button_click       = getRegionAll("button_click")
          val cost_purchase      = getRegionAll("cost_purchase")
          val cost_subscription  = getRegionAll("cost_subscription")
          val item_check         = getRegionAll("item_check")
          val item_close         = getRegionAll("item_close")
          val item_def           = getRegionAll("item_def")
          val leaderboard_def    = getRegionAll("leaderboard_def")
          val leaderboard_press  = getRegionAll("leaderboard_press")
          val menu_def           = getRegionAll("menu_def")
          val menu_press         = getRegionAll("menu_press")
          val panel_ads_turn_off = getRegionAll("panel_ads_turn_off")
          val panel_click        = getRegionAll("panel_click")
          val panel_music_sound  = getRegionAll("panel_music_sound")
          val progress           = getRegionAll("progress")
          val purchase_def       = getRegionAll("purchase_def")
          val purchase_press     = getRegionAll("purchase_press")
          val separator          = getRegionAll("separator")
          val subscription_def   = getRegionAll("subscription_def")
          val subscription_press = getRegionAll("subscription_press")
          val ads_btn_blur       = getRegionAll("ads_btn_blur")
          val hand               = getRegionAll("hand")
          val click              = getRegionAll("click")
          val ads_check          = getRegionAll("ads_check")

          // atlas Items ------------------------------------------------------------------------------

          val listItems = List(9) { getRegionItems("${it.inc()}") }

          // textures ------------------------------------------------------------------------------

          val PROGRESS_MASK     = SpriteManager.EnumTexture.ALL_PROGRESS_MASK.data.texture
          val BACKGROUND_BLURED = SpriteManager.EnumTexture.ALL_BACKGROUND_BLURED.data.texture

          val MASK = SpriteManager.EnumTexture.MASK.data.texture
          val LENNA = SpriteManager.EnumTexture.LENNA.data.texture
     }

}