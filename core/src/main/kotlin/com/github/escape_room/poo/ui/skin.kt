package com.github.escape_room.poo.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import ktx.assets.disposeSafely
import ktx.scene2d.Scene2DSkin
import ktx.style.*

enum class Drawables(
    val atlasKey: String,
) {
    PLAYER("player"),
    SLIME("slime"),
    FRAME_BGD("frame_bgd"),
    FRAME_FGD("frame_fgd"),

}


operator fun Skin.get(drawable: Drawables): Drawable = this.getDrawable(drawable.atlasKey)

enum class Fonts(
    val atlasRegionKey: String,
    val scaling: Float
) {
    DEFAULT("fnt_white", 0.20f),
    BIG("fnt_white", 0.5f),
    BIGGER("fnt_white", 0.75f);

    val skinKey = "Font_${this.name.lowercase()}"
    val fontPath = "ui/${this.atlasRegionKey}.fnt"
}

operator fun Skin.get(font: Fonts): BitmapFont = this.getFont(font.skinKey)

enum class Labels {
    FRAME,
    TITLE,
    LARGE;

    val skinKey = this.name.lowercase()
}

enum class Buttons {
    TEXT_BUTTON;

    val skinKey = this.name.lowercase()
}

fun loadSkin() {
    Scene2DSkin.defaultSkin = skin(TextureAtlas("ui/ui.atlas")) { skin ->
        label(Labels.FRAME.skinKey){
            font = BitmapFont(Gdx.files.internal("ui/fnt_white.fnt"), skin.getRegion("fnt_white"))
            background = skin[Drawables.FRAME_FGD]
        }
        loadFontSkin(skin)
        loadLabelSkin(skin)
        loadButtonSkin(skin)
    }
}

private fun loadFontSkin(skin: Skin) {
    Fonts.values().forEach { fnt ->
        skin[fnt.skinKey] =
            BitmapFont(Gdx.files.internal(fnt.fontPath), skin.getRegion(fnt.atlasRegionKey)).apply {
                data.markupEnabled = true
                data.setScale(fnt.scaling)
                color = Color.BLACK
            }
    }
}

private fun @SkinDsl Skin.loadButtonSkin(skin: Skin) {
    textButton(Buttons.TEXT_BUTTON.skinKey) {
        font = skin[Fonts.DEFAULT]
    }
}

private fun @SkinDsl Skin.loadLabelSkin(skin: Skin) {
    label(Labels.FRAME.skinKey) {
        font = skin[Fonts.DEFAULT]
        fontColor = Color.BLACK
        background = skin[Drawables.FRAME_FGD].apply {
            leftWidth = 2f
            rightWidth = 2f
            topHeight = 1f
        }
    }
    label(Labels.TITLE.skinKey) {
        font = skin[Fonts.BIG]
        fontColor = Color.RED
        background = skin[Drawables.FRAME_FGD].apply {
            leftWidth = 2f
            rightWidth = 2f
            topHeight = 1f
        }
    }
    label(Labels.LARGE.skinKey) {
        font = skin[Fonts.BIGGER]
    }
}

fun disposeSkin() = Scene2DSkin.defaultSkin.disposeSafely()
