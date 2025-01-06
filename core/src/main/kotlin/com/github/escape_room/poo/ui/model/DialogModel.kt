package com.github.escape_room.poo.ui.model
import ktx.log.logger
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.escape_room.poo.dialog.Dialog
import com.github.escape_room.poo.event.EntityDialogEvent


class DialogModel(stage: Stage) : PropertyChangeSource(), EventListener {

    private lateinit var dialog: Dialog
    var text by propertyNotify("")
    var options by propertyNotify(listOf<DialogOptionModel>())
    var completed by propertyNotify(false)

    init {
        stage.addListener(this)
    }

    fun triggerOption(optionIdx:Int){
        dialog.triggerOption(optionIdx)
        updateTextAndOptions()
        Gdx.app.log("DialogModel", "Trigger received")
    }

    private fun updateTextAndOptions() {
        completed=dialog.isComplete()
        if(!completed){
            text=dialog.currentNode.text
            options = dialog.currentNode.options.map { DialogOptionModel(it.id,it.text) }
            Gdx.app.log("DialogModel", "Updated text: $text, options: $options")
        }
    }

    override fun handle(event: Event): Boolean {

        when(event){
            is EntityDialogEvent ->{
                this.dialog=event.dialog
                updateTextAndOptions()
                Gdx.app.log("DialogModel", "EntityDialogEvent received")
            }

            else -> return false
        }
        return true

    }

}

