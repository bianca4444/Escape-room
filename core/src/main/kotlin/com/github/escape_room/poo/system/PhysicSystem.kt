package com.github.escape_room.poo.system

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.StaticBody
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Manifold
import com.badlogic.gdx.physics.box2d.World
import com.github.escape_room.poo.component.PhsysicComponent
import com.github.escape_room.poo.component.TiledComponent
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
    private val physicCmps: ComponentMapper<PhsysicComponent>,
    private val tiledCmps: ComponentMapper<TiledComponent>,
    private val collisionCmps: ComponentMapper<PhsysicComponent>
) : ContactListener, IteratingSystem(interval = Fixed(1/60f)) {

    init {
        phWorld.setContactListener(this)
    }

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

        physicCmp.prevPos.set(physicCmp.body.position)

        if(!physicCmp.impulse.isZero){
            physicCmp.body.applyLinearImpulse(physicCmp.impulse,physicCmp.body.worldCenter,true)
            physicCmp.impulse.setZero()
        }
    }
    override fun onAlphaEntity(entity: Entity, alpha: Float) {
        val physicCmp = physicCmps[entity]
        val imageCmp = imageCmps[entity]

        val (prevX,prevY)= physicCmp.prevPos
        val (bodyX, bodyY) = physicCmp.body.position
        imageCmp.image.run{
            setPosition(
                MathUtils.lerp(prevX,bodyX,alpha)-width*0.5f,
                MathUtils.lerp(prevY,bodyY,alpha)-height*0.5f
            )
        }
    }

    private val Fixture.entity: Entity
        get()=this.body.userData as Entity

    override fun beginContact(contact: Contact) {
        val entityA: Entity = contact.fixtureA.entity
        val entityB: Entity = contact.fixtureB.entity
        val isEntityATiledCollisionSensor = entityA in tiledCmps && contact.fixtureA.isSensor
        val isEntityBCollisionFixture = entityB in collisionCmps && !contact.fixtureB.isSensor

        val isEntityBTiledCollisionSensor = entityB in tiledCmps && contact.fixtureB.isSensor
        val isEntityACollisionFixture = entityA in collisionCmps && !contact.fixtureA.isSensor

        when {
            isEntityATiledCollisionSensor && isEntityBCollisionFixture ->{
                tiledCmps[entityA].nearbyEntities += entityB
            }
            isEntityBTiledCollisionSensor && isEntityACollisionFixture -> {
                tiledCmps[entityB].nearbyEntities += entityA
            }
        }
    }

    override fun endContact(contact: Contact) {
        val entityA: Entity = contact.fixtureA.entity
        val entityB: Entity = contact.fixtureB.entity
        val isEntityATiledCollisionSensor = entityA in tiledCmps && contact.fixtureA.isSensor
        val isEntityBTiledCollisionSensor = entityB in tiledCmps && contact.fixtureB.isSensor

        when {
            isEntityATiledCollisionSensor && !contact.fixtureB.isSensor ->{
                tiledCmps[entityA].nearbyEntities -= entityB
            }
            isEntityBTiledCollisionSensor && !contact.fixtureA.isSensor -> {
                tiledCmps[entityB].nearbyEntities -= entityA
            }
        }

    }

    private fun Fixture.isStaticBody()= this.body.type==StaticBody
    private fun Fixture.isDynamicBody()= this.body.type==DynamicBody
    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        contact.isEnabled=
            (contact.fixtureA.isStaticBody() && contact.fixtureB.isDynamicBody()) ||
                (contact.fixtureB.isStaticBody() && contact.fixtureA.isDynamicBody())

    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) = Unit



    companion object {
        private val log = logger<PhysicSystem>()
    }

}

