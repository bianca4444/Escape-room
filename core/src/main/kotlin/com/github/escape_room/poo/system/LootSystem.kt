package com.github.escape_room.poo.system

import com.badlogic.gdx.graphics.g2d.Animation
import com.github.escape_room.poo.component.AnimationComponent
import com.github.escape_room.poo.component.AnimationType
import com.github.escape_room.poo.component.LootComponent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem

@AllOf([LootComponent::class])
class LootSystem(
    private val lootCmps: ComponentMapper<LootComponent>,
    private val aniCmps: ComponentMapper<AnimationComponent>,
) : IteratingSystem(){
    override fun onTickEntity(entity: Entity) {
        with(lootCmps[entity]){
            if(interactEntity==null){
                return
            }

            configureEntity(entity){lootCmps.remove(it)}
            aniCmps.getOrNull(entity)?.let {aniCmp ->
                aniCmp.nextAnimation(AnimationType.OPEN)
                aniCmp.playMode=Animation.PlayMode.NORMAL
            }
        }

    }
}
