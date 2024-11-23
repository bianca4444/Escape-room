package com.github.escape_room.poo.screen

// Importuri necesare pentru grafică, batch-uri și scene
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.github.quillraven.fleks.World
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.graphics.use
import ktx.log.logger

// Clasa care reprezintă ecranul principal al jocului
class GameScreen: KtxScreen {

    // Batch folosit pentru desenarea texturilor
    private val spriteBatch: Batch = SpriteBatch()

    // Scenă care gestionează actorii și elementele UI
    private val stage: Stage = Stage(ExtendViewport(16f, 9f))

    // Textură ce va fi desenată pe ecran
    private val texture: Texture = Texture("assets/graphics/player.png")
    private val world: World = World{

    }

    // Metodă apelată când ecranul devine activ
    override fun show() {
        log.debug { "Game Screen gets shown" } // Mesaj de debug în consolă
        stage.addActor(
            Image(texture).apply {
                setPosition(1f,1f)
                setSize(1f,1f)
                setScaling(Scaling.fill)
            }
        )
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }
    companion object {
        // Logger pentru mesaje de debug
        private val log = logger<GameScreen>()
    }

    // Metodă apelată la fiecare cadru (de obicei 60 FPS)
    override fun render(delta: Float) {
        with(stage) {
            act(delta)
            draw()
        }
    }

    // Metodă apelată pentru eliberarea resurselor când ecranul este distrus
    override fun dispose() {
        spriteBatch.disposeSafely() // Eliberează resursele batch-ului
        texture.disposeSafely()     // Eliberează memoria ocupată de textura
        stage.disposeSafely()       // Eliberează resursele scenei
        world.dispose()
    }
}
