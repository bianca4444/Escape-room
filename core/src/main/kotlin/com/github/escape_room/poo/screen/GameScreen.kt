package com.github.escape_room.poo.screen

// Importuri necesare pentru grafică, batch-uri și scene
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.escape_room.poo.component.imageComponent
import com.github.escape_room.poo.component.imageComponent.Companion.ImageComponentListener
import com.github.escape_room.poo.system.RenderSystem
import com.github.quillraven.fleks.World
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.log.logger

// Clasa care reprezintă ecranul principal al jocului
class GameScreen: KtxScreen {

    // Batch folosit pentru desenarea texturilor


    // Scenă care gestionează actorii și elementele UI
    private val stage: Stage = Stage(ExtendViewport(16f, 9f))

    // Textură ce va fi desenată pe ecran
    private val playerTexture: Texture = Texture("assets/graphics/player.png")
    private val world: World = World{
        inject(stage)

        componentListener<ImageComponentListener>()

        system<RenderSystem>()
    }

    // Metodă apelată când ecranul devine activ
    override fun show() {
        log.debug { "Game Screen gets shown" } // Mesaj de debug în consolă

        world.entity{
            add<imageComponent>{
                image=  Image(TextureRegion(playerTexture,48,48)).apply {
                    setSize(4f,4f)
                }
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
        playerTexture.disposeSafely()     // Eliberează memoria ocupată de textura
        world.dispose()
    }
    companion object {
        // Logger pentru mesaje de debug
        private val log = logger<GameScreen>()
    }
}
