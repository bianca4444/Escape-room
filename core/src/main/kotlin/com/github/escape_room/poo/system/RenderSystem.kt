package com.github.escape_room.poo.system

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.escape_room.poo.component.imageComponent
import com.github.quillraven.fleks.*
import com.github.quillraven.fleks.collection.compareEntity
import ktx.graphics.use
import ktx.tiled.forEachLayer
import com.github.escape_room.poo.event.MapChangeEvent
import com.badlogic.gdx.scenes.scene2d.Event
import ktx.assets.disposeSafely
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile


@AllOf([imageComponent::class])


class RenderSystem(
    private val stage: Stage,
    private val imageCmps: ComponentMapper<imageComponent>
) : EventListener, IteratingSystem(
    comparator = compareEntity {e1,e2 ->imageCmps[e1].compareTo(imageCmps[e2])}
) {

    private val bgdLayers = mutableListOf<TiledMapTileLayer>()
    private val fgdLayers = mutableListOf<TiledMapTileLayer>()
    private val mapRender = OrthogonalTiledMapRenderer(null,1/16f,stage.batch)
    private val orthoCam = stage.camera as OrthographicCamera
    override fun onTick() {
        super.onTick()

        with(stage) {
            viewport.apply()

            AnimatedTiledMapTile.updateAnimationBaseTime()
            mapRender.setView(orthoCam)


            if(bgdLayers.isNotEmpty()) {
                stage.batch.use(orthoCam.combined) {
                    bgdLayers.forEach { mapRender.renderTileLayer(it)}
                }
            }

            act(deltaTime)
            draw()

            if(fgdLayers.isNotEmpty()) {
                stage.batch.use(orthoCam.combined) {
                    fgdLayers.forEach { mapRender.renderTileLayer(it)}
                }
            }
        }
    }

    override fun onTickEntity(entity: Entity) {
        imageCmps[entity].image.toFront()
    }

    override fun handle(event: Event?): Boolean {
        if (event is MapChangeEvent) {
            bgdLayers.clear()
            fgdLayers.clear()
            event.map.forEachLayer<TiledMapTileLayer> { layer ->
                if (layer.name.startsWith("fgd_")) {
                    fgdLayers.add(layer)
                } else {
                    bgdLayers.add(layer)
                }
            }
            return true
        }
        return false
    }
        override fun onDispose(){
            mapRender.disposeSafely()
        }
}


