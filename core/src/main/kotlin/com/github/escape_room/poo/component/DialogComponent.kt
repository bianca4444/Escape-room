package com.github.escape_room.poo.component

import com.github.escape_room.poo.dialog.Dialog
import com.github.quillraven.fleks.Entity

enum class DialogId {
    NONE,
    BLOB,
    BLOB2,
    BLOB3,
    BLOB4,
    BLOB5,
    BLOB6,
    BLOB7
}

data class DialogComponent(
    var dialogId: DialogId = DialogId.NONE,
) {
    var interactEntity: Entity? = null
    var currentDialog: Dialog? = null
    var hasInteracted: Boolean = false // Flag pentru a semnala dacă dialogul a fost accesat
}

