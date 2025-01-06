package com.github.escape_room.poo.screen

// Importuri necesare pentru grafică, batch-uri și scene

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.escape_room.poo.component.AnimationComponent
import com.github.escape_room.poo.component.AnimationModel
import com.github.escape_room.poo.component.AnimationType
import com.github.escape_room.poo.component.PhsysicComponent
import com.github.escape_room.poo.component.PhsysicComponent.Companion.PhsysicComponentListener
import com.github.escape_room.poo.component.StateComponent
import com.github.escape_room.poo.component.StateComponent.Companion.StateComponentListener
import com.github.escape_room.poo.component.imageComponent
import com.github.escape_room.poo.component.imageComponent.Companion.ImageComponentListener
import com.github.escape_room.poo.dialog.Dialog
import com.github.escape_room.poo.dialog.dialog
import com.github.escape_room.poo.event.EntityDialogEvent
import com.github.escape_room.poo.event.MapChangeEvent
import com.github.escape_room.poo.event.fire
import com.github.escape_room.poo.input.PlayerKeyboardInputProcessor
import com.github.escape_room.poo.input.gdxInputProcessor
import com.github.escape_room.poo.system.AnimationSystem
import com.github.escape_room.poo.system.AttackSystem
import com.github.escape_room.poo.system.AudioSystem
import com.github.escape_room.poo.system.CameraSystem
import com.github.escape_room.poo.system.CollisionDespawnSystem
import com.github.escape_room.poo.system.CollisionSpawnSystem
import com.github.escape_room.poo.system.DebugSystem
import com.github.escape_room.poo.system.DialogSystem
import com.github.escape_room.poo.system.EntitySpawnSystem
import com.github.escape_room.poo.system.LootSystem
import com.github.escape_room.poo.system.MoveSystem
import com.github.escape_room.poo.system.PhysicSystem
import com.github.escape_room.poo.system.RenderSystem
import com.github.escape_room.poo.system.StateSystem
import com.github.escape_room.poo.ui.model.DialogModel
import com.github.escape_room.poo.ui.view.dialogView
import com.github.escape_room.poo.ui.view.gameView
import com.github.quillraven.fleks.World
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.log.logger
import ktx.math.vec2
import ktx.scene2d.actors

// Clasa care reprezintă ecranul principal al jocului
class GameScreen: KtxScreen {

    // Batch folosit pentru desenarea texturilor


    // Scenă care gestionează actorii și elementele UI
    private val stage: Stage = Stage(ExtendViewport(16f, 9f))
    private val uiStage: Stage = Stage(ExtendViewport(320f, 180f))
    private val textureAtlas= TextureAtlas("assets/graphics/game.atlas")
    private var currentMap: TiledMap? = null
    private val phWorld = createWorld(gravity = vec2()).apply {
        autoClearForces = false
    }
    // Textură ce va fi desenată pe ecran

    private val eWorld: World = World{
        inject(stage)
        inject("uiStage",uiStage)
        inject(textureAtlas)
        inject(phWorld)


        componentListener<ImageComponentListener>()
        componentListener<PhsysicComponentListener>()
        componentListener<StateComponentListener>()

        system<EntitySpawnSystem>()
        system<CollisionSpawnSystem>()
        system<CollisionDespawnSystem>()
        system<MoveSystem>()
        system<AttackSystem>()
        system<LootSystem>()
        system<DialogSystem>()
        system<PhysicSystem>()
        system<AnimationSystem>()
        system<StateSystem>()
        system<CameraSystem>()
        system<RenderSystem>()
        system<AudioSystem>()
        //system<DebugSystem>() Apelam sistemul doar daca verificam ceva
    }

    init {
        uiStage.actors {
            dialogView(DialogModel(stage))

        }
    }

    // Metodă apelată când ecranul devine activ
    override fun show() {
        log.debug { "Game Screen gets shown" } // Mesaj de debug în consolă

        eWorld.systems.forEach { system ->
            if(system is EventListener){
                stage.addListener(system)
            }
        }

        val tiledMap= TmxMapLoader().load("map/map.tmx")
        currentMap = tiledMap
        stage.fire(MapChangeEvent(tiledMap!!))

        PlayerKeyboardInputProcessor(eWorld)
        gdxInputProcessor(uiStage)
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        uiStage.viewport.update(width, height, true)
        super.resize(width, height)
    }


    // Metodă apelată la fiecare cadru (de obicei 60 FPS)
    override fun render(delta: Float) {
        eWorld.update(delta.coerceAtMost(0.25f))
    }

    // Metodă apelată pentru eliberarea resurselor când ecranul este distrus
    override fun dispose() {
        stage.disposeSafely()
        uiStage.disposeSafely()
        textureAtlas.disposeSafely()
        eWorld.dispose()
        currentMap?.disposeSafely()
        phWorld.disposeSafely()
    }
    companion object {
        // Logger pentru mesaje de debug
        private val log = logger<GameScreen>()
    }
}
