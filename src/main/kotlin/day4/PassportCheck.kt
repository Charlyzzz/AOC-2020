package day4

import print
import java.io.File

data class Passport(val fields: Map<String, String>) {

    fun hasRequiredFields() = requiredFields.all { fields.contains(it) }

    fun isValid(): Boolean {
        return hasRequiredFields() && validByr() && validIyr() && validEyr() && validHgt() && validHcl() && validEcl() && validPid()
    }

    private fun validByr(): Boolean {
        val byr by fields
        return byr.length == 4 && IntRange(1920, 2002).contains(byr.toInt())
    }

    private fun validIyr(): Boolean {
        val iyr by fields
        return iyr.length == 4 && IntRange(2010, 2020).contains(iyr.toInt())
    }

    private fun validEyr(): Boolean {
        val eyr by fields
        return eyr.length == 4 && IntRange(2020, 2030).contains(eyr.toInt())
    }

    private fun validHgt(): Boolean {
        val hgt by fields
        return when {
            hgt.endsWith("cm") -> {
                val height = hgt.dropLast(2).toInt()
                IntRange(150, 193).contains(height)
            }
            hgt.endsWith("in") -> {
                val height = hgt.dropLast(2).toInt()
                IntRange(59, 76).contains(height)
            }
            else -> false
        }
    }

    private fun validHcl(): Boolean {
        val hcl by fields
        return Regex("#[0-9a-f]{6}").matches(hcl)
    }

    private fun validEcl(): Boolean {
        val ecl by fields
        return validEyeColors.contains(ecl)
    }

    private fun validPid(): Boolean {
        val pid by fields
        return Regex("""\d{9}""").matches(pid)
    }

    companion object {
        val requiredFields = setOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")

        val validEyeColors = setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")

        fun empty(): Passport = Passport(emptyMap())

        fun merge(passport: Passport, rawFields: String): Passport {
            val fields = rawFields.trim().split(" ").map {
                val (key, value) = it.split(":")
                key to value
            }.toMap()
            return Passport(passport.fields + fields)
        }
    }
}

class PassportBuilder {

    private val passports: MutableList<Passport> = mutableListOf()
    private var current: Passport = Passport.empty()

    fun parseLine(line: String): PassportBuilder {
        current = if (line.isEmpty()) {
            passports.add(current)
            Passport.empty()
        } else {
            Passport.merge(current, line)
        }
        return this
    }

    fun passports(): List<Passport> = passports.apply { add(current) }
}

fun main() {
    val passports = File("src/main/kotlin/day4/input")
        .readLines()
        .fold(PassportBuilder()) { passports, line -> passports.parseLine(line) }
        .passports()

    countPassportsWithAllFields(passports).print()
    countPassportsWithValidFields(passports).print()
}

fun countPassportsWithAllFields(passports: List<Passport>) = passports.count { it.hasRequiredFields() }
fun countPassportsWithValidFields(passports: List<Passport>) = passports.count { it.isValid() }

