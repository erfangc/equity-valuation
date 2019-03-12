package com.erfangc.equity.valuation.yahoo

import org.jsoup.Jsoup
import org.junit.Test
import kotlin.test.assertEquals

class TableParserTest {

    @Test
    fun parseTableWithColumnsAsDates() {
        val doc = Jsoup.parse(
            """
<html>
<body>
<table>
    <tbody>
    <tr>
        <td>Foo</td>
        <td>1/1/2008</td>
        <td>12/31/2009</td>
    </tr>
    <tr colspan="3"/>
    <tr>
        <td>Metric1</td>
        <td>1.5</td>
        <td>2.4</td>
    </tr>
    <tr>
        <td>Metric 2</td>
        <td>150</td>
        <td>166</td>
    </tr>
    </tbody>
</table>
</body>
</html>
        """.trimIndent()
        )
        val table = doc.select("table").first()
        val datedContent = TableParser().parseTableWithColumnsAsDates(table)
        val latest = datedContent.latest()
        assertEquals(2, latest.size)
        assertEquals(2.4, latest.getValue("Metric1"))
        assertEquals(166.0, latest.getValue("Metric2"))
    }
}