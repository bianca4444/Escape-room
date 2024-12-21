package com.github.escape_room.poo.system;

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Path
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.AllOf
import com.github.escape_room.poo.component.AnimationComponent
import com.github.escape_room.poo.component.AnimationComponent.Companion.NO_ANIMATION
import com.github.escape_room.poo.component.imageComponent
import ktx.app.gdxError
import ktx.collections.map
import ktx.log.logger

@AllOf([AnimationComponent::class, imageComponent::class])
class AnimationSystem(
    private val textureAtlas: TextureAtlas,
    private val animationCmps: ComponentMapper<AnimationComponent>,
    private val imageCmps: ComponentMapper<imageComponent>,
) : IteratingSystem() {
    private val cachedAnimations = mutableMapOf<String, Animation<TextureRegionDrawable>>()

    override fun onTickEntity(entity: Entity) {
        val aniCmp = animationCmps[entity]

            if(aniCmp.nextAnimation== NO_ANIMATION){
                aniCmp.stateTime+= deltaTime
            } else {
                aniCmp.animation=animation(aniCmp.nextAnimation)
                aniCmp.stateTime=0f
                aniCmp.nextAnimation=NO_ANIMATION
            }

            aniCmp.animation.playMode = aniCmp.playMode
            imageCmps[entity].image.drawable=aniCmp.animation.getKeyFrame(aniCmp.stateTime)



    }

    private fun animation(aniKeyPath: String): Animation<TextureRegionDrawable> {
        return cachedAnimations.getOrPut(aniKeyPath) {
            LOG.debug { "Creating new animation $aniKeyPath" }
            val regions = textureAtlas.findRegions(aniKeyPath)
            if (regions.isEmpty) {
                gdxError("There are no texture regions for $aniKeyPath")
            }
            Animation(DEFAULT_FRAME_DURATION, regions.map { TextureRegionDrawable(it) })
        }
    }

    companion object {
        private val LOG = logger<AnimationSystem>()
        private const val DEFAULT_FRAME_DURATION = 1 / 8f
    }
}
