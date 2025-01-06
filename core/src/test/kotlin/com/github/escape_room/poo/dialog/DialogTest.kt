package com.github.escape_room.poo.dialog

import com.badlogic.gdx.scenes.scene2d.actions.Actions.action
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DialogTest {

    @Test
    fun testDialogDsl(){
        lateinit var firstNode: Node
        lateinit var secondNode: Node

        val testDialog:Dialog=dialog("testDialog"){
            firstNode=node(0, "Node 0"){
                option("next"){
                    action={this@dialog.goToNode(1)}
                }
            }
            secondNode=node(1, "Node 1"){
                option("previous"){
                    action={this@dialog.goToNode(0)}
                }
                option("end"){
                    action={this@dialog.end()}
                }
            }
        }
        testDialog.start()
        assertEquals(firstNode,testDialog.currentNode)

        testDialog.triggerOption(0)
        assertEquals(secondNode,testDialog.currentNode)

        testDialog.triggerOption(1)
        assertTrue(testDialog.isComplete())

    }
}
