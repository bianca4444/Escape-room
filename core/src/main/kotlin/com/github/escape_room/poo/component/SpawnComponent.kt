package com.github.escape_room.poo.component

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import ktx.math.vec2

const val DEFAULT_SPEED=3f

data class SpawnCfg(
    val model: AnimationModel,
    val speedScaling:Float=1f,
    val canAttack:Boolean=true,
    val attackDelay:Float=0.2f,
    val lootable:Boolean=false,
    val attackExtraRange: Float=0f,
    val physicScaling: Vector2=vec2(1f,1f),
    val physicOffset: Vector2=vec2(0f, 0f),
    val bodyType: BodyType = BodyType.DynamicBody,

)



data class SpawnComponent(
    var type: String = "",
    var location: Vector2 = vec2()
)

