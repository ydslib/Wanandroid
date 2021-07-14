package com.yds.base

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        println(findSubstring("barfoothefoobarman", arrayOf("foo","bar")))
    }

    fun findSubstring(s: String, words: Array<String>): List<Int?>? {
        val res: MutableList<Int?> = ArrayList()
        val words_len = words.size
        val w_len = words[0].length
        val all_len = words_len * w_len
        val s_len = s.length
        if (s_len < all_len) return res
        val map: MutableMap<String, Int> = HashMap()
        for (i in 0 until words_len) {
            map[words[i]] = map.getOrDefault(words[i], 0) + 1
        }
        for (i in 0 until s_len - all_len) {
            val tmp = s.substring(i, i + all_len)
            val sMap: MutableMap<String, Int> = HashMap()
            var j = 0
            while (j < all_len) {
                val key = tmp.substring(j, j + words_len)
                sMap[key] = sMap.getOrDefault(key, 0) + 1
                j = j + words_len
            }
            if (sMap == map) res.add(i)
        }
        return res
    }
}