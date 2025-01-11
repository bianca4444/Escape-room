package com.github.escape_room.poo.system

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.escape_room.poo.component.PlayerComponent
import com.github.escape_room.poo.component.ImageComponent
import com.github.escape_room.poo.event.MapChangeEvent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.Qualifier
import ktx.tiled.height
import ktx.tiled.width
import kotlin.math.max
import kotlin.math.min
import kotlin.text.toFloat


@AllOf([PlayerComponent::class, ImageComponent::class])
class CameraSystem(
    @Qualifier("GameStage") stage: Stage,
    private val imageCmps: ComponentMapper<ImageComponent>,
) : EventListener, IteratingSystem() {
    private val camera: Camera = stage.camera
    private var maxW = 0f
    private var maxH = 0f

    override fun onTickEntity(entity: Entity) {
        // we center on the image because it has an
        // interpolated position for rendering which makes
        // the game smoother
        with(imageCmps[entity]) {
            val viewW = camera.viewportWidth * 0.5f
            val viewH = camera.viewportHeight * 0.5f
            val camMinW = min(viewW, maxW - viewW)
            val camMaxW = max(viewW, maxW - viewW)
            val camMinH = min(viewH, maxH - viewH)
            val camMaxH = max(viewH, maxH - viewH)
            camera.position.set(
                image.x.coerceIn(camMinW, camMaxW),
                image.y.coerceIn(camMinH, camMaxH),
                camera.position.z
            )
        }
    }

    override fun handle(event: Event?): Boolean {
        if (event is MapChangeEvent) {
            maxW = event.map.width.toFloat()
            maxH = event.map.height.toFloat()
            return true
        }
        return false
    }
}
