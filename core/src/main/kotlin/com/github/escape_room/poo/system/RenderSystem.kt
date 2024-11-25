package com.github.escape_room.poo.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.escape_room.poo.component.imageComponent
import com.github.quillraven.fleks.*
import com.github.quillraven.fleks.collection.compareEntity


@AllOf([imageComponent::class])


class RenderSystem(
    private val stage: Stage,
    private val imageCmps: ComponentMapper<imageComponent>
) : IteratingSystem(
    comparator = compareEntity {e1,e2 ->imageCmps[e1].compareTo(imageCmps[e2])}
) {

    override fun onTick() {
        super.onTick()

        with(stage) {
            viewport.apply()
            act(deltaTime)
            draw()
        }
    }
    override fun onTickEntity(entity: Entity) {
        imageCmps[entity].image.toFront()
    }
}
