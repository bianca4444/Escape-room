package com.github.escape_room.poo.ai

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.github.escape_room.poo.component.AnimationType

enum class DefaultState : EntityState {
    IDLE{
        override fun enter(entity: AiEntity){
            entity.animation(AnimationType.IDLE)
        }

        override fun update(entity: AiEntity){
            when{
                entity.wantsToAttack ->entity.state(ATTACK)
                entity.wantsToRun -> entity.state(RUN)
            }
        }
    },
    RUN{
        override fun enter(entity: AiEntity){
            entity.animation(AnimationType.RUN)
        }

        override fun update(entity: AiEntity){
            when {
                entity.wantsToAttack -> entity.state(ATTACK)
                !entity.wantsToRun -> entity.state(IDLE)
            }
        }
    },
    ATTACK{
        override fun enter(entity: AiEntity){
            entity.animation(AnimationType.ATTACK, PlayMode.NORMAL)
            entity.root(true)
            entity.startAttack()
        }

        override fun exit(entity: AiEntity){
            entity.root(false)
        }

        override fun update(entity: AiEntity){
            val attackCmp = entity.attackCmp
            if(attackCmp.isReady && !attackCmp.doAttack){
                entity.changeToPrev()
            } else if(attackCmp.isReady){
                entity.animation(AnimationType.ATTACK, PlayMode.NORMAL,true)
                entity.startAttack()

            }
        }
    },
}

