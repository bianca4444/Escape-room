
package com.github.escape_room.poo.screen

// Importuri necesare pentru grafică, batch-uri și scene

import com.badlogic.gdx.ai.GdxAI
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.github.quillraven.fleks.world
import com.github.escape_room.poo.Escape_Room
import com.github.escape_room.poo.component.ImageComponent.Companion.ImageComponentListener
import com.github.escape_room.poo.component.PhysicComponent.Companion.PhysicComponentListener
import com.github.escape_room.poo.component.StateComponent.Companion.StateComponentListener
import com.github.escape_room.poo.dialog.Dialog
import com.github.escape_room.poo.dialog.Node
import com.github.escape_room.poo.dialog.dialog
import com.github.escape_room.poo.event.EntityDialogEvent
import com.github.escape_room.poo.event.fire
import com.github.escape_room.poo.input.PlayerInputProcessor
import com.github.escape_room.poo.input.gdxInputProcessor
import com.github.escape_room.poo.system.*
import com.github.escape_room.poo.ui.disposeSkin
import com.github.escape_room.poo.ui.loadSkin
import com.github.escape_room.poo.ui.model.DialogModel
import com.github.escape_room.poo.ui.view.*
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.box2d.createWorld
import ktx.log.logger
import ktx.scene2d.actors

// Clasa care reprezintă ecranul principal al jocului
class GameScreen(game: Escape_Room) : KtxScreen {
    private val gameStage = game.gameStage
    private val uiStage = game.uiStage
    private val gameAtlas = TextureAtlas("graphics/game.atlas")
    private val phWorld = createWorld(gravity = Vector2.Zero).apply {
        autoClearForces = false
    }
    // Textură ce va fi desenată pe ecran

    private val eWorld = world {
        injectables {
            add(phWorld)
            add("GameStage", gameStage)
            add("UiStage", uiStage)
            add("GameAtlas", gameAtlas)
        }

        components {
            add<PhysicComponentListener>()
            add<ImageComponentListener>()
            add<StateComponentListener>()

        }

        systems {
            add<EntitySpawnSystem>()
            add<CollisionSpawnSystem>()
            add<CollisionDespawnSystem>()
            add<PhysicSystem>()
            add<AnimationSystem>()
            add<MoveSystem>()
            add<AttackSystem>()
            add<LootSystem>()
            add<DialogSystem>()
            add<StateSystem>()
            add<PortalSystem>()
            add<CameraSystem>()
            add<RenderSystem>()
            add<AudioSystem>()
            add<DebugSystem>()
        }
    }

    init {
        loadSkin()
        eWorld.systems.forEach { sys ->
            if (sys is EventListener) {
                gameStage.addListener(sys)
            }
        }
        PlayerInputProcessor(eWorld)
        gdxInputProcessor(uiStage)

        // UI
        uiStage.actors {
            dialogView(DialogModel(gameStage))
        }
            val testDialog: Dialog = dialog("testDialog") {
                node(0, "Escape-Room --- Learning platform based on gamification"){
                    option("Next"){
                        action = { this@dialog.goToNode(1)}
                    }
                }
                node(1, "Gender, Digitalization, Green: Ensuring a Sustainable Future for all in Europe" ) {
                    option("Next") {
                        action = { this@dialog.goToNode(2) }
                    }
                }
                node(2, "Team Leader - Dumitrescu Bianca Alexandra – dumitrescu.bianca@stud.acs.upb.ro \n" +
                        "Joița Fabian Gabriel – joitafabian@gmail.com \n" +
                        "Jugulete George Marius Alexandru – george.jugulete@stud.acs.upb.ro\n" +
                        "Manescu Daria Ioana – dariamanescuioana8@gmail.com\n" +
                        "Sandu Andreea Diana – andreea_diana.sandu@stud.acs.upb.ro ") {
                    option("Next") {
                        action = { this@dialog.goToNode(3) }
                    }
                }
                node(3, "Teachers: Mihai Caramihai, Daniel Chis") {
                    option("Start Game") {
                        action = { this@dialog.end() }
                    }
                }

            }
            testDialog.start()
            gameStage.fire(EntityDialogEvent(testDialog))

    }
        // Metodă apelată când ecranul devine activ
        override fun show() {
            eWorld.system<PortalSystem>().setMap("map/map.tmx")

        }

    override fun render(delta: Float) {
        val dt = delta.coerceAtMost(0.25f)
        GdxAI.getTimepiece().update(dt)
        eWorld.update(dt)
    }

    // Metodă apelată pentru eliberarea resurselor când ecranul este distrus
    override fun dispose() {
        eWorld.dispose()
        phWorld.disposeSafely()
        gameAtlas.disposeSafely()
        disposeSkin()
    }
}
