package com.github.escape_room.poo.component

import com.badlogic.gdx.ai.fsm.DefaultStateMachine
import com.badlogic.gdx.ai.fsm.State
import com.github.escape_room.poo.ai.AiEntity
import com.github.escape_room.poo.ai.DefaultState
import com.github.escape_room.poo.ai.EntityState
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World

data class StateComponent(
    var nextState: EntityState = DefaultState.IDLE,
    val stateMachine: DefaultStateMachine<AiEntity, EntityState> = DefaultStateMachine()
){
    companion object {
        class StateComponentListener(
            private val world:World,
        ) : ComponentListener<StateComponent>{
            override fun onComponentAdded(entity: Entity, component: StateComponent) {
                component.stateMachine.owner= AiEntity(entity,world)
            }
            override fun onComponentRemoved(entity: Entity, component: StateComponent) = Unit


        }
    }
}
