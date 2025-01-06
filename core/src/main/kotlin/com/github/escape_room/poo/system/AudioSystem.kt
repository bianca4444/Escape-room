package com.github.escape_room.poo.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.github.escape_room.poo.event.EntityAttackEvent
import com.github.escape_room.poo.event.EntityLootEvent
import com.github.escape_room.poo.event.MapChangeEvent
import com.github.quillraven.fleks.IntervalSystem
import ktx.assets.disposeSafely
import ktx.log.logger
import ktx.tiled.propertyOrNull

class AudioSystem : EventListener, IntervalSystem() {

    private val musicCache=mutableMapOf<String, Music>()
    private val soundCache=mutableMapOf<String, Sound>()
    private val soundRequests=mutableMapOf<String,Sound>()

    override fun onTick() {
        if(soundRequests.isEmpty()){
            return
        }
        soundRequests.values.forEach{(it.play(0.3f))}
        soundRequests.clear()

    }

    override fun handle(event: Event): Boolean {
        when(event){
            is MapChangeEvent->{
                event.map.propertyOrNull<String>("music")?.let{path->
                    log.debug { "Changing music to $path" }
                    val music=musicCache.getOrPut(path){
                        Gdx.audio.newMusic(Gdx.files.internal(path)).apply {
                            isLooping = true
                        }
                    }
                    music.play()
                }
                return true
            }
            is EntityAttackEvent-> queueSound("audio/${event.model.atlasKey}_attack.wav")
            is EntityLootEvent-> queueSound("audio/${event.model.atlasKey}_open.wav")
        }
        return false
    }

    private fun queueSound(soundPath : String){
        log.debug { "Queuing sound $soundPath" }
        if(soundPath in soundRequests){
            return
        }
        val sound=soundCache.getOrPut(soundPath){
            Gdx.audio.newSound(Gdx.files.internal(soundPath))
        }
        soundRequests[soundPath]=sound

    }

    override fun onDispose() {
        musicCache.values.forEach { it.disposeSafely() }
        soundCache.values.forEach { it.disposeSafely() }
    }

    companion object{
        private val log=logger<AudioSystem>()
    }
}
