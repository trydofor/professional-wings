package pro.fessional.wings.faceless.flywave.util

import java.util.SortedMap
import java.util.TreeMap

/**
 * SQL模板解析和合并。 解析时，
 * (1)忽略引号内文字，避免错误
 * (2)判断字符边界，避免截断
 *
 * @author trydofor
 * @since 2019-06-20
 */
object TemplateUtil {


    /**
     * 直接完成 parse和merge
     * @param txt 文本
     * @param sub 查找文本
     * @param rpl 替换文本
     * @param qto 引号字符，默认`'`
     */
    fun replace(txt: String, sub: String, rpl: String, qto: String = "'"): String {
        val idx = parse(txt, sub, qto)
        return merge(txt, idx, rpl)
    }


    /**
     * 直接完成 parse和merge
     * @param txt 文本
     * @param sub 查找文本
     * @param rpl 替换文本
     * @param qto 引号字符，默认`'`
     */
    fun replace(txt: String, rep: Map<String, String>, qto: String = "'"): String {
        val key = rep.keys.toList()
        val idx = parse(txt, key, qto)
        return merge(txt, idx, rep)
    }

    /**
     * 使用新表名，合并sql片段。
     * @see parse
     * @param txt 目标文本
     * @param idx 解析过的坐标.`<index1,index2>`
     * @param tbl 新表
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
     * 处理多个token的模板
     * @see parse
     * @param txt 目标文本
     * @param idx 解析过的坐标。`<index1, Pair<index2, token>>`
     * @param map 替换值。`<token, value>` token区分大小写
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
     * 忽略大小写解析多个token在文本中的开始和结束坐标。
     * 会处理多个token之间的包含关系，即两个坐标有交叉，舍去后面的，直到没有交叉。
     * @param txt 目标文本
     * @param tkn 单个token
     * @param qto 引号字符，默认`'`
     * @see maskQuote
     * @see isBoundary
     */
    fun parse(txt: String, tkn: List<String>, qto: String = "'"): SortedMap<Int, Pair<Int, String>> {
        val idx = TreeMap<Int, Pair<Int, String>>()
        if (tkn.isEmpty() || txt.isBlank()) {
            return idx
        }
        val msk = maskQuote(txt, qto)
        val ix = TreeMap<Int, Int>()
        for (tk in tkn) {
            parse(msk, tk, ix)
            for ((p1, p2) in ix) {
                idx[p1] = Pair(p2, tk)
            }
            ix.clear()
        }

        // 处理交叉
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
     * 忽略大小写解析单个token在文本中的开始和结束坐标。
     * @param txt 目标文本
     * @param tkn 单个token
     * @param qto 引号字符，默认`'`
     * @see maskQuote
     * @see isBoundary
     */
    fun parse(txt: String, tkn: String, qto: String = "'"): SortedMap<Int, Int> {
        val idx = TreeMap<Int, Int>()
        if (tkn.isBlank() || txt.isBlank()) {
            return idx
        }
        val msk = maskQuote(txt, qto)
        parse(msk, tkn, idx)
        return idx
    }

    private fun parse(msk: String, tkn: String, idx: TreeMap<Int, Int>) {
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
                val bgn = (i == 0 || (i > 0 && isBoundary(msk, i - 1)))
                if (bgn && isBoundary(msk, end)) {
                    idx[i] = end
                }
                off = end
            }
        }
    }

    /**
     * 把引号内字符全部用等数量的空格替换掉，按char数量，不是byte
     * @param txt 目标文本
     * @param qto 引号字符，默认`'`
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
     * 找到跟当前quote字符对应的结束位置。
     * 处理`\`转义情况。
     * @param txt 目标文本
     * @param idx 起始quote的位置
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
     * 是不是字符边界。连续的`[A-Z0-9_]`和非ASCII字符认为是连续的。
     * @param txt 目标文本
     * @param idx 起始位置
     * @param dollar `$`是否认为边界
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
            // 非ascii命名
            c.toInt() > Byte.MAX_VALUE && idx > 0 && txt[idx - 1].toInt() > Byte.MAX_VALUE -> false
            else -> true
        }
    }

    /**
     * 忽略大小写检测sub字串是不是完整存在于文本txt。
     * @see isBoundary
     * @param txt 文本
     * @param sub 子串
     * @param dollar `$`是否认为边界
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