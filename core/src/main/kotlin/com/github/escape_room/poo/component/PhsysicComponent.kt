package com.github.escape_room.poo.component

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.EntityCreateCfg
import ktx.box2d.BodyDefinition
import ktx.box2d.body
import ktx.math.vec2

class PhsysicComponent {
    val prevPos=vec2()
    val impulse = vec2()
    lateinit var body: Body

    companion object {
        fun EntityCreateCfg.physicCmpFromImage(
           world: World,
           image: Image,
           bodyType: BodyType,
           fixtureAction: BodyDefinition.(PhsysicComponent, Float, Float) -> Unit
        ):PhsysicComponent {
            val x = image.x
            val y = image.y
            val w=image.width
            val h=image.height
            return add {
                body=world.body(bodyType) {
                    position.set(x+w/2, y+h/2)
                    fixedRotation=true
                    allowSleep=true
                    this.fixtureAction(this@add,w,h)
                }
            }
        }
        class PhsysicComponentListener : ComponentListener<PhsysicComponent>{
            override fun onComponentAdded(entity: Entity, component: PhsysicComponent) {
                component.body.userData=entity
            }
            override fun onComponentRemoved(entity: Entity, component: PhsysicComponent) {
                val body = component.body
                body.world.destroyBody(body)
                body.userData=null
            }
        }
    }
}
