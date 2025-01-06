package com.github.escape_room.poo.ui.view

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.escape_room.poo.ui.Drawables
import com.github.escape_room.poo.ui.Labels
import com.github.escape_room.poo.ui.get
import ktx.actors.alpha
import ktx.scene2d.KTable
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor
import ktx.scene2d.label
import ktx.scene2d.table


class GameView(
    skin: Skin,
) : Table(skin), KTable{

    init {
        setFillParent(true)


        table{
            background = skin[Drawables.FRAME_BGD]
            label(text="Test",style=Labels.FRAME.skinKey){lblCell->
                this.setAlignment(Align.topLeft)
                this.wrap = true
                lblCell.expand().fill().pad(14f)
            }
            this.alpha=0f
            it.expand().width(130f).height(90f).top().row()
        }

    }
}

@Scene2dDsl
fun <S> KWidget<S>.gameView(
    skin: Skin= Scene2DSkin.defaultSkin,
    init: GameView.(S) -> Unit ={}
) : GameView=actor(GameView(skin), init)
