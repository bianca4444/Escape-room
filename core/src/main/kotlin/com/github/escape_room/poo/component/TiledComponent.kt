package com.github.escape_room.poo.component

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.github.quillraven.fleks.Entity

class TiledComponent {
    lateinit var cell: TiledMapTileLayer.Cell
    val nearbyEntities = mutableSetOf<Entity>()
}
