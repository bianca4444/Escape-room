package com.github.escape_room.poo.component

import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

const val DEFAULT_SPEED=3f

data class SpawnCfg(
    val model: AnimationModel,
    val speedScaling:Float=1f,

)



data class SpawnComponent(
    var type: String = "",
    var location: Vector2 = vec2()
)

