package com.github.escape_room.poo.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.escape_room.poo.component.DialogComponent
import com.github.escape_room.poo.component.DialogId
import com.github.escape_room.poo.component.MoveComponent
import com.github.escape_room.poo.dialog.Dialog
import com.github.escape_room.poo.dialog.dialog
import com.github.escape_room.poo.event.EntityDialogEvent
import com.github.escape_room.poo.event.fire
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.app.gdxError


@AllOf([DialogComponent::class])
class DialogSystem(
    private val dialogCmps: ComponentMapper<DialogComponent>,
    private val moveCmps: ComponentMapper<MoveComponent>,
    private val stage: Stage,
) : IteratingSystem(){

    private val dialogCache=mutableMapOf<DialogId, Dialog>()

    override fun onTickEntity(entity: Entity) {
        with(dialogCmps[entity]){
            val triggerEntity: Entity? = interactEntity
            if(triggerEntity == null) {return}

            var dialog: Dialog?=currentDialog
            if(dialog!=null){
                if(dialog.isComplete()){
                    moveCmps.getOrNull(triggerEntity)?.let { it.root=false }
                    currentDialog=null
                    interactEntity=null
                }
                return
            }

            dialog=getDialog(dialogId).also{it.start()}
            currentDialog=dialog
            moveCmps.getOrNull(triggerEntity)?.let { it.root=true }
            stage.fire(EntityDialogEvent(dialog))
        }
    }
    private fun getDialog(dialogId:DialogId): Dialog {
        return dialogCache.getOrPut(dialogId) {
            when(dialogId){
                DialogId.Q -> dialog(dialogId.name){
                    node(0,"Hello") {
                        option("Hi"){
                            action={this@dialog.goToNode(1)}
                        }
                    }
                    node(1,"Cf"){
                        option("Bn"){
                            action={this@dialog.goToNode(0)}
                        }
                        option("bine tuuu"){
                            action={this@dialog.end()}
                        }
                    }
                }
                else -> gdxError("No dialog for $dialogId")
            }
        }
    }
}
