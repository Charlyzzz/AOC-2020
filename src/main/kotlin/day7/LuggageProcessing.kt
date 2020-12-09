package day7

import print

val isEmpty = Regex("(.*) bags contain no other bags")
val bagColor = Regex("""(\w+ \w+) bags contain""")
val bagContent = Regex("""(\d+) (\w+ \w+)""")

typealias Color = String

data class Content(val color: Color, val count: Int)

data class Bag(val color: Color, val contains: List<Content>, val containedBy: List<Color>)

private const val SHINY_GOLD = "shiny gold"

fun main() {
    val bagGraph = buildBagGraph(bagRules)
    countBagsThatContainsGoldenBags(bagGraph).print()
    countBagsInsideGoldenBag(bagGraph).print()
}

private fun countBagsThatContainsGoldenBags(bagGraph: Map<Color, Bag>): Int {
    val initialBags = bagGraph.getValue(SHINY_GOLD).containedBy
    return countBagsContainedBy(bagGraph, emptySet(), initialBags).size
}

private tailrec fun countBagsContainedBy(
    bagGraph: Map<String, Bag>,
    colorsChecked: Set<Color>,
    nextColors: List<Color>
): Set<Color> {
    if (nextColors.isEmpty()) return colorsChecked
    val color = nextColors.first()
    val rest = nextColors.drop(1)
    val bag = bagGraph.getValue(color)
    return countBagsContainedBy(bagGraph, colorsChecked + color, rest + bag.containedBy)
}

data class NextBag(val content: Content, val multiplier: Int)

private fun countBagsInsideGoldenBag(bagGraph: Map<Color, Bag>): Int {
    val initialBags = nextBags(bagGraph, SHINY_GOLD, 1)
    return countBagsInside(bagGraph, 0, initialBags)
}

private tailrec fun countBagsInside(bagGraph: Map<Color, Bag>, bagCount: Int, nextBags: List<NextBag>): Int {
    if (nextBags.isEmpty()) return bagCount
    val nextBag = nextBags.first()
    val rest = nextBags.drop(1)
    val (content, multiplier) = nextBag
    val (color, count) = content
    return countBagsInside(
        bagGraph,
        bagCount + count * multiplier,
        rest + nextBags(bagGraph, color, count * multiplier)
    )
}

private fun nextBags(bagGraph: Map<Color, Bag>, color: Color, multiplier: Int): List<NextBag> {
    return bagGraph.getValue(color).contains.map { NextBag(it, multiplier) }
}

private fun buildBagGraph(bagRules: String): Map<Color, Bag> =
    bagRules.lines().fold(emptyMap()) { graph, rule ->
        when {
            isEmpty.matches(rule) -> {
                val color = isEmpty.find(rule)!!.groupValues.first()
                val bag = graph[color] ?: Bag(color, emptyList(), emptyList())
                graph + (color to bag)
            }
            else -> {
                var updatedGraph = graph
                val color = bagColor.find(rule)!!.groupValues[1]
                val content = bagContent.findAll(rule).fold(emptyList<Content>()) { content, match ->
                    val (_, count, insideBagColor) = match.groupValues
                    val insideBag = graph[insideBagColor] ?: Bag(insideBagColor, emptyList(), emptyList())
                    updatedGraph =
                        updatedGraph + (insideBagColor to insideBag.copy(
                            containedBy = insideBag.containedBy + color
                        ))
                    content + Content(insideBagColor, count.toInt())
                }
                val bag = graph[color] ?: Bag(color, emptyList(), emptyList())
                updatedGraph + (color to bag.copy(contains = content))
            }
        }
    }
