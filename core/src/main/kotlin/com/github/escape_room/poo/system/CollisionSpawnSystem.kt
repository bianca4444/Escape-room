package com.github.escape_room.poo.system

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.github.escape_room.poo.component.CollisionComponent
import com.github.escape_room.poo.component.PhsysicComponent
import com.github.escape_room.poo.component.PhsysicComponent.Companion.physicCmpFromShape2D
import com.github.escape_room.poo.component.TiledComponent
import com.github.escape_room.poo.event.CollisionDespawnEvent
import com.github.escape_room.poo.event.MapChangeEvent
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import ktx.collections.GdxArray
import ktx.tiled.forEachLayer
import ktx.tiled.height
import ktx.tiled.isEmpty
import ktx.tiled.isNotEmpty
import ktx.tiled.shape
import ktx.tiled.width
import kotlin.collections.forEach
import kotlin.math.max
import ktx.math.component1
import ktx.math.component2

@AllOf([PhsysicComponent::class, CollisionComponent::class])

class CollisionSpawnSystem(
    private val phWorld : World,
    private val physicCmps : ComponentMapper<PhsysicComponent>
) : EventListener, IteratingSystem() {
    private val tiledLayers = GdxArray<TiledMapTileLayer>()
    private val processedCells = mutableSetOf<Cell>()

    private fun TiledMapTileLayer.forEachCell(
        startX:Int,
        startY:Int,
        size:Int,
        action: (Cell, Int, Int) -> Unit
    ) {
        for(x in startX-size..startX+size){
            for(y in startY-size.. startY+size){
                this.getCell(x,y)?.let {action(it,x,y)}


            }
        }
    }

    override fun onTickEntity(entity: Entity) {
        val(entityX,entityY) = physicCmps[entity].body.position
        tiledLayers.forEach { layer ->
            layer.forEachCell(entityX.toInt(),entityY.toInt(), SPAWN_AREA_SIZE) {cell,x,y ->
                if(cell.tile.objects.isEmpty()){
                    return@forEachCell
                }

                if(cell in processedCells){
                    return@forEachCell
                }

                processedCells.add(cell)
                cell.tile.objects.forEach {mapObject ->
                    world.entity{
                        physicCmpFromShape2D(phWorld,x,y,mapObject.shape)
                        add<TiledComponent>{
                            this.cell=cell
                            nearbyEntities.add(entity)

                        }
                    }

                }
            }

        }
    }

    override fun handle(event: Event): Boolean {
        when(event) {
            is MapChangeEvent ->{
                event.map.layers.getByType(TiledMapTileLayer::class.java, tiledLayers)

                return true
            }
            is CollisionDespawnEvent -> {
                processedCells.remove(event.cell)
                return true
            }
            else -> return false
        }
    }
    companion object {
        const val SPAWN_AREA_SIZE=3
    }
}
