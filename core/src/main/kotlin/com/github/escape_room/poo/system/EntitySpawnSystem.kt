package com.github.escape_room.poo.system

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.utils.Scaling
import com.github.quillraven.fleks.*
import com.github.escape_room.poo.Escape_Room.Companion.UNIT_SCALE
import com.github.escape_room.poo.actor.FlipImage
import com.github.escape_room.poo.component.*
import com.github.escape_room.poo.component.PhysicComponent.Companion.bodyFromImageAndCfg
import com.github.escape_room.poo.component.SpawnCfg.Companion.DEFAULT_ATTACK_DAMAGE
import com.github.escape_room.poo.component.SpawnCfg.Companion.DEFAULT_SPEED
import com.github.escape_room.poo.event.MapChangeEvent
import ktx.app.gdxError
import ktx.box2d.circle
import ktx.log.logger
import ktx.math.vec2
import ktx.tiled.*
import ktx.tiled.set
import ktx.tiled.type
import ktx.tiled.x
import ktx.tiled.y
import kotlin.math.roundToInt
import kotlin.times


@AllOf([SpawnComponent::class])
class EntitySpawnSystem(
    @Qualifier("GameAtlas") private val atlas: TextureAtlas,
    private val physicWorld: World,
    private val spawnCmps: ComponentMapper<SpawnComponent>,
    private val playerCmps: ComponentMapper<PlayerComponent>,
) : EventListener, IteratingSystem() {

    private val cachedCfgs = mutableMapOf<String, SpawnCfg>()
    private val cachedSizes = mutableMapOf<String, Vector2>()
    private val playerEntities = world.family(allOf = arrayOf(PlayerComponent::class))

    override fun onTickEntity(entity: Entity) {
        with(spawnCmps[entity]) {
            val cfg = spawnCfg(type)
            val relativeSize = size(cfg.atlasKey)
            LOG.debug { "Spawning entity of type $type with size $relativeSize" }

            val spawnedEntity = world.entity {
                val imageCmp = add<ImageComponent> {
                    image = FlipImage().apply {
                        setScaling(Scaling.fill)
                        setPosition(location.x, location.y)
                        setSize(relativeSize.x * cfg.scaleSize, relativeSize.y * cfg.scaleSize)
                        color = this@with.color
                    }
                }

                add<AnimationComponent> {
                    nextAnimation(cfg.atlasKey, AnimationType.IDLE)
                }

                val physicCmp = add<PhysicComponent> {
                    body = bodyFromImageAndCfg(physicWorld, imageCmp.image, cfg)
                }


                if (cfg.scaleSpeed != 0f) {
                    add<MoveComponent> { speed = DEFAULT_SPEED * cfg.scaleSpeed }
                }

                if (cfg.canAttack) {
                    add<AttackComponent> {
                        maxDelay = cfg.attackDelay
                        damage = (DEFAULT_ATTACK_DAMAGE * cfg.scaleAttackDamage).roundToInt()
                        extraRange = cfg.attackExtraRange
                    }
                }


                if (cfg.bodyType != BodyDef.BodyType.StaticBody) {
                    // entity is not static -> add collision component to spawn
                    // collision entities around it
                    add<CollisionComponent>()
                }

                if (cfg.dialogId != DialogId.NONE) {
                    add<DialogComponent> {
                        dialogId = cfg.dialogId
                    }
                }

                if (type == PLAYER_TYPE) {
                    add<PlayerComponent>()
                    // add state component at the end since its ComponentListener initialization logic
                    // depends on some components added above
                    add<StateComponent>()
                } else if (type == CHEST_TYPE) {
                    add<LootComponent>()
                } else {
                    // any other entity gets an AIComponent to potentially
                    // use a behavior tree for its behavior.
                    // AIComponent entities also have a list of nearby other entities
                    // that they can interact with

                    // such entities also get an "action sensor"
                    // entities who are within that sensor get added to the nearby entities list
                    physicCmp.body.circle(4f) {
                        isSensor = true
                        userData = ACTION_SENSOR
                    }
                }
            }

        }

        world.remove(entity)
    }

    private fun spawnCfg(type: String): SpawnCfg = cachedCfgs.getOrPut(type) {
        when {
            // player is 48x48 graphic -> scale down physic body to match 16x16 world
            type == PLAYER_TYPE -> PLAYER_CFG
            // chest gets a StaticBody so that entities cannot walk through it
            // because DynamicBody entities do not collide with each other
            type == CHEST_TYPE -> SpawnCfg(
                "chest",
                bodyType = BodyDef.BodyType.StaticBody,
                canAttack = false,
                lifeScale = 0f,
            )
            // slim is a 32x32 graphic -> scale down physic body to match 16x16 world
            type == "SLIME" -> SpawnCfg(
                "slime",
                lifeScale = 0.75f,
                scalePhysic = vec2(0.3f, 0.3f),
                physicOffset = vec2(0f, -2f * UNIT_SCALE),
                aiTreePath = "ai/slime.tree",
                hasLight = true,
            )

            type == "BLOB" -> SpawnCfg(
                atlasKey = "slime",
                scalePhysic = vec2(0.3f, 0.3f),
                physicOffset = vec2(0f, -2f * UNIT_SCALE),
                hasLight = true,
                dialogId = DialogId.BLOB,
                lifeScale = 0f
            )

            type.isNotBlank() -> SpawnCfg(type.lowercase())
            else -> gdxError("SpawnType must be specified")
        }
    }

    private fun size(atlasKey: String): Vector2 {
        return cachedSizes.getOrPut(atlasKey) {
            val regions = atlas.findRegions("$atlasKey/${AnimationType.IDLE.atlasKey}")
            if (regions.isEmpty) {
                gdxError("There are no texture regions for $atlasKey")
            }
            val firstFrame = regions.first()
            vec2(firstFrame.originalWidth * UNIT_SCALE, firstFrame.originalHeight * UNIT_SCALE)
        }

    }

    override fun handle(event: Event?): Boolean {
        if (event is MapChangeEvent) {
            val entityLayer = event.map.layer("entities")
            entityLayer.objects.forEach { mapObj ->
                val typeStr = mapObj.name ?: gdxError("MapObject ${mapObj.id} of 'entities' layer does not have a NAME")

                if (typeStr == PLAYER_TYPE && playerEntities.isNotEmpty) {
                    // spawn player only once
                    return@forEach
                }

                world.entity {
                    add<SpawnComponent> {
                        type = typeStr
                        location.set(mapObj.x * UNIT_SCALE, mapObj.y * UNIT_SCALE)
                        color = mapObj.property("color", Color.WHITE)
                    }
                }
            }
            return true
        }
        return false
    }

    companion object {
        private val LOG = logger<EntitySpawnSystem>()
        private const val PLAYER_TYPE = "PLAYER"
        private const val CHEST_TYPE = "CHEST"
        const val ACTION_SENSOR = "ActionSensor"
        const val HIT_BOX_SENSOR = "HitBoxSensor"

        val PLAYER_CFG = SpawnCfg(
            "player",
            scaleSpeed = 3f,
            scalePhysic = vec2(0.3f, 0.3f),
            physicOffset = vec2(0f, -10f * UNIT_SCALE),
            scaleAttackDamage = 1.25f,
            attackExtraRange = 0.6f,
            hasLight = true,
        )
    }
}
