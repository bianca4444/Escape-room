package com.github.escape_room.poo.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.Input.Keys.A
import com.badlogic.gdx.Input.Keys.D
import com.badlogic.gdx.Input.Keys.DOWN
import com.badlogic.gdx.Input.Keys.LEFT
import com.badlogic.gdx.Input.Keys.RIGHT
import com.badlogic.gdx.Input.Keys.S
import com.badlogic.gdx.Input.Keys.SPACE
import com.badlogic.gdx.Input.Keys.UP
import com.badlogic.gdx.Input.Keys.W
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.github.escape_room.poo.component.AttackComponent
import com.github.escape_room.poo.component.MoveComponent
import com.github.escape_room.poo.component.PlayerComponent
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.World
import ktx.app.KtxInputAdapter
import kotlin.collections.set

fun gdxInputProcessor(processor: InputProcessor) {
    val currProcessor = Gdx.input.inputProcessor
    if (currProcessor == null) {
        Gdx.input.inputProcessor = processor
    } else {
        if (currProcessor is InputMultiplexer) {
            if (processor !in currProcessor.processors) {
                currProcessor.addProcessor(processor)
            }
        } else {
            Gdx.input.inputProcessor = InputMultiplexer(currProcessor, processor)
        }
    }
}

class PlayerInputProcessor(
    world: World,
    private val moveCmps: ComponentMapper<MoveComponent> = world.mapper(),
    private val attackCmps: ComponentMapper<AttackComponent> = world.mapper(),
) : KtxInputAdapter {
    private val playerEntities = world.family(allOf = arrayOf(PlayerComponent::class))
    private var playerCos = 0f
    private var playerSin = 0f
    private var paused = false
    private val pressedKeys = mutableSetOf<Int>()

    init {
        gdxInputProcessor(this)
    }

    private fun Int.isMovementKey(): Boolean {
        return this == UP || this == DOWN || this == LEFT || this == RIGHT
    }

    private fun isPressed(keycode: Int): Boolean = keycode in pressedKeys

    private fun updatePlayerMovement() {
        playerEntities.forEach { player ->
            with(moveCmps[player]) {
                cosSin.set(playerCos, playerSin).nor()
            }
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        pressedKeys += keycode
        if (keycode.isMovementKey()) {
            when (keycode) {
                UP -> playerSin = 1f
                DOWN -> playerSin = -1f
                RIGHT -> playerCos = 1f
                LEFT -> playerCos = -1f
            }
            updatePlayerMovement()
            return true
        } else if (keycode == SPACE) {
            playerEntities.forEach { attackCmps[it].doAttack = true }
            return true
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        pressedKeys -= keycode
        if (keycode.isMovementKey()) {
            when (keycode) {
                UP -> playerSin = if (isPressed(DOWN)) -1f else 0f
                DOWN -> playerSin = if (isPressed(UP)) 1f else 0f
                RIGHT -> playerCos = if (isPressed(LEFT)) -1f else 0f
                LEFT -> playerCos = if (isPressed(RIGHT)) 1f else 0f
            }
            updatePlayerMovement()
            return true
        }
        return false
    }
}
