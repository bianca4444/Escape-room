package com.github.escape_room.poo.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.escape_room.poo.component.TiledComponent
import com.github.escape_room.poo.event.CollisionDespawnEvent
import com.github.escape_room.poo.event.fire
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.Qualifier

@AllOf([TiledComponent::class])
class CollisionDespawnSystem(
    @Qualifier("GameStage") private val stage: Stage,
    private val tiledCmps: ComponentMapper<TiledComponent>,
) : IteratingSystem() {
    override fun onTickEntity(entity: Entity) {
        // for existing collision tiled entities we check if there are no nearby entities anymore
        // and remove them in that case
        if (tiledCmps[entity].nearbyEntities.isEmpty()) {
            stage.fire(CollisionDespawnEvent(tiledCmps[entity].cell))
            world.remove(entity)
        }
    }
}
