package com.github.escape_room.poo.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.escape_room.poo.ui.disposeSkin
import com.github.escape_room.poo.ui.loadSkin
import com.github.escape_room.poo.ui.view.GameView
import com.github.escape_room.poo.ui.view.gameView
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.actors
import ktx.scene2d.table

class UiScreen : KtxScreen {
    private val stage: Stage = Stage(ExtendViewport(320f, 180f))



    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun show() {
        stage.clear()
        stage.actors {
            gameView()
        }
    }


    override fun render(delta: Float) {
        if(Gdx.input.isKeyPressed(Input.Keys.R)) {
            hide()
            show()
        }
        stage.act()
        stage.draw()
    }

    override fun dispose() {
        stage.disposeSafely()
    }

}
