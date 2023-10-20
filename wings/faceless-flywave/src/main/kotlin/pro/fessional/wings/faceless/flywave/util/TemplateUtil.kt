package pro.fessional.wings.faceless.flywave.util

import java.util.SortedMap
import java.util.TreeMap

/**
 * SQL template parsing and merging. When parsing.
 * (1) Ignore text in quotes to avoid errors
 * (2) Judge character boundaries to avoid truncation
 *
 * @author trydofor
 * @since 2019-06-20
 */
object TemplateUtil {


    /**
     * Parse and merge are done directly
     *
     * @param txt text
     * @param sub string to find
     * @param rpl string to replace
     * @param qto quotation mark character, default `'`
     * @param bnd whether to check boundaries, default check
     */
    fun replace(txt: String, sub: String, rpl: String, qto: String = "'", bnd: Boolean = true): String {
        val idx = parse(txt, sub, qto, bnd)
        return merge(txt, idx, rpl)
    }


    /**
     * Parse and merge are done directly
     *
     * @param txt text
     * @param rep map of find and replace
     * @param qto quotation mark character, default `'`
     * @param bnd whether to check boundaries, default check
     */
    fun replace(txt: String, rep: Map<String, String>, qto: String = "'", bnd: Boolean = true): String {
        val key = rep.keys.toList()
        val idx = parse(txt, key, qto, bnd)
        return merge(txt, idx, rep)
    }

    /**
     * Merge the sql segment with new table
     *
     * @see parse
     * @param txt text
     * @param idx parsed indexes `<index1,index2>`
     * @param tbl new table
     */
    fun merge(txt: String, idx: SortedMap<Int, Int>, tbl: String): String {
        if (idx.isEmpty()) return txt
        val len = txt.length
        val buff = StringBuilder(len * 3 / 2)
        var off = 0
        for ((p1, p2) in idx) {
            if (p1 > off) {
                buff.append(txt, off, p1)
            }
            buff.append(tbl)
            off = p2
        }

        if (off in 1 until len) {
            buff.append(txt, off, len)
        }
        return buff.toString()
    }

    /**
     * Merge template with multiple token
     *
     * @see parse
     * @param txt text
     * @param idx parsed indexes `<index1, Pair<index2, token>>`
     * @param map map of find and replace `<token, value>` token is case-sensitive
     */
    fun merge(txt: String, idx: SortedMap<Int, Pair<Int, String>>, map: Map<String, String>): String {
        if (idx.isEmpty()) return txt
        val len = txt.length
        val buf = StringBuilder(len * 3)
        var off = 0
        for ((p1, pair) in idx) {
            val (p2, tkn) = pair
            if (p1 > off) {
                buf.append(txt, off, p1)
            }
            buf.append(map[tkn])
            off = p2
        }

        if (off in 1 until len) {
            buf.append(txt, off, len)
        }
        return buf.toString()
    }

    /**
     * Parse the start and end coordinates of multiple tokens in the text (case-insensitive).
     * Containment relationships between multiple tokens are handled, i.e.
     * two coordinates are crossed and the later one is rounded off until there is no crossing.
     *
     * @param txt text
     * @param tkn single token
     * @param qto quotation mark character, default `'`
     * @param bnd whether to check boundaries, default check
     * @see maskQuote
     * @see isBoundary
     */
    fun parse(txt: String, tkn: List<String>, qto: String = "'", bnd: Boolean = true): SortedMap<Int, Pair<Int, String>> {
        val idx = TreeMap<Int, Pair<Int, String>>()
        if (tkn.isEmpty() || txt.isBlank()) {
            return idx
        }
        val msk = maskQuote(txt, qto)
        val ix = TreeMap<Int, Int>()
        for (tk in tkn) {
            if (tk.isEmpty()) continue
            parse(msk, tk, ix, bnd)
            for ((p1, p2) in ix) {
                idx[p1] = Pair(p2, tk)
            }
            ix.clear()
        }

        // handle crossing
        var e2 = -1
        val iter = idx.entries.iterator()
        while (iter.hasNext()) {
            val (p1, p2) = iter.next()
            if (p1 < e2) {
                iter.remove()
            } else {
                e2 = p2.first
            }
        }

        return idx
    }

    /**
     * Parse the start and end coordinates of single token in the text (case-insensitive).
     *
     * @param txt text
     * @param tkn single token
     * @param qto quotation mark character, default `'`
     * @param bnd whether to check boundaries, default check
     * @see maskQuote
     * @see isBoundary
     */
    fun parse(txt: String, tkn: String, qto: String = "'", bnd: Boolean = true): SortedMap<Int, Int> {
        val idx = TreeMap<Int, Int>()
        if (tkn.isBlank() || txt.isBlank()) {
            return idx
        }
        val msk = maskQuote(txt, qto)
        parse(msk, tkn, idx, bnd)
        return idx
    }

    private fun parse(msk: String, tkn: String, idx: TreeMap<Int, Int>, bnd: Boolean = true) {
        if (msk.isBlank() || tkn.isBlank()) {
            return
        }

        val len = tkn.length
        var off = 0
        while (true) {
            val i = msk.indexOf(tkn, off, true)
            if (i < 0) {
                break
            } else {
                val end = i + len
                if (bnd) {
                    val bgn = (i == 0 || isBoundary(msk, i - 1))
                    if (bgn && isBoundary(msk, end)) {
                        idx[i] = end
                    }
                } else {
                    idx[i] = end
                }
                off = end
            }
        }
    }

    /**
     * Replace all the chars inside the quotes with an equal number of spaces, by char, not byte.
     *
     * @param txt text
     * @param qto quotation mark character, default `'`
     */
    fun maskQuote(txt: String, qto: String = "'"): String {
        if (txt.isBlank() || qto.isBlank()) {
            return txt
        }

        var off: Int
        var idx: Int
        var fnd = false
        val buf = txt.toCharArray()
        for (chr in qto) {
            off = 0
            idx = txt.indexOf(chr, off)
            while (idx >= 0) {
                val end = findQuoteEnd(txt, idx)
                if (end > idx) {
                    off = end + 1
                    buf.fill(' ', idx, off)
                    fnd = true
                } else {
                    off = idx + 1
                }
                idx = txt.indexOf(chr, off)
            }
        }

        return if (fnd) {
            String(buf)
        } else {
            txt
        }
    }

    /**
     * Find the end index corresponding to the current quote character. Handles the `\` escape case.
     *
     * @param txt text
     * @param idx index of the start quote
     */
    fun findQuoteEnd(txt: String, idx: Int): Int {
        if (idx < 0 || idx >= txt.length - 1) return -1
        val chr = txt[idx]
        var off = idx + 1
        while (true) {
            val ix = txt.indexOf(chr, off)
            if (ix < 0) return -1
            var cnt = 0
            var qtc = ix - 1
            while (qtc > 0) {
                val c = txt[qtc]
                if (c == '\\') {
                    cnt++
                    qtc--
                } else {
                    break
                }
            }
            if (cnt % 2 == 0) {
                return ix
            }
            off = ix + 1
        }
    }

    /**
     * Whether is a character boundary.
     * Consecutive `[A-Z0-9_]` and non-ASCII characters are considered consecutive.
     *
     * @param txt text
     * @param idx index of starting
     * @param dollar whether `$` is the boundary
     */
    fun isBoundary(txt: String, idx: Int, dollar: Boolean = true): Boolean {
        if (idx <= 0 || idx >= txt.length - 1) return true

        val c = txt[idx]
        return when {
            c in 'A'..'Z' -> false
            c in 'a'..'z' -> false
            c in '0'..'9' -> false
            c == '_' -> false
            c == '$' -> dollar
            // non-ascii naming
            c.code > Byte.MAX_VALUE && txt[idx - 1].code > Byte.MAX_VALUE -> false
            else -> true
        }
    }

    /**
     * Whether the sub-string is complete in the text (case-insensitive).
     *
     * @see isBoundary
     * @param txt text
     * @param sub substring
     * @param dollar whether `$` is the boundary
     */
    fun isBoundary(txt: String, sub: String, dollar: Boolean = true): Boolean {
        if (txt.isEmpty() || sub.isEmpty()) return false

        var off = 0
        val len = sub.length
        while (true) {
            val i = txt.indexOf(sub, off, true)
            if (i < 0) {
                return false
            } else {
                val end = i + len
                if (isBoundary(txt, i - 1, dollar) && isBoundary(txt, end, dollar)) {
                    return true
                }
                off = end
            }
        }
    }
}
