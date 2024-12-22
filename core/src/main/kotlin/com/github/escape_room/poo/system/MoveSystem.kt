package com.github.escape_room.poo.system

import com.github.escape_room.poo.component.MoveComponent
import com.github.escape_room.poo.component.PhsysicComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.math.component1
import ktx.math.component2

@AllOf([MoveComponent::class, PhsysicComponent::class])

class MoveSystem(
    private val moveCmps: ComponentMapper<MoveComponent>,
    private val phsysicCmps: ComponentMapper<PhsysicComponent>,
) : IteratingSystem() {

    override fun onTickEntity(entity: Entity) {
        val moveCmp = moveCmps[entity]
        val phsysicCmp = phsysicCmps[entity]
        val mass = phsysicCmp.body.mass
        val (velX,velY) = phsysicCmp.body.linearVelocity
        if(moveCmp.cos==0f && moveCmp.sin==0f) {
            phsysicCmp.impulse.set(
                mass*(0f - velX),
                mass*(0f-velY),
            )
            return
        }
        phsysicCmp.impulse.set(
            mass*(moveCmp.speed*moveCmp.cos-velX),
            mass*(moveCmp.speed*moveCmp.sin-velY),
        )
    }
}
