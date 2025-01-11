package com.github.escape_room.poo.component

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable

enum class AttackState {
    READY, PREPARE, ATTACKING, DEAL_DAMAGE
}

data class AttackComponent(
    var doAttack: Boolean = false,
    var damage: Int = 0,
    var delay: Float = 0f,
    var maxDelay: Float = 0f,
    var extraRange: Float = 0f,
    var state: AttackState = AttackState.READY
) {
    fun isReady() = state == AttackState.READY

    fun startAttack() {
        state = AttackState.PREPARE
    }
}
