package com.github.escape_room.poo.component

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.escape_room.poo.actor.FlipImage
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Qualifier
import kotlin.text.compareTo

class ImageComponent : Comparable<ImageComponent> {
    lateinit var image: FlipImage
    var layer = 0

    override fun compareTo(other: ImageComponent): Int {
        val layerDiff = layer.compareTo(other.layer)
        return if (layerDiff != 0) {
            layerDiff
        } else {
            val yDiff = other.image.y.compareTo(image.y)
            if (yDiff != 0) {
                yDiff
            } else {
                other.image.x.compareTo(image.x)
            }
        }
    }

    companion object {
        class ImageComponentListener(
            @Qualifier("GameStage") private val stage: Stage,
        ) : ComponentListener<ImageComponent> {
            override fun onComponentAdded(entity: Entity, component: ImageComponent) {
                stage.addActor(component.image)
            }

            override fun onComponentRemoved(entity: Entity, component: ImageComponent) {
                stage.root.removeActor(component.image)
            }
        }
    }
}
