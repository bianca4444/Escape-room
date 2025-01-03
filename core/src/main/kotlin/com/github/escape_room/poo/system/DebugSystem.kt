package com.github.escape_room.poo.system

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.escape_room.poo.system.AttackSystem.Companion.AABB_RECT
import com.github.quillraven.fleks.IntervalSystem
import ktx.assets.disposeSafely
import ktx.graphics.use

class DebugSystem(
    private val phWorld: World,
    private val stage: Stage
) :IntervalSystem(enabled = true) {

    private lateinit var physicRenderer:Box2DDebugRenderer
    private lateinit var shapeRenderer:ShapeRenderer

    init {
        if(enabled){
            physicRenderer = Box2DDebugRenderer()
            shapeRenderer = ShapeRenderer()
        }
    }

    override fun onTick() {
        physicRenderer.render(phWorld,stage.camera.combined)
        shapeRenderer.use(ShapeRenderer.ShapeType.Line,stage.camera.combined){
            it.setColor(1f, 0f, 0f, 0f)
            it.rect(AABB_RECT.x, AABB_RECT.y, AABB_RECT.width-AABB_RECT.x, AABB_RECT.height-AABB_RECT.y)

        }
    }

    override fun onDispose() {
        if(enabled){
            physicRenderer.disposeSafely()
            shapeRenderer.disposeSafely()
        }
    }
}
