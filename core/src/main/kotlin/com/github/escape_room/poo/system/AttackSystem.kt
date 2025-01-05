package com.github.escape_room.poo.system

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.World
import com.github.escape_room.poo.component.AnimationComponent
import com.github.escape_room.poo.component.AttackComponent
import com.github.escape_room.poo.component.AttackState
import com.github.escape_room.poo.component.LootComponent
import com.github.escape_room.poo.component.PhsysicComponent
import com.github.escape_room.poo.component.PlayerComponent
import com.github.escape_room.poo.component.imageComponent
import com.github.escape_room.poo.system.EntitySpawnSystem.Companion.HIT_BOX_SENSOR
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.box2d.query
import ktx.math.component1
import ktx.math.component2

@AllOf([AttackComponent::class, PhsysicComponent::class, imageComponent::class])
class AttackSystem(
    private val attackCmps: ComponentMapper<AttackComponent>,
    private val animationCmps: ComponentMapper<AnimationComponent>,
    private val imgCmps: ComponentMapper<imageComponent>,
    private val physicCmps: ComponentMapper<PhsysicComponent>,
    private val playerCmps: ComponentMapper<PlayerComponent>,
    private val lootCmps: ComponentMapper<LootComponent>,
    private val phWorld: World,
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val attackCmp=attackCmps[entity]

        if(attackCmp.isReady && !attackCmp.doAttack){
            return
        }

        if(attackCmp.isPrepared && attackCmp.doAttack){
            attackCmp.doAttack=false
            attackCmp.state= AttackState.ATTACKING
            attackCmp.delay=attackCmp.maxDelay
            return
        }

        attackCmp.delay-=deltaTime
        if(attackCmp.delay<=0f && attackCmp.isAttacking){

            val image=imgCmps[entity].image
            val physicCmp=physicCmps[entity]
            val attackLeft=image.flipX
            val (x,y) = physicCmp.body.position
            val (offX,offY) =physicCmp.offset
            val (w,h)=physicCmp.size
            val halfW=w/2
            val halfH=h/2

            if(attackLeft){
                AABB_RECT.set(
                    x+offX-halfW-attackCmp.extraRange,
                    y+offY-halfH,
                    x+offX+halfW,
                    y+offY+halfH,
                )
            } else{
                AABB_RECT.set(
                    x+offX-halfW,
                    y+offY-halfH,
                    x+offX+halfW+attackCmp.extraRange,
                    y+offY+halfH,
                )
            }


            phWorld.query(AABB_RECT.x, AABB_RECT.y, AABB_RECT.width, AABB_RECT.height){ fixture->
                if(fixture.userData != HIT_BOX_SENSOR){
                    return@query true
                }

                val fixtureEntity=fixture.entity
                if(fixtureEntity==entity){
                    return@query true
                }

                configureEntity(fixtureEntity){
                    if(entity in playerCmps) {
                        lootCmps.getOrNull(it)?.let { lootCmp ->
                            lootCmp.interactEntity = entity
                        }
                    }
                }

                return@query true
            }
            attackCmp.state= AttackState.READY
        }
        val isDone=animationCmps.getOrNull(entity)?.isAnimationFinished()?:true
        if(isDone){
            attackCmp.state=AttackState.READY
        }
    }
    companion object {
        val AABB_RECT= Rectangle()
    }
}
