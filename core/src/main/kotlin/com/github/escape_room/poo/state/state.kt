package com.github.escape_room.poo.state

import com.badlogic.gdx.graphics.g2d.Animation
import com.github.escape_room.poo.component.AnimationType
import com.github.escape_room.poo.component.EntityState
import com.github.escape_room.poo.component.StateEntity


enum class DefaultState : EntityState {
    IDLE {
        override fun enter(stateEntity: StateEntity) {
            stateEntity.animation(AnimationType.IDLE)
        }

        override fun update(stateEntity: StateEntity) {
            when {
                stateEntity.wantsToAttack -> stateEntity.state(ATTACK)
                stateEntity.wantsToMove -> stateEntity.state(RUN)
            }
        }
    },
    RUN {
        override fun enter(stateEntity: StateEntity) {
            stateEntity.animation(AnimationType.RUN)
        }

        override fun update(stateEntity: StateEntity) {
            when {
                stateEntity.wantsToAttack -> stateEntity.state(ATTACK)
                !stateEntity.wantsToMove -> stateEntity.state(IDLE)
            }
        }
    },
    ATTACK {
        override fun enter(stateEntity: StateEntity) {
            with(stateEntity) {
                animation(AnimationType.ATTACK, Animation.PlayMode.NORMAL)
                moveCmp.root = true
                startAttack()
            }
        }

        override fun exit(stateEntity: StateEntity) {
            stateEntity.moveCmp.root = false
        }

        override fun update(stateEntity: StateEntity) {
            val attackCmp = stateEntity.attackCmp
            if (attackCmp.isReady() && !attackCmp.doAttack) {
                // done attacking
                stateEntity.changeToPreviousState()
            } else if (attackCmp.isReady()) {
                // start another attack
                stateEntity.resetAnimation()
                attackCmp.startAttack()
            }
        }
    },


}
