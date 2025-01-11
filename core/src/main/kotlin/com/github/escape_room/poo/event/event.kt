package com.github.escape_room.poo.event

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.Stage

import com.github.escape_room.poo.dialog.Dialog


fun Stage.fire(event: Event) = this.root.fire(event)

data class MapChangeEvent(val map: TiledMap) : Event()

data class CollisionDespawnEvent(val cell: Cell) : Event()

data class EntityAttackEvent(val atlasKey: String) : Event()

data class EntityDeathEvent(val atlasKey: String) : Event()

class EntityLootEvent : Event()

data class EntityDialogEvent(val dialog: Dialog) : Event()



class GamePauseEvent : Event()

class GameResumeEvent : Event()
