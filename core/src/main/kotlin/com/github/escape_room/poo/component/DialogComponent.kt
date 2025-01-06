package com.github.escape_room.poo.component

import com.github.escape_room.poo.dialog.Dialog
import com.github.quillraven.fleks.Entity

enum class DialogId{
    NONE,
    Q,
}

data class DialogComponent(
    var dialogId:DialogId=DialogId.NONE,
) {
    var interactEntity: Entity? = null
    var currentDialog:Dialog?=null
}

