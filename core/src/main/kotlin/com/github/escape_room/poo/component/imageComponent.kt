package com.github.escape_room.poo.component

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity

class imageComponent: Comparable<imageComponent> {
    lateinit var image: Image
    override fun compareTo(other: imageComponent): Int {
        val yDiff=other.image.y.compareTo(image.y)
        return if(yDiff!=0){
            yDiff
        } else {
            other.image.x.compareTo(image.x)
        }
    }

    companion object {
        class ImageComponentListener(
            private val stage: Stage
        ) : ComponentListener <imageComponent>{
            override fun onComponentAdded(entity: Entity, component: imageComponent) {
                stage.addActor(component.image)
            }
            override fun onComponentRemoved(entity: Entity, component: imageComponent) {
                stage.root.removeActor(component.image)
            }
        }
    }

}
