package com.github.escape_room.poo.screen

// Importuri necesare pentru grafică, batch-uri și scene

import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.escape_room.poo.component.AnimationComponent
import com.github.escape_room.poo.component.AnimationType
import com.github.escape_room.poo.component.imageComponent
import com.github.escape_room.poo.component.imageComponent.Companion.ImageComponentListener
import com.github.escape_room.poo.event.MapChangeEvent
import com.github.escape_room.poo.event.fire
import com.github.escape_room.poo.system.AnimationSystem
import com.github.escape_room.poo.system.RenderSystem
import com.github.quillraven.fleks.World
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.log.logger

// Clasa care reprezintă ecranul principal al jocului
class GameScreen: KtxScreen {

    // Batch folosit pentru desenarea texturilor


    // Scenă care gestionează actorii și elementele UI
    private val stage: Stage = Stage(ExtendViewport(16f, 9f))
    private val textureAtlas= TextureAtlas("assets/graphics/game.atlas")
    // Textură ce va fi desenată pe ecran

    private val world: World = World{
        inject(stage)
        inject(textureAtlas)

        componentListener<ImageComponentListener>()
        system<AnimationSystem>()
        system<RenderSystem>()
    }

    // Metodă apelată când ecranul devine activ
    override fun show() {
        log.debug { "Game Screen gets shown" } // Mesaj de debug în consolă

        world.systems.forEach { system ->
            if(system is EventListener){
                stage.addListener(system)
            }
        }


        val tiledMap= TmxMapLoader().load("map/map.tmx")
        stage.fire(MapChangeEvent(tiledMap))

        world.entity{
            add<imageComponent>{
                image=  Image().apply {
                    setSize(4f,4f)
                }
            }
            add<AnimationComponent>{
                nextAnimation("player", AnimationType.IDLE)
            }
        }

    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }


    // Metodă apelată la fiecare cadru (de obicei 60 FPS)
    override fun render(delta: Float) {
        world.update(delta)
    }

    // Metodă apelată pentru eliberarea resurselor când ecranul este distrus
    override fun dispose() {
        stage.disposeSafely()       // Eliberează resursele scenei
        textureAtlas.disposeSafely()
        world.dispose()
    }
    companion object {
        // Logger pentru mesaje de debug
        private val log = logger<GameScreen>()
    }
}
