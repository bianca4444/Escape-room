package com.github.escape_room.poo.ui.view

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.github.escape_room.poo.dialog.Dialog
import com.github.escape_room.poo.dialog.dialog
import com.github.escape_room.poo.event.EntityDialogEvent
import com.github.escape_room.poo.event.fire
import com.github.escape_room.poo.ui.Drawables
import com.github.escape_room.poo.ui.Labels
import com.github.escape_room.poo.ui.get
import ktx.actors.txt
import ktx.scene2d.*

class TimerView(
    private val initialTime: Float, // Timpul de start, în secunde
    skin: Skin,
    private val onTimeUp: () -> Unit
) : Table(skin), KTable {

    private val timerLabel: Label
    private var remainingTime: Float = initialTime

    init {
        setFillParent(true)

        this.timerLabel = label(text = formatTime(remainingTime), style = Labels.FRAME.skinKey) { lblCell ->
            this.setAlignment(Align.center)
            lblCell.expand().center()
        }


    }

    fun update(delta: Float) {
        remainingTime -= delta
        if (remainingTime < 0) {
            remainingTime = 0f
            onTimeUp()
        }
        timerLabel.txt = formatTime(remainingTime)
    }

    private fun formatTime(time: Float): String {
        val minutes = (time / 60).toInt()
        val seconds = (time % 60).toInt()
        return "%02d:%02d".format(minutes, seconds)
    }




    fun gameOver(stage: Stage){
        val testDialog: Dialog = dialog("testDialog") {
            node(0, "Timpul a expirat, Game Over") {
                option("") {
                    action = { this@dialog.end() }
                }
            }
        }
        testDialog.start()
        stage.fire(EntityDialogEvent(testDialog))
    }

}

@Scene2dDsl
fun <S> KWidget<S>.timerView(
    initialTime: Float,
    skin: Skin = Scene2DSkin.defaultSkin,
    onTimeUp: () -> Unit,
    init: TimerView.(S) -> Unit = {}
): TimerView = actor(TimerView(initialTime, skin, onTimeUp), init)
