package com.github.escape_room.poo.component

enum class AttackState {
    READY, PREPARE, ATTACKING
}


data class AttackComponent(
    var doAttack: Boolean = false,
    var delay: Float = 0f,
    var maxDelay: Float = 0f,
    var extraRange: Float = 0f,
    var state: AttackState = AttackState.READY
) {
    val isReady: Boolean
        get()=state== AttackState.READY

    val isPrepared: Boolean
        get()=state== AttackState.PREPARE

    val isAttacking: Boolean
        get()=state== AttackState.ATTACKING

    fun startAttack() {
        state = AttackState.PREPARE
    }
}
