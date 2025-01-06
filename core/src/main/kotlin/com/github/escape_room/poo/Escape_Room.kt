package com.github.escape_room.poo

import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.github.escape_room.poo.screen.GameScreen
import com.github.escape_room.poo.screen.UiScreen
import com.github.escape_room.poo.ui.disposeSkin
import com.github.escape_room.poo.ui.loadSkin
import ktx.app.KtxGame
import ktx.app.KtxScreen

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms. */

class Escape_Room : KtxGame<KtxScreen>() {

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        loadSkin()
        addScreen(GameScreen())
        addScreen(UiScreen())
        setScreen<GameScreen>()
    }

    override fun dispose() {
        super.dispose()
        disposeSkin()
    }

    companion object{
        const val UNIT_SCALE=1/16f
    }
}
