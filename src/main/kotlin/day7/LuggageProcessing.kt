package day7

import java.util.*

val isEmpty = Regex("(.*) bags contain no other bags")
val bagColor = Regex("""(\w+ \w+) bags contain""")
val bagContent = Regex("""(\d+) (\w+ \w+)""")

typealias Color = String

private const val SHINY_GOLD = "shiny gold"

fun main() {
    val (contentMap, reverseContentMap) = buildBagMap(bagRules)
    countBagsThatContainsGoldenBag(reverseContentMap).also { println(it) }
    countBagsInsideGoldenBag(contentMap).also { println(it) }
}

fun countBagsInsideGoldenBag(contentMap: Map<Color, List<Pair<Color, Int>>>): Int =
    countBagsInside(contentMap, SHINY_GOLD)

fun countBagsInside(contentMap: Map<Color, List<Pair<Color, Int>>>, color: Color): Int {
    val content = contentMap.getOrDefault(color, emptyList())
    if (content.isEmpty()) {
        return 0
    }
    return content.sumBy { (newColor, count) ->
        count + count * countBagsInside(contentMap, newColor)
    }
}

fun countBagsThatContainsGoldenBag(reverseContentMap: Map<Color, Set<Color>>): Int {
    var bagsVisited = emptySet<Color>()
    val initialBags = reverseContentMap[SHINY_GOLD] ?: error("color not found")
    val bagsToCheck = emptyQueue(initialBags)
    while (bagsToCheck.isNotEmpty()) {
        bagsToCheck.poll().also {
            bagsVisited = bagsVisited + it
            val colors = reverseContentMap.getOrDefault(it, emptySet())
            colors.forEach { bagToCheck ->
                bagsToCheck.add(bagToCheck)
            }
        }
    }
    return bagsVisited.size
}


data class BagMap(val contentMap: Map<Color, List<Pair<Color, Int>>>, val reverseContentMap: Map<Color, Set<Color>>)

fun buildBagMap(bagRules: String): BagMap =
    bagRules.lines().fold(BagMap(emptyMap(), emptyMap())) { bagMap, rule ->
        when {
            isEmpty.matches(rule) -> {
                val color = isEmpty.find(rule)!!.groupValues.first()
                bagMap.copy(
                    contentMap = bagMap.contentMap + (color to emptyList()),
                    reverseContentMap = bagMap.reverseContentMap + (color to emptySet())
                )
            }
            else -> {
                val bagColor = bagColor.find(rule)!!.groupValues[1]
                bagContent.findAll(rule).fold(bagMap) { bm, match ->
                    val (_, count, bagInside) = match.groupValues
                    val bagContent = bm.contentMap.getOrDefault(bagColor, emptyList()) + (bagInside to count.toInt())

                    val existingBags = bm.reverseContentMap.getOrDefault(bagInside, emptySet())

                    bm.copy(
                        contentMap = bm.contentMap + (bagColor to bagContent),
                        reverseContentMap = bm.reverseContentMap + (bagInside to (existingBags + bagColor))
                    )
                }
            }
        }
    }

fun <T> emptyQueue(elems: Collection<T>? = null): Queue<T> =
    if (elems.isNullOrEmpty()) {
        LinkedList()
    } else {
        LinkedList(elems)
    }