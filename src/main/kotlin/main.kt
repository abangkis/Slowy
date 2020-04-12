import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.html.ThScope
import kotlinx.html.classes
import kotlinx.html.dom.create
import kotlinx.html.js.tr
import kotlinx.html.td
import kotlinx.html.th
import org.w3c.dom.Element
import kotlin.browser.document
import kotlin.browser.window

fun main() {
//    document.write("Hello, world!")
//    document.bgColor = "FFAA12"

    window.onload = {
        var table = document.getElementById("tableBody")

        MainScope().launch {
            val ipInfo = fetchIpInfo()
            ipInfo.split("\n").forEach {
                val info = it.split("=")
                if(info.size == 2){
                    when(info[0]) {
                        "ip", "ts", "loc", "uag" -> addTr(info, table)
                    }
                }
            }
        }
    }

//
//    val email = document.getElementById("email") as HTMLInputElement
//    email.value = "hadi@jetbrains.com"
}

private fun addTr(info: List<String>, table: Element?) {
    var tr = document.create.tr {
        th {
            scope = ThScope.row
            +info[0]
        }
        td {
            classes = setOf("text-center", "mark")
            +info[1]
        }
    }
    table?.appendChild(tr)
}

suspend fun fetchIpInfo() = window.fetch("https://www.cloudflare.com/cdn-cgi/trace")
        .await()
        .text()
        .await()
        .toString()
//        .json()
//        .await()
//        .unsafeCast<Array<JphUser>>()


