package com.github.escape_room.poo.system

import com.badlogic.gdx.physics.box2d.World
import com.github.escape_room.poo.component.PhsysicComponent
import com.github.escape_room.poo.component.imageComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Fixed
import com.github.quillraven.fleks.IteratingSystem
import jdk.internal.org.jline.utils.Colors.h
import ktx.log.logger
import ktx.math.component1
import ktx.math.component2

@AllOf([PhsysicComponent::class, imageComponent::class])

class PhysicSystem (
    private val phWorld: World,
    private val imageCmps: ComponentMapper<imageComponent>,
    private val physicCmps: ComponentMapper<PhsysicComponent>
) : IteratingSystem(interval = Fixed(1/60f)) {

    override fun onUpdate() {
        if(phWorld.autoClearForces){
            log.error { "Autoclearforces must be set to false" }
            phWorld.autoClearForces = false
        }
        super.onUpdate()
        phWorld.clearForces()
    }

    override fun onTick() {
        super.onTick()
        phWorld.step(deltaTime,6,2) //din documentatie
    }

    override fun onTickEntity(entity: Entity) {
        val physicCmp = physicCmps[entity]
        val imageCmp = imageCmps[entity]

        if(!physicCmp.impulse.isZero){
            physicCmp.body.applyLinearImpulse(physicCmp.impulse,physicCmp.body.worldCenter,true)
            physicCmp.impulse.setZero()
        }

        val (bodyX, bodyY) = physicCmp.body.position
        imageCmp.image.run{
            setPosition(bodyX-width*0.5f, bodyY-height*0.5f)
        }

    }

    companion object {
        private val log = logger<PhysicSystem>()
    }

}

