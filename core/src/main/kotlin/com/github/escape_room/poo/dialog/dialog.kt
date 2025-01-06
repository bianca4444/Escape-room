package com.github.escape_room.poo.dialog

import ktx.app.gdxError

fun dialog(id: String,cfg:Dialog.() -> Unit): Dialog {
    return Dialog(id).apply(cfg)
}

@DslMarker
annotation class DialogDslMarker

@DialogDslMarker
data class Dialog(
    val id:String,
    private val nodes:MutableList<Node> = mutableListOf(),
    private var complete:Boolean = false
){
    lateinit var currentNode:Node

    fun isComplete(): Boolean =complete

    fun node(id:Int,text:String,cfg:Node.() -> Unit): Node {
        return Node(id,text).apply {
            this.cfg()
            this@Dialog.nodes += this
        }
    }

    fun goToNode(nodeId:Int){
        currentNode = nodes.firstOrNull { it.id == nodeId }
            ?: gdxError("There is no node with id $nodeId in dialog $this")

    }

    fun start(){
        complete=false
        currentNode=nodes.first()
    }

    fun end(){
        complete=true
    }

    fun triggerOption(optionId:Int){
        val option= currentNode[optionId] ?: gdxError("There is no node with id $optionId in dialog $currentNode")
        option.action()
    }

}


@DialogDslMarker
data class Node(val id:Int,val text:String)
{
    var options: MutableList<Option> = mutableListOf()
        private set

    fun hasNoOptions(): Boolean = options.isEmpty()

    fun option(text: String, cfg: Option.() -> Unit) {
        options += Option(options.size, text).apply {
            this.cfg()
        }
    }

    operator fun get(optionIdx: Int):Option? = options.getOrNull(optionIdx)
}
@DialogDslMarker
data class Option(
    val id:Int,
    val text:String,
    var action:()->Unit={}
)
