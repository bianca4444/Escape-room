package com.github.escape_room.poo.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.Input.Keys.A
import com.badlogic.gdx.Input.Keys.D
import com.badlogic.gdx.Input.Keys.S
import com.badlogic.gdx.Input.Keys.SPACE
import com.badlogic.gdx.Input.Keys.W
import com.badlogic.gdx.InputMultiplexer
import com.github.escape_room.poo.component.AttackComponent
import com.github.escape_room.poo.component.MoveComponent
import com.github.escape_room.poo.component.PlayerComponent
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.World
import ktx.app.KtxInputAdapter

class PlayerKeyboardInputProcessor(
    world:World,
    private val moveCmps: ComponentMapper<MoveComponent> = world.mapper(),
    private val attackCmps: ComponentMapper<AttackComponent> = world.mapper(),
) :KtxInputAdapter {

    private var playerSin=0f
    private var playerCos=0f
    private val playerEntities = world.family(allOf =arrayOf(PlayerComponent::class))

    init {
        Gdx.input.inputProcessor = this

    }

    private fun Int.isMovementKey() : Boolean{
        return this == W || this == S || this == A || this == D
    }

    private fun updatePlayerMovement(){
        playerEntities.forEach { player ->
            with(moveCmps[player]) {
                cos=playerCos
                sin=playerSin
            }
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        if(keycode.isMovementKey()) {
            when(keycode) {
                W -> playerSin=1f
                S -> playerSin=-1f
                D -> playerCos=1f
                A -> playerCos=-1f
            }
            updatePlayerMovement()
            return true
        } else if (keycode == SPACE) {
            playerEntities.forEach {
                with(attackCmps[it]){
                    doAttack=true
                }
            }
            return true
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        if(keycode.isMovementKey()) {
            when(keycode) {
                W -> playerSin=if(Gdx.input.isKeyPressed(S))-1f else 0f
                S -> playerSin=if(Gdx.input.isKeyPressed(W))1f else 0f
                D -> playerCos=if(Gdx.input.isKeyPressed(A))-1f else 0f
                A -> playerCos=if(Gdx.input.isKeyPressed(D))1f else 0f
            }
            updatePlayerMovement()
            return true
        }
        return false
    }
}
