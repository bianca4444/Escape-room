package com.github.escape_room.poo.system

import com.badlogic.gdx.scenes.scene2d.Stage
import com.github.escape_room.poo.component.DialogComponent
import com.github.escape_room.poo.component.DialogId
import com.github.escape_room.poo.component.DisarmComponent
import com.github.escape_room.poo.component.MoveComponent
import com.github.escape_room.poo.dialog.Dialog
import com.github.escape_room.poo.dialog.dialog
import com.github.escape_room.poo.event.EntityDialogEvent
import com.github.escape_room.poo.event.fire
import com.github.quillraven.fleks.AllOf
import com.github.quillraven.fleks.ComponentMapper
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.IteratingSystem
import com.github.quillraven.fleks.Qualifier
import ktx.app.gdxError
import java.sql.Blob

// Variabila globală care contorizează câte chei ai
var keyCount = 0

var slimeCount =0

// Funcție care incrementează contorul de chei
fun incrementKeyCount() {
    keyCount++
    println("Ai mai luat o cheie!")
    println(keyCount)
}
//Functie care incrementeaza contorul de slime-uri vizitate
fun incrementSlimeCount() {
    slimeCount++
    println("Ai vorbit cu slime.")
    println(slimeCount)
}

@AllOf([DialogComponent::class])
class DialogSystem(
    private val dialogCmps: ComponentMapper<DialogComponent>,
    private val moveCmps: ComponentMapper<MoveComponent>,
    private val disarmCmps: ComponentMapper<DisarmComponent>,
    @Qualifier("GameStage") private val stage: Stage
) : IteratingSystem() {
    private val dialogCache = mutableMapOf<DialogId, Dialog>()

    override fun onTickEntity(entity: Entity) {
        with(dialogCmps[entity]) {
            val triggerEntity = interactEntity
            var dialog = currentDialog

            if(hasInteracted)
                return

            if (triggerEntity == null) {
                return
            } else if (dialog != null) {
                if (dialog.isComplete()) {
                    moveCmps.getOrNull(triggerEntity)?.let { it.root = false }
                    configureEntity(triggerEntity) { disarmCmps.remove(it) }
                    currentDialog = null
                    interactEntity = null
                }
                return
            }

            dialog = getDialog(dialogId).also { it.start() }
            currentDialog = dialog
            moveCmps.getOrNull(triggerEntity)?.let { it.root = true }
            configureEntity(triggerEntity) { disarmCmps.add(it) }

            stage.fire(EntityDialogEvent(dialog))

            if(dialogId == DialogId.BLOB7) hasInteracted = false
            else hasInteracted = true
        }
    }

    private fun getDialog(id: DialogId): Dialog {
        return dialogCache.getOrPut(id) {
            when (id) {
                DialogId.BLOB -> dialog(id.name) {

                    node(0, "Circular Economy Action Plan is a product policy framework that will make sustainable products, services, and business models the norm and transform consumption patterns so that no waste is produced  The main proposed actions for this aim are the following: Designing sustainable products, Empowering consumers and public buyers, Circularity in production processes") {
                        option("True") {
                            action = {
                                incrementSlimeCount()
                                this@dialog.goToNode(1) }
                        }
                        option("False") {
                            action = {this@dialog.goToNode(11) }
                        }
                    }
                    node(1, "Circular Economy Action Plan is a product policy framework that will make sustainable products, services, and business models the norm and transform consumption patterns so that no waste is produced  The main proposed actions for this aim are the following: Designing sustainable products and unuse the circularity in the production processes") {
                        option("True") {
                            action = {this@dialog.goToNode(11) }
                        }
                        option("False") {
                            action = {
                                this@dialog.goToNode(2) }
                        }
                    }
                    node(2, "Bio-based economy is synonymous with bioeconomy or biotechonomy ") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(3)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(3, "Bio-based economy is synonymous with circular economy") {
                        option("True") {
                            action = {this@dialog.goToNode(11) }
                        }
                        option("False") {
                            action = {
                                this@dialog.goToNode(4) }
                        }
                    }
                    node(4, "The definition of Circular bioeconomy definition is the application of the Circular Economy concept to biological resources, products and materials.") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(5)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(5, "The definition of Circular bioeconomy definition is a part of Circular Economy. ") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(6)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(6, "The definition of Circular bioeconomy definition is more than Bioeconomy and Circular economy alone. ") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(7)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(7, "The definition of Circular bioeconomy definition is intersection of Bioeconomy and Circular economy. ") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(8)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(8, "The definition of Circular bioeconomy definition is circular economy without Bioeconomy . ") {
                        option("True") {
                            action = { this@dialog.goToNode(11) }
                        }
                        option("False") {
                            action = {
                                this@dialog.goToNode(12)
                            } }
                    }
                    node(11, "Wrong answer."){
                        option("Ok"){
                            action = {this@dialog.end() }}
                    }
                    node(12,"Amazing! You earned a key. Go to each one of my other friends to claim the other ones."){
                        option("Great"){
                            action = {
                                incrementKeyCount()
                                this@dialog.end()}
                        }
                    }
                }

                DialogId.BLOB2 -> dialog(id.name) {

                    node(0, "The European Green Deal (EDG) is a package of policy initiatives, which aims to set the EU on the path to a green transition, with the ultimate goal of reaching climate neutrality by 2050. The primary goals under the Deal are net zero emissions of greenhouse gasses by 2050; economic growth is decoupled from the exploitation of resources; no one being left behind. In this aim, the  main policies set in 2021 by the EU Commission  included in the European Green Deal are in connection to:") {
                        option("See choices") {
                            action = {
                                incrementSlimeCount()
                                this@dialog.goToNode(1) }
                        }
                    }
                    node(1, "Increase the EU’s climate ambition for 2030 and 2050 ") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(2)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(2, "Supplying clean, affordable, and secure energy ") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(3)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(3, "Creating a clean and circular economy") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(4)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(4, "Efficient building and renovating ") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(5)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(5, "Accelerating the shift to sustainable and smart mobility ") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(6)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(6, "“From farm to fork” ") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(7)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(7, "Ecosystem and biodiversity preservation and restoration") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(8)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(8, "A zero pollution ambition for a toxic-free environment ") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(9)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(9, "No circular economy only industrial growth ") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(11)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(12) } }
                    }
                    node(11, "Wrong answer."){
                        option("Ok"){
                            action = {this@dialog.end() }}
                    }
                    node(12,"Amazing! You earned a key. Go to each one of my other friends to claim the other ones."){
                        option("Great"){
                            action = {
                                incrementKeyCount()
                                this@dialog.end()}
                        }
                    }
                }

                DialogId.BLOB3 -> dialog(id.name) {

                    node(0, "What are the EU Commission main target regarding green / sustainable agriculture?") {
                        option("See choices") {
                            action = {
                                incrementSlimeCount()
                                this@dialog.goToNode(1) }
                        }
                    }
                    node(1, "The main EU Commission target regarding green / sustainable agriculture is to ensure food security in conditions taking into account the geopolitical uncertainties, climate change and biodiversity loss. ") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(2)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(2, "The main EU Commission target regarding green / sustainable agriculture is to increase the food production to assure all the need of EU population function of adequate eating requirements. ") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(11)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(3) } }
                    }
                    node(2, "The main EU Commission target regarding green / sustainable agriculture is to assure all the needed food requirements for the EU population by diminishing the import and increasing the healthy food production. ") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(11)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(3) } }
                    }
                    node(3, "The main EU Commission target regarding green / sustainable agriculture is to develop and diversify the intensive agriculture procedures.") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(11)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(4) } }
                    }
                    node(4, "What is the definition of green / sustainable agriculture?") {
                        option("See choices") {
                            action = {
                                this@dialog.goToNode(5) }
                        }
                    }
                    node(5, "The green / sustainable agriculture is the type of agricultural activity to supply society's food and textile needs in the present without compromising the ability of future generations to meet their own needs.") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(6)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(6, "The green / sustainable agriculture is the farming activity that uses a lot of machinery, labor, chemicals, etc. in order to grow as many crops or keep as many animals as possible on the amount of land available.") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(11)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(7) } }
                    }
                    node(7, "The green / sustainable agriculture comprises the methods of producing large amounts of crops, by using chemicals and machines.") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(11)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(8) } }
                    }
                    node(8, "The green / sustainable agriculture uses farming techniques that respect the environment, biodiversity and the earth’s natural waste absorption capacity.") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(12)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(11, "Wrong answer."){
                        option("Ok"){
                            action = {this@dialog.end() }}
                    }
                    node(12,"Amazing! You earned a key. Go to each one of my other friends to claim the other ones."){
                        option("Great"){
                            action = {
                                incrementKeyCount()
                                this@dialog.end()}
                        }
                    }
                }

                DialogId.BLOB4 -> dialog(id.name) {

                    node(0, "Why the EU’s Common Agriculture Policy (CAP) is central to the EU Green Deal & its Farm to Fork and Biodiversity strategies?") {
                        option("See choices") {
                            action = {
                                incrementSlimeCount()
                                this@dialog.goToNode(1) }
                        }
                    }
                    node(1, "The EU’s Common Agriculture Policy (CAP) is central to the EU Green Deal & its Farm to Fork and Biodiversity strategies as it is built around a main goal, very important for these policies and strategies: maximum productivity of crops or domestic animal growth on the amount of available land.") {
                        option("True") {
                            action = { this@dialog.goToNode(11) }
                        }
                        option("False"){
                            action = {this@dialog.goToNode(2)}
                        }
                    }
                    node(2, "The EU’s Common Agriculture Policy (CAP) is central to the EU Green Deal & its Farm to Fork and Biodiversity strategies as it built around a most important principle for these policies and strategies: higher use of inputs such as capital, labor, agrochemicals and water, to optimize crop yields or domestic animal growth.") {
                        option("True") {
                            action = { this@dialog.goToNode(11) }
                        }
                        option("False"){
                            action = {this@dialog.goToNode(3)}
                        }
                    }
                    node(3, "The EU’s Common Agriculture Policy (CAP) is central to the EU Green Deal & its Farm to Fork and Biodiversity strategies as is built around three main goals, highly important to these policy initiatives and strategies, to achieve a sustainable system of agriculture in the EU: economic sustainability, environmental sustainability, and. the social sustainability of farms.") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(4)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(4, "The EU’s Common Agriculture Policy (CAP) is central to the EU Green Deal & its Farm to Fork and Biodiversity strategies as it is a global standard of agriculture products in terms of safety, security of supply, nutrition and quality, the environmental sustainability being subsequent.") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(11)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(5) } }
                    }
                    node(5, "What are the urgent objectives of green / sustainable agriculture?") {
                        option("See choices") {
                            action = {
                                this@dialog.goToNode(6) }
                        }
                    }
                    node(6, "The urgent objectives of green / sustainable agriculture are to increase productivity and to secure products’ fair prices for food security and poverty alleviation by implementing modern farming techniques, using improved seeds, irrigation, and implementing better management solutions for farm animals’ growth.") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(11)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(7) } }
                    }
                    node(7, "The urgent objectives of green / sustainable agriculture are to reduce dependency on pesticides and antimicrobials, reduce excess fertilization, increase organic farming, improve animal welfare, and reverse biodiversity loss.") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(8)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(8, "The urgent objectives of green / sustainable agriculture are market access and value chain development in order to help farmers to get fair prices for their produce and enhance overall agricultural value-added.") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(11)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(9) } }
                    }
                    node(9, "The urgent objectives of green / sustainable agriculture are higher yields, better quality of products, better crops’ adaptability, and higher capacity to feed concentrated farm animals."){
                        option("True") {
                            action = {
                                this@dialog.goToNode(11)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(12) } }
                    }
                    node(11, "Wrong answer."){
                        option("Ok"){
                            action = {this@dialog.end() }}
                    }
                    node(12,"Amazing! You earned a key. Go to each one of my other friends to claim the other ones."){
                        option("Great"){
                            action = {
                                incrementKeyCount()
                                this@dialog.end()}
                        }
                    }
                }

                DialogId.BLOB5 -> dialog(id.name) {

                    node(0, "Which are the characteristics of organic farming?") {
                        option("See choices") {
                            action = {
                                incrementSlimeCount()
                                this@dialog.goToNode(1) }
                        }
                    }
                    node(1, "Organic farming uses large amount of labor and investment to increase the crops’ yield, uses pesticides, fertilizers, and other chemicals, and also specific chemicals to assure the farm animals health.") {
                        option("True") {
                            action = { this@dialog.goToNode(11) }
                        }
                        option("False") {
                            action = {this@dialog.goToNode(2) }
                        }
                    }
                    node(2, "Organic farming uses large capital investment in equipment and technology, large-scale farms, monocultures, high-yield hybrid crops, extensive use of chemical fertilizers and pesticides, and for livestock-systems with high concentration and confinement of animals. ") {
                        option("True") {
                            action = { this@dialog.goToNode(11) }
                        }
                        option("False") {
                            action = {this@dialog.goToNode(3) }
                        }
                    }
                    node(3, "Organic farming applies all the technological improvements for the productivity increase, both for crops or livestock, with no important interests for products’ nutritional quality or environment sustainability.") {
                        option("True") {
                            action = { this@dialog.goToNode(11) }
                        }
                        option("False") {
                            action = {this@dialog.goToNode(4) }
                        }
                    }
                    node(4, "Organic farming does not use synthetic chemicals such as fertilizers, herbicides, insecticides, pesticides, but only uses natural / bio fertilizers, applies natural-derived biocides, reduces soil erosion, decreases nitrate leaching, uses traditional crop rotation, and recycles animal wastes back into the farm.") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(5)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(5, "6.\tWhat is the target of European Green Deal regarding the Organic farming in connection with the EU Organic farming Action Plan?") {
                        option("See choices") {
                            action = {
                                this@dialog.goToNode(6) }
                        }
                    }
                    node(6, "The target of the EU Green Deal regarding the Organic farming is to reach 50% of agricultural land under organic farming by 2030.") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(11)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(7) } }
                    }
                    node(7, "The target of the EU Green Deal regarding the Organic farming is to reach 25% of agricultural land under organic farming by 2030.") {
                        option("True") {
                            action = {
                                this@dialog.goToNode(8)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(8, "The target of the EU Green Deal regarding the Organic farming is to replace all the chemical fertilizers by bio fertilizers until 2035."){
                        option("True") {
                            action = {
                                this@dialog.goToNode(11)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(9) } }
                    }
                    node(9, "The target of the EU Green Deal regarding the Organic farming is to entirely assure EU population’ food quality and security until 2030."){
                        option("True") {
                            action = {
                                this@dialog.goToNode(11)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(10) } }
                    }
                    node(10, "7.\tIn which main domain of activity, the EU Commission intend to assure important budgetary allowance with the aim to achieve the EU Organic farming Action Plan’ objectives?"){
                        option("See choices") {
                            action = {
                                this@dialog.goToNode(13) }
                        }
                    }
                    node(13, "For achieving the Organic farming Action Plan’ objectives the EU Commission intend to assure important budgetary allowance for research and innovation, at least 30% of the budget for research and innovation to actions in the fields of agriculture, forestry and rural areas comprising topics specific to or relevant for the organic sector."){
                        option("True") {
                            action = {
                                this@dialog.goToNode(14)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(11) } }
                    }
                    node(14, "For achieving the Organic farming Action Plan’ objectives the EU Commission intend to assure important budgetary allowance for farms modern management implementation."){
                        option("True") {
                            action = {
                                this@dialog.goToNode(11)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(15) } }
                    }
                    node(15, "For achieving the Organic farming Action Plan’ objectives the EU Commission intend to assure important budgetary allowance for introduction of new systems of concentrated animals feeding and use of more efficient agrochemicals in land exploitation."){
                        option("True") {
                            action = {
                                this@dialog.goToNode(11)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(16) } }
                    }
                    node(16, "For achieving the Organic farming Action Plan’ objectives the EU Commission intend to assure important budgetary allowance for implementation of optimal models to apply agrochemicals and mechanization in land cultivation and to realize intensive farm animals’ growth."){
                        option("True") {
                            action = {
                                this@dialog.goToNode(11)
                            }
                        }
                        option("False") { action = { this@dialog.goToNode(12) } }
                    }
                    node(11, "Wrong answer."){
                        option("Ok"){
                            action = {this@dialog.end() }}
                    }
                    node(12,"Amazing! You earned a key. Go to each one of my other friends to claim the other ones."){
                        option("Great"){
                            action = {
                                incrementKeyCount()
                                this@dialog.end()}
                        }
                    }
                }
                DialogId.BLOB6 -> dialog(id.name) {

                    node(0, "Circular economy represents a model of production and consumption that involves sharing, leasing, reusing, repairing, refurbishing, and recycling existing materials and products as long as possible. In this way, the life cycle of products is extended. "){
                        option("True"){
                            action = {
                                incrementSlimeCount()
                                this@dialog.goToNode(1) }
                        }
                        option("False"){
                            action = {this@dialog.goToNode(11) }
                        }
                    }
                    node(1, "Circular economy represents an industrial economy that is restorative or regenerative by value and design. ") {
                        option("True"){
                            action = {this@dialog.goToNode(2) }
                        }
                        option("False"){
                            action = {this@dialog.goToNode(11) }
                        }

                    }
                    node(2, "Circular economy represents an economic system that targets zero waste and pollution throughout materials lifecycles, from environment extraction to industrial transformation, and final consumers, applying to all involved ecosystems. Upon its lifetime end, materials return to either an industrial process or, in the case of a treated organic residual, safely back to the environment as in a natural regenerating cycle. "){
                        option("True"){
                            action = {this@dialog.goToNode(3) }
                        }
                        option("False"){
                            action = {this@dialog.goToNode(11) }
                        }
                    }
                    node(3, "Circular economy represents an economic system in which the natural resources are turned into products that are ultimately destined to become waste. "){
                        option("True"){
                            action = {this@dialog.goToNode(11) }
                        }
                        option("False"){
                            action = {this@dialog.goToNode(4) }
                        }
                    }
                    node(4, "The circular economy has the potential to contribute significantly to the following sustainable development goals (SDGs) set by the United Nations (UN) only for the following SDGs:  Sustainable cities and communities  (SDG 11)  and Responsible consumption and production (SDG  12)  "){
                        option("True"){
                            action = {this@dialog.goToNode(11) }
                        }
                        option("False"){
                            action = {this@dialog.goToNode(5) }
                        }
                    }
                    node(5, "The circular economy has the potential to contribute significantly to the following sustainable development goals (SDGs) set by the United Nations (UN) only for the following SDGs:  Decent work and economic growth (SDG 8) and Industry, innovation, and infrastructure (SDG9) "){
                        option("True"){
                            action = {this@dialog.goToNode(11) }
                        }
                        option("False"){
                            action = {this@dialog.goToNode(6) }
                        }
                    }
                    node(6,"The circular economy has the potential to contribute significantly to the following sustainable development goals (SDGs) set by the United Nations (UN) only for three SDGs: Affordable and clean energy (SDG 7), Sustainable cities and communities  (SDG 11), and Climate action (SDG 13) "){
                        option("True"){
                            action = {this@dialog.goToNode(11) }
                        }
                        option("False"){
                            action = {this@dialog.goToNode(7) }
                        }
                    }
                    node(7,"The circular economy has the potential to contribute significantly to the following sustainable development goals (SDGs) set by the United Nations (UN) for the following SDGs without  limiting: Affordable and clean energy (SDG 7), Decent work and economic growth (SDG 8), Industry, innovation and infrastructure (SDG9), Sustainable cities and communities  (SDG11), Responsible consumption and production (SDG 12) and Climate action (SDG 13) "){
                        option("True"){
                            action = {this@dialog.goToNode(8) }
                        }
                        option("False"){
                            action = {this@dialog.goToNode(11) }
                        }
                    }
                    node(8,"The benefits of switching to a circular economy are in the following fields: To protect the environment"){
                        option("True"){
                            action = {this@dialog.goToNode(9) }
                        }
                        option("False"){
                            action = {this@dialog.goToNode(11) }
                        }
                    }
                    node(9, "The benefits of switching to a circular economy are in the following fields:Reduce raw material dependence "){
                        option("True"){
                            action = {this@dialog.goToNode(10) }
                        }
                        option("False"){
                            action = {this@dialog.goToNode(11) }
                        }
                    }
                    node(10,"The benefits of switching to a circular economy are in the following fields:Create jobs and save consumers money "){
                        option("True"){
                            action = {this@dialog.goToNode(12) }
                        }
                        option("False"){
                            action = {this@dialog.goToNode(11) }
                        }
                    }
                    node(11, "Wrong answer."){
                        option("Ok"){
                        action = {this@dialog.end() }}
                    }
                    node(12,"Amazing! You earned a key. Go to each one of my other friends to claim the other ones."){
                        option("Great"){
                            action = {
                                incrementKeyCount()
                                this@dialog.end()}
                        }
                    }
                }

                DialogId.BLOB7 -> dialog(id.name) {
                    if(slimeCount < 6)
                    {
                        node(0, "Go back and search some more") {
                            option("Ok"){
                                action = {
                                    dialogCache.clear()
                                    this@dialog.end()}
                            }
                        }
                    } else if (keyCount >= 3){
                        node(0, "You win!") {
                            option("Great!"){
                                action = {this@dialog.end()}
                            }
                        }
                    }else{
                        node(0, "Game lost!") {
                            option("Try again"){
                                action = {this@dialog.end()}
                            }
                        }
                    }

                }

                else -> gdxError("No dialog configured for $id.")
            }
        }
    }
}
