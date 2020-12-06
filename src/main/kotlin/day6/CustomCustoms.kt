package day6

fun main() {
    val groups = parseGroupInputs(groupAnswersInput)
    countGroupAnswers(groups).also { println(it) }
    countGroupSharedAnswers(groups).also { println(it) }
}

fun countGroupSharedAnswers(groups: List<Group>): Int =
    groups.fold(0) { count, group ->
        count + group.sharedAnswersCount()
    }

private fun countGroupAnswers(groups: List<Group>): Int =
    groups.fold(0) { count, group ->
        count + group.answers.size
    }

fun parseGroupInputs(rawInputs: String): List<Group> =
    rawInputs
        .split("\n\n")
        .map { Group.fromAnswers(it.split("\n")) }

data class Group(val size: Int, val answers: Map<Char, Int>) {
    fun sharedAnswersCount(): Int = answers.count { it.value == size }

    companion object {
        fun fromAnswers(answers: List<String>): Group {
            val groupedAnswers = answers.fold(mutableMapOf<Char, Int>()) { questions, personAnswers ->
                questions.also {
                    personAnswers.forEach { questions[it] = questions.getOrDefault(it, 0) + 1 }
                }
            }
            return Group(answers.size, groupedAnswers)
        }
    }
}

