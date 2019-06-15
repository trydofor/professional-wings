@file:JvmName("DigestSugar")

package pro.fessional.wings.oracle.sugar.funs

import java.security.MessageDigest

/**
 * @author trydofor
 * @since 2019-06-14
 */

fun String?.md5() = utf8Hash("MD5")

fun String?.sha1() = utf8Hash("SHA-1")
fun String?.sha256() = utf8Hash("SHA-256")
fun String?.sha512() = utf8Hash("SHA-512")

/**
 * @see MessageDigest.getInstance
 */
fun String?.utf8Hash(type: String): String {
    if (this == null || this.isBlank()) return ""

    val HEX_CHARS = "0123456789ABCDEF"
    val bytes = MessageDigest
            .getInstance(type)
            .digest(this.toByteArray(charset("UTF8")))
    val result = StringBuilder(bytes.size * 2)

    bytes.forEach {
        val i = it.toInt()
        result.append(HEX_CHARS[i shr 4 and 0x0f])
        result.append(HEX_CHARS[i and 0x0f])
    }

    return result.toString()
}
