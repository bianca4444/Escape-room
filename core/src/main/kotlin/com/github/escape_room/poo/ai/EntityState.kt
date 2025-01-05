package com.github.escape_room.poo.ai

import com.badlogic.gdx.ai.fsm.State
import com.badlogic.gdx.ai.msg.Telegram
import com.github.quillraven.fleks.Entity

interface EntityState: State<AiEntity>{
    override fun enter(entity: AiEntity)= Unit

    override fun exit(entity: AiEntity) = Unit

    override fun update(entity: AiEntity) = Unit

    override fun onMessage(entity: AiEntity, telegram: Telegram) =false

}
