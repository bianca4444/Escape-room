package com.github.escape_room.poo.system
import ktx.box2d.body
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.github.escape_room.poo.Escape_Room.Companion.UNIT_SCALE
import com.github.escape_room.poo.component.AnimationComponent
import com.github.escape_room.poo.component.AnimationModel
import com.github.escape_room.poo.component.AnimationType
import com.github.escape_room.poo.component.DEFAULT_SPEED
import com.github.escape_room.poo.component.MoveComponent
import com.github.quillraven.fleks.EntityCreateCfg
import com.github.escape_room.poo.component.SpawnCfg
import com.github.escape_room.poo.component.SpawnComponent
import com.github.escape_room.poo.component.imageComponent
import com.github.escape_room.poo.event.MapChangeEvent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.World
import jdk.jfr.internal.Type
import ktx.app.gdxError
import ktx.collections.gdxArrayOf
import ktx.math.vec2
import ktx.tiled.layer
import ktx.tiled.type
import ktx.tiled.x
import ktx.tiled.y
import com.github.escape_room.poo.component.PhsysicComponent.Companion.physicCmpFromImage
import com.github.escape_room.poo.component.PlayerComponent
import ktx.box2d.box


@AllOf([SpawnComponent::class])
class EntitySpawnSystem(
    private val phWorld: com.badlogic.gdx.physics.box2d.World,
    private val atlas: TextureAtlas,
    private val spawnCmps: ComponentMapper<SpawnComponent>,
) : EventListener, IteratingSystem() {
    private val cacheCfgs=mutableMapOf<String, SpawnCfg>()
    private val cacheSize=mutableMapOf<AnimationModel, Vector2>()

    override fun onTickEntity(entity: Entity) {
        with(spawnCmps[entity]) {
            val cfg = spawnCfg(type)
            val relativeSize= size(cfg.model)

            world.entity{
               val imageCmp = add<imageComponent>{
                    image =  Image().apply{
                        setPosition(location.x, location.y)
                        setSize(relativeSize.x, relativeSize.y)
                        setScaling(Scaling.fill)
                    }
                }
                add<AnimationComponent>{
                    nextAnimation(cfg.model, AnimationType.IDLE)
                }
                physicCmpFromImage(phWorld,imageCmp.image,BodyDef.BodyType.DynamicBody){ phCmp, width,height ->
                    box(width, height){
                        isSensor = false

                    }
                }
                if(cfg.speedScaling>0f) {
                    add<MoveComponent> {
                        speed = DEFAULT_SPEED * cfg.speedScaling
                    }
                }

                if(type == "Player"){
                    add<PlayerComponent>()
                }

            }
        }
        world.remove(entity)
    }

    private fun spawnCfg(type: String ): SpawnCfg = cacheCfgs.getOrPut(type){
        when{
            type == "Player" -> SpawnCfg(AnimationModel.PLAYER)
            type == "Chest" -> SpawnCfg(AnimationModel.CHEST)
            else -> gdxError("Type has no SpawnCfg")
        }
    }

    private fun size(model: AnimationModel)= cacheSize.getOrPut(model){
        val regions = atlas.findRegions("${model.atlasKey}/${AnimationType.IDLE.atlasKey}")
        if(regions.isEmpty){
            gdxError("There are no regions for the idle animation")
        }
        val firstFrame=regions.first()
        vec2(firstFrame.originalWidth*UNIT_SCALE,firstFrame.originalHeight*UNIT_SCALE)
    }

    override fun handle(event: Event): Boolean {
        when(event){
            is MapChangeEvent -> {
                val entityLayer=event.map.layer("entities")
                entityLayer.objects.forEach { mapObject ->
                    val type= mapObject.type ?: gdxError("Entity type not found")
                    world.entity{
                        add<SpawnComponent>{
                            this.type=type
                            this.location.set(mapObject.x*UNIT_SCALE,mapObject.y*UNIT_SCALE)
                        }
                    }
                }
                return true
            }
        }
        return false
    }


}
