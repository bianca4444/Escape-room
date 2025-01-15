package com.github.escape_room.poo.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.escape_room.poo.component.DialogComponent
import com.github.escape_room.poo.component.DialogId
import com.github.escape_room.poo.component.DisarmComponent
import com.github.escape_room.poo.component.MoveComponent
import com.github.escape_room.poo.dialog.Dialog
import com.github.escape_room.poo.dialog.dialog
import com.github.escape_room.poo.event.EntityDialogEvent
import com.github.escape_room.poo.event.fire
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.Qualifier
import ktx.app.gdxError
import java.sql.Blob

// Variabila globală care contorizează câte chei ai
var keyCount = 0

var slimeCount =0

// Funcție care incrementează contorul de chei
fun incrementKeyCount() {
    keyCount++
    println("Ai mai luat o cheie!")
}

@AllOf([DialogComponent::class])
class DialogSystem(
    private val dialogCmps: ComponentMapper<DialogComponent>,
    private val moveCmps: ComponentMapper<MoveComponent>,
    private val disarmCmps: ComponentMapper<DisarmComponent>,
    @Qualifier("GameStage") private val stage: Stage
) : IteratingSystem() {
    private val dialogCache = mutableMapOf<DialogId, Dialog>()

    override fun onTickEntity(entity: Entity) {
        with(dialogCmps[entity]) {
            val triggerEntity = interactEntity
            var dialog = currentDialog

            if(hasInteracted)
                return

            if (triggerEntity == null) {
                return
            } else if (dialog != null) {
                if (dialog.isComplete()) {
                    moveCmps.getOrNull(triggerEntity)?.let { it.root = false }
                    configureEntity(triggerEntity) { disarmCmps.remove(it) }
                    currentDialog = null
                    interactEntity = null
                }
                return
            }

            dialog = getDialog(dialogId).also { it.start() }
            currentDialog = dialog
            moveCmps.getOrNull(triggerEntity)?.let { it.root = true }
            configureEntity(triggerEntity) { disarmCmps.add(it) }

            stage.fire(EntityDialogEvent(dialog))

            if(dialogId == DialogId.BLOB7) hasInteracted = false
            else hasInteracted = true
        }
    }

    private fun getDialog(id: DialogId): Dialog {
        return dialogCache.getOrPut(id) {
            when (id) {
                DialogId.BLOB -> dialog(id.name) {

                    node(0, "Hello adventurer! Can you please take care of my crazy blue brothers?") {
                        option("But why?") {
                            action = { this@dialog.goToNode(1) }
                            slimeCount++
                        }
                    }
                    node(1, "A dark magic has possessed them. There is no cure - KILL EM ALL!!!") {
                        option("Again?") {
                            action = { this@dialog.goToNode(0) }
                        }

                        option("Ok, ok") {
                            action = { this@dialog.goToNode(2) }
                        }
                    }
                    node(2, "Are their eyes glowing red because of the magic?") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(3)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(6) } }
                    }
                    node(3, "Do they attack anyone who gets close?") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(4)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(6) } }
                    }
                    node(4, "Can they be defeated with regular weapons?") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(5)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(6) } }
                    }
                    node(5, "Congratulations! You have defeated the blue brothers and saved the village!") {

                        option("Finish") { action = { incrementKeyCount()

                            this@dialog.end() } }
                    }
                    node(6, "Your answer was not good! Nu o să-ți dau cheia.") {
                        option("Finish") { action = { this@dialog.end() } }
                    }
                }

                DialogId.BLOB2 -> dialog(id.name) {

                    node(0, "Hello adventurer! The green goblins are causing chaos. Can you help us?") {
                        option("What happened?") {
                            action = { this@dialog.goToNode(1) }
                            slimeCount++
                        }
                    }
                    node(1, "They've been enchanted by a powerful druid. Only a skilled warrior can stop them.") {
                        option("How can I stop them?") {
                            action = { this@dialog.goToNode(2) }
                        }
                    }
                    node(2, "Are they stronger than normal goblins?") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(3)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(6) } }
                    }
                    node(3, "Will they attack anyone who enters their territory?") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(4)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(6) } }
                    }
                    node(4, "Can they be defeated with magic or weapons?") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(5)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(6) } }
                    }
                    node(5, "Congratulations! You've defeated the goblins and saved the village!") {


                        option("Finish") { action = { incrementKeyCount()
                            this@dialog.end() } }
                    }
                    node(6, "Wrong answers! You have failed.") {
                        option("Finish") { action = { this@dialog.end() } }
                    }
                }

                DialogId.BLOB3 -> dialog(id.name) {

                    node(0, "Greetings, brave adventurer! The fiery beasts in the volcano need your help.") {
                        option("Why are they so aggressive?") {
                            action = { this@dialog.goToNode(1) }
                            slimeCount++
                        }
                    }
                    node(1, "The volcanic spirit has been disturbed. Only someone with courage can calm the beasts.") {
                        option("What should I do?") {
                            action = { this@dialog.goToNode(2) }
                        }
                    }
                    node(2, "Are the beasts stronger than usual?") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(3)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(6) } }
                    }
                    node(3, "Do they attack anyone who comes near the volcano?") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(4)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(6) } }
                    }
                    node(4, "Can the spirit of the volcano be calmed with an offering?") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(5)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(6) } }
                    }
                    node(5, "Well done! You have calmed the beasts and saved the village from disaster!") {

                        option("Finish") { action = { incrementKeyCount()
                            this@dialog.end() } }
                    }
                    node(6, "Incorrect answers! The beasts remain uncontrolled.") {
                        option("Finish") { action = { this@dialog.end() } }
                    }
                }

                DialogId.BLOB4 -> dialog(id.name) {

                    node(0, "Hello, adventurer! The forest spirits are angry and attacking everyone.") {
                        option("What triggered their anger?") {
                            action = { this@dialog.goToNode(1) }
                            slimeCount++
                        }
                    }
                    node(1, "An ancient artifact has been stolen from their sacred grove.") {
                        option("How can I help them?") {
                            action = { this@dialog.goToNode(2) }
                        }
                    }
                    node(2, "Are the spirits attacking anyone who enters the forest?") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(3)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(6) } }
                    }
                    node(3, "Do they only attack those who carry the stolen artifact?") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(4)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(6) } }
                    }
                    node(4, "Can the artifact be returned to calm the spirits?") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(5)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(6) } }
                    }
                    node(5, "Great job! You've returned the artifact and calmed the spirits!") {

                        option("Finish") { action = { incrementKeyCount()
                            this@dialog.end() } }
                    }
                    node(6, "Incorrect! The spirits remain angry.") {
                        option("Finish") { action = { this@dialog.end() } }
                    }
                }

                DialogId.BLOB5 -> dialog(id.name) {

                    node(0, "Hello, adventurer! The shadow creatures are causing havoc in the town.") {
                        option("Why are they attacking?") {
                            action = { this@dialog.goToNode(1) }
                            slimeCount++
                        }
                    }
                    node(1, "They have been summoned by a dark sorcerer. We need your help!") {
                        option("How can I stop them?") {
                            action = { this@dialog.goToNode(2) }
                        }
                    }
                    node(2, "Are they immune to magic?") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(3)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(6) } }
                    }
                    node(3, "Do they attack only at night?") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(4)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(6) } }
                    }
                    node(4, "Can they be stopped by breaking their connection to the sorcerer?") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(5)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(6) } }
                    }
                    node(5, "Well done! You have stopped the creatures and saved the town!") {

                        option("Finish") { action = { incrementKeyCount()
                            this@dialog.end() } }
                    }
                    node(6, "Wrong answers! The creatures continue their attack.") {
                        option("Finish") { action = { this@dialog.end() } }
                    }
                }
                DialogId.BLOB6 -> dialog(id.name) {

                    node(0, "Hello!"){
                        option("Hi"){
                            action = { this@dialog.end() }
                            slimeCount++
                        }
                    }
                }

                DialogId.BLOB7 -> dialog(id.name) {

                    if(keyCount < 2 && slimeCount < 6)
                    {
                        node(0, "Go back and search some more") {
                            option("Ok"){
                                action = {this@dialog.end()}
                            }
                        }
                    } else if (keyCount >= 3){
                        node(0, "You win!") {
                            option("Great!"){
                                action = {this@dialog.end()}
                            }
                        }
                    }else{
                        node(0, "Game lost!") {
                            option("Try again"){
                                action = {this@dialog.end()}
                            }
                        }
                    }

                }

                else -> gdxError("No dialog configured for $id.")
            }
        }
    }
}
