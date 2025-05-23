package com.uxo.monax.game.manager

import com.badlogic.gdx.Gdx
import com.uxo.monax.game.screens.GameScreen
import com.uxo.monax.game.screens.LoaderScreen
import com.uxo.monax.game.screens.MenuScreen
import com.uxo.monax.game.screens.test.TestScreen_BlurBackground
import com.uxo.monax.game.utils.advanced.AdvancedScreen
import com.uxo.monax.game.utils.gdxGame
import com.uxo.monax.game.utils.runGDX

class NavigationManager {

    private val backStack = mutableListOf<String>()

    fun navigate(toScreenName: String, fromScreenName: String? = null) = runGDX {
        gdxGame.updateScreen(getScreenByName(toScreenName))
        backStack.filter { name -> name == toScreenName }.onEach { name -> backStack.remove(name) }
        fromScreenName?.let { fromName ->
            backStack.filter { name -> name == fromName }.onEach { name -> backStack.remove(name) }
            backStack.add(fromName)
        }
    }

    fun back() = runGDX {
        if (isBackStackEmpty()) exit() else gdxGame.updateScreen(getScreenByName(backStack.removeAt(backStack.lastIndex)))
    }

    fun exit() = runGDX { Gdx.app.exit() }


    fun isBackStackEmpty() = backStack.isEmpty()

    private fun getScreenByName(name: String): AdvancedScreen = when(name) {
        LoaderScreen::class.java.name -> LoaderScreen()
        GameScreen  ::class.java.name -> GameScreen()
        MenuScreen  ::class.java.name -> MenuScreen()

        TestScreen_BlurBackground::class.java.name -> TestScreen_BlurBackground()
//        TestScreen_Blur::class.java.name -> TestScreen_Blur()

        else -> GameScreen()
    }

}