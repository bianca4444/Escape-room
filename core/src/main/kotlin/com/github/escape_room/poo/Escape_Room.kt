package com.github.escape_room.poo

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.escape_room.poo.screen.GameScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.assets.disposeSafely

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms. */

class Escape_Room : KtxGame<KtxScreen>(), EventListener {
    private val batch: Batch by lazy { SpriteBatch() }
    val gameStage by lazy { Stage(ExtendViewport(16f, 9f), batch) }
    val uiStage by lazy { Stage(ExtendViewport(320f, 180f), batch) }
    private var paused = false

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG

        gameStage.addListener(this)

        addScreen(GameScreen(this))
        setScreen<GameScreen>()
    }

    override fun resize(width: Int, height: Int) {
        gameStage.viewport.update(width, height, true)
        uiStage.viewport.update(width, height, true)
        super.resize(width, height)
    }

    override fun render() {
        clearScreen(0f, 0f, 0f, 1f)
        // dt is set to zero during pause to
        // stop animations
        val dt = if (paused) 0f else Gdx.graphics.deltaTime
        currentScreen.render(dt)
    }

    override fun dispose() {
        super.dispose()
        gameStage.disposeSafely()
        uiStage.disposeSafely()
        batch.disposeSafely()
    }

    override fun handle(event: Event): Boolean {
        when (event) {


            else -> return false
        }

        return true
    }

    companion object {
        // 16px = 1m in our physic world
        const val UNIT_SCALE = 1 / 16f
    }
}
