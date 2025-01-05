package com.github.escape_room.poo.ai
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.github.escape_room.poo.component.AnimationComponent
import com.github.escape_room.poo.component.AnimationType
import com.github.escape_room.poo.component.AttackComponent
import com.github.escape_room.poo.component.MoveComponent
import com.github.escape_room.poo.component.StateComponent
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.World

data class AiEntity(
    private val entity: Entity,
    private val world: World,
    private val animationCmps: ComponentMapper<AnimationComponent> = world.mapper(),
    private val moveCmps: ComponentMapper<MoveComponent> = world.mapper(),
    private val attackCmps: ComponentMapper<AttackComponent> = world.mapper(),
    private val stateCmps: ComponentMapper<StateComponent> = world.mapper(),
) {

    val wantsToRun: Boolean
        get() {
            val moveCmp = moveCmps[entity]
            return moveCmp.cos != 0f || moveCmp.sin != 0f
        }

    val wantsToAttack: Boolean
        get() =attackCmps.getOrNull(entity)?.doAttack ?: false

    val attackCmp: AttackComponent
        get()=attackCmps[entity]

    fun animation(type: AnimationType,mode: PlayMode=PlayMode.LOOP,resetAnimation: Boolean = false){
        with(animationCmps[entity]) {
            nextAnimation(type)
            playMode=mode
            if(resetAnimation){
                stateTime=0f
            }
        }
    }
    fun state(next: EntityState){
        with(stateCmps[entity]){nextState=next}
    }

    fun changeToPrev(){
        with(stateCmps[entity]){nextState=stateMachine.previousState}
    }

    fun root(enable:Boolean){
        with(moveCmps[entity]){ root=enable }
    }
    fun startAttack(){
        with(attackCmps[entity]){startAttack()}
    }
}
