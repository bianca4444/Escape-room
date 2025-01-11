package com.github.escape_room.poo.component

import com.github.quillraven.fleks.Entity

data class PortalComponent(
    var id: Int = -1,
    var toMap: String = "",
    var toPortal: Int = 0,
    var triggerEntities: MutableSet<Entity> = mutableSetOf(),
)
