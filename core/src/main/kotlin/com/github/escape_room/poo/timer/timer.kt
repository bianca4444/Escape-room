package timer

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont

class Timer : Actor() {
    private val label: Label
    private var elapsedTime = 0f

    init {
        // Creează o instanță de Skin și adaugă fontul
        val skin = Skin().apply {
            add("default-font", BitmapFont(Gdx.files.internal("ui/fnt_white.fnt")))
            add("default", Label.LabelStyle(BitmapFont(Gdx.files.internal("ui/fnt_white.fnt")), Color.BLACK))
        }

        // Creează un label pentru Timer
        label = Label("00:00", skin)
    }

    override fun act(delta: Float) {
        super.act(delta)
        elapsedTime += delta
        val minutes = (elapsedTime / 60).toInt()
        val seconds = (elapsedTime % 60).toInt()
        label.setText(String.format("%02d:%02d", minutes, seconds))
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        label.draw(batch, parentAlpha) // Desenează timer-ul
    }
}
