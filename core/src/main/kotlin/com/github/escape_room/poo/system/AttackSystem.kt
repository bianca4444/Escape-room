package com.github.escape_room.poo.system

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.escape_room.poo.component.AnimationComponent
import com.github.escape_room.poo.component.AttackComponent
import com.github.escape_room.poo.component.AttackState
import com.github.escape_room.poo.component.DialogComponent
import com.github.escape_room.poo.component.LootComponent
import com.github.escape_room.poo.component.PhysicComponent
import com.github.escape_room.poo.component.PlayerComponent
import com.github.escape_room.poo.component.ImageComponent
import com.github.escape_room.poo.event.EntityAttackEvent
import com.github.escape_room.poo.event.fire
import com.github.escape_room.poo.system.EntitySpawnSystem.Companion.HIT_BOX_SENSOR
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.Qualifier
import ktx.box2d.query
import ktx.math.component1
import ktx.math.component2
import kotlin.collections.minusAssign
import kotlin.compareTo
import kotlin.times

@AllOf([AttackComponent::class, PhysicComponent::class, ImageComponent::class])
class AttackSystem(
    private val attackCmps: ComponentMapper<AttackComponent>,
    private val animationCmps: ComponentMapper<AnimationComponent>,
    private val imgCmps: ComponentMapper<ImageComponent>,
    private val physicCmps: ComponentMapper<PhysicComponent>,
    private val lootCmps: ComponentMapper<LootComponent>,
    private val playerCmps: ComponentMapper<PlayerComponent>,
    private val dialogCmps: ComponentMapper<DialogComponent>,
    private val phWorld: World,
    @Qualifier("GameStage") private val stage: Stage,
) : IteratingSystem() {
    override fun onTickEntity(entity: Entity) {
        val attackCmp = attackCmps[entity]

        if (attackCmp.state == AttackState.READY && !attackCmp.doAttack) {
            // no intention to attack -> do nothing
            return
        }

        if (attackCmp.doAttack && attackCmp.state == AttackState.PREPARE) {
            // attack intention and ready to attack -> start attack
            attackCmp.doAttack = false
            attackCmp.state = AttackState.ATTACKING
            attackCmp.delay = attackCmp.maxDelay
            return
        }

        attackCmp.delay -= deltaTime
        if (attackCmp.delay <= 0f && attackCmp.state == AttackState.ATTACKING) {
            // deal damage to nearby enemies
            attackCmp.state = AttackState.DEAL_DAMAGE

            animationCmps.getOrNull(entity)?.let { aniCmp ->
                stage.fire(EntityAttackEvent(aniCmp.atlasKey))
            }

            val image = imgCmps[entity].image
            val physicCmp = physicCmps[entity]

            val attackLeft = image.flipX
            val (x, y) = physicCmp.body.position
            val (offX, offY) = physicCmp.offset
            val (w, h) = physicCmp.size
            val halfW = w * 0.5f
            val halfH = h * 0.5f

            if (attackLeft) {
                AABB_RECT.set(
                    offX + x - halfW - attackCmp.extraRange,
                    offY + y - halfH,
                    offX + x + halfW,
                    offY + y + halfH
                )
            } else {
                AABB_RECT.set(
                    offX + x - halfW,
                    offY + y - halfH,
                    offX + x + halfW + attackCmp.extraRange,
                    offY + y + halfH
                )
            }

            phWorld.query(AABB_RECT.x, AABB_RECT.y, AABB_RECT.width, AABB_RECT.height) { fixture ->
                if (fixture.userData != HIT_BOX_SENSOR) {
                    // we are only interested if we detect hit-boxes of other entities
                    return@query true
                }

                val fixtureEntity = fixture.body.userData as Entity
                if (fixtureEntity == entity) {
                    // ignore the entity itself that is attacking
                    return@query true
                }

                val isAttackerPlayer = entity in playerCmps
                if (isAttackerPlayer && fixtureEntity in playerCmps) {
                    // player does not damage other player entities
                    return@query true
                } else if (!isAttackerPlayer && fixtureEntity !in playerCmps) {
                    // non-player entities do not damage other non-player entities
                    return@query true
                }

                // fixtureEntity refers to another entity that gets hit by the attack
                configureEntity(fixtureEntity) {
                    if (isAttackerPlayer) {
                        // player can open chests
                        lootCmps.getOrNull(it)?.let { lootCmp ->
                            lootCmp.interactEntity = entity
                        }
                        // player can trigger dialogs
                        dialogCmps.getOrNull(it)?.let { dialogCmp ->
                            dialogCmp.interactEntity = entity
                        }
                    }
                }
                return@query true
            }
        }

        val isDone = animationCmps.getOrNull(entity)?.isAnimationFinished() ?: true
        if (isDone) {
            attackCmp.state = AttackState.READY
        }
    }

    companion object {
        val AABB_RECT = Rectangle()
    }
}
