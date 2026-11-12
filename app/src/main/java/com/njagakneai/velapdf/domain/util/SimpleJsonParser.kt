package com.njagakneai.velapdf.domain.util

/**
 * Parser JSON rekursif murni berbasis Kotlin JVM tanpa ketergantungan org.json Android,
 * sehingga seluruh aset kamus/ontologi dapat langsung diuji di unit test JVM maupun runtime Android.
 */
object SimpleJsonParser {

    @Suppress("UNCHECKED_CAST")
    fun parseObject(json: String): Map<String, Any?> {
        val parsed = Parser(json).parseValue()
        return parsed as? Map<String, Any?>
            ?: throw IllegalArgumentException("JSON root bukan sebuah objek.")
    }

    private class Parser(private val src: String) {
        private var pos = 0

        fun parseValue(): Any? {
            skipWhitespace()
            if (pos >= src.length) throw IllegalArgumentException("Akhir JSON tidak terduga.")
            return when (val ch = src[pos]) {
                '{' -> parseObject()
                '[' -> parseArray()
                '"' -> parseString()
                't', 'f' -> parseBoolean()
                'n' -> parseNull()
                '-', in '0'..'9' -> parseNumber()
                else -> throw IllegalArgumentException("Karakter JSON tidak valid '$ch' di posisi $pos")
            }
        }

        private fun parseObject(): Map<String, Any?> {
            pos++ // '{'
            val map = linkedMapOf<String, Any?>()
            skipWhitespace()
            if (pos < src.length && src[pos] == '}') {
                pos++
                return map
            }
            while (pos < src.length) {
                skipWhitespace()
                if (src[pos] != '"') {
                    throw IllegalArgumentException("Mengharapkan key string pada posisi $pos")
                }
                val key = parseString()
                skipWhitespace()
                if (pos >= src.length || src[pos] != ':') {
                    throw IllegalArgumentException("Mengharapkan ':' setelah key '$key' pada posisi $pos")
                }
                pos++
                map[key] = parseValue()
                skipWhitespace()
                if (pos < src.length && src[pos] == ',') {
                    pos++
                    continue
                }
                if (pos < src.length && src[pos] == '}') {
                    pos++
                    return map
                }
                throw IllegalArgumentException("Mengharapkan ',' atau '}' pada posisi $pos")
            }
            throw IllegalArgumentException("Objek JSON tidak ditutup.")
        }

        private fun parseArray(): List<Any?> {
            pos++ // '['
            val list = mutableListOf<Any?>()
            skipWhitespace()
            if (pos < src.length && src[pos] == ']') {
                pos++
                return list
            }
            while (pos < src.length) {
                list.add(parseValue())
                skipWhitespace()
                if (pos < src.length && src[pos] == ',') {
                    pos++
                    continue
                }
                if (pos < src.length && src[pos] == ']') {
                    pos++
                    return list
                }
                throw IllegalArgumentException("Mengharapkan ',' atau ']' pada posisi $pos")
            }
            throw IllegalArgumentException("Array JSON tidak ditutup.")
        }

        private fun parseString(): String {
            pos++ // '"'
            val sb = StringBuilder()
            while (pos < src.length) {
                val ch = src[pos++]
                when (ch) {
                    '"' -> return sb.toString()
                    '\\' -> {
                        if (pos >= src.length) throw IllegalArgumentException("Escape sequence tidak lengkap.")
                        when (val esc = src[pos++]) {
                            '"', '\\', '/' -> sb.append(esc)
                            'b' -> sb.append('\b')
                            'f' -> sb.append('\u000C')
                            'n' -> sb.append('\n')
                            'r' -> sb.append('\r')
                            't' -> sb.append('\t')
                            'u' -> {
                                val hex = src.substring(pos, pos + 4)
                                sb.append(hex.toInt(16).toChar())
                                pos += 4
                            }
                            else -> throw IllegalArgumentException("Escape '\\$esc' tidak dikenali.")
                        }
                    }
                    else -> sb.append(ch)
                }
            }
            throw IllegalArgumentException("String JSON tidak ditutup.")
        }

        private fun parseBoolean(): Boolean {
            return if (src.startsWith("true", pos)) {
                pos += 4
                true
            } else if (src.startsWith("false", pos)) {
                pos += 5
                false
            } else {
                throw IllegalArgumentException("Literal boolean tidak valid pada posisi $pos")
            }
        }

        private fun parseNull(): Any? {
            if (src.startsWith("null", pos)) {
                pos += 4
                return null
            }
            throw IllegalArgumentException("Literal null tidak valid pada posisi $pos")
        }

        private fun parseNumber(): Number {
            val start = pos
            if (src[pos] == '-') pos++
            while (pos < src.length && (src[pos].isDigit() || src[pos] in ".eE+-")) {
                pos++
            }
            val raw = src.substring(start, pos)
            return if (raw.contains('.') || raw.contains('e') || raw.contains('E')) {
                raw.toDouble()
            } else {
                raw.toLong()
            }
        }

        private fun skipWhitespace() {
            while (pos < src.length && src[pos].isWhitespace()) {
                pos++
            }
        }
    }
}

fun Map<String, Any?>.str(key: String, default: String = ""): String =
    this[key] as? String ?: default

fun Map<String, Any?>.optInt(key: String, default: Int = 0): Int =
    (this[key] as? Number)?.toInt() ?: default

fun Map<String, Any?>.optBool(key: String, default: Boolean = false): Boolean =
    this[key] as? Boolean ?: default

@Suppress("UNCHECKED_CAST")
fun Map<String, Any?>.obj(key: String): Map<String, Any?> =
    this[key] as? Map<String, Any?> ?: emptyMap()

@Suppress("UNCHECKED_CAST")
fun Map<String, Any?>.objList(key: String): List<Map<String, Any?>> =
    (this[key] as? List<Any?>)?.mapNotNull { it as? Map<String, Any?> } ?: emptyList()

fun Map<String, Any?>.strList(key: String): List<String> =
    (this[key] as? List<Any?>)?.mapNotNull { it as? String } ?: emptyList()

@Suppress("UNCHECKED_CAST")
fun Map<String, Any?>.strMap(key: String): Map<String, String> {
    val raw = this[key] as? Map<String, Any?> ?: return emptyMap()
    return raw.mapNotNull { (k, v) -> (v as? String)?.let { k to it } }.toMap()
}
