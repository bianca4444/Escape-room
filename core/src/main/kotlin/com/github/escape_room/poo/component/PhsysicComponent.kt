package com.github.escape_room.poo.component

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Shape2D
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.github.escape_room.poo.Escape_Room.Companion.UNIT_SCALE
import com.github.escape_room.poo.system.CollisionSpawnSystem.Companion.SPAWN_AREA_SIZE
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.EntityCreateCfg
import ktx.app.gdxError
import ktx.box2d.BodyDefinition
import ktx.box2d.body
import ktx.box2d.circle
import ktx.box2d.loop
import ktx.math.vec2
import java.awt.geom.Rectangle2D

class PhsysicComponent {
    val prevPos=vec2()
    val impulse = vec2()
    lateinit var body: Body

    companion object {
        fun EntityCreateCfg.physicCmpFromShape2D(
            world: World,
            x:Int,
            y:Int,
            shape: Shape2D,
        ) : PhsysicComponent {
            when(shape) {
                is Rectangle -> {
                    val bodyX = x+shape.x*UNIT_SCALE
                    val bodyY = y+shape.y*UNIT_SCALE
                    val bodyW = shape.width*UNIT_SCALE
                    val bodyH = shape.height*UNIT_SCALE
                    return add{
                        body=world.body(BodyType.StaticBody) {
                            position.set(bodyX, bodyY)
                            fixedRotation = true
                            allowSleep = false
                            loop(
                                vec2(0f,0f),
                                vec2(bodyW, 0f),
                                vec2(bodyW, bodyH),
                                vec2(0f, bodyH)
                            )
                            circle(SPAWN_AREA_SIZE+2f){isSensor = true}
                        }
                    }

                }
                else -> gdxError("Shape not found")
            }
        }


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
