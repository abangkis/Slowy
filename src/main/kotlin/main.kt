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
import org.w3c.dom.events.EventListener
import kotlin.browser.document
import kotlin.browser.window

fun main() {
//    document.write("Hello, world!")
//    document.bgColor = "FFAA12"

// Update the online status icon based on connectivity
    window.addEventListener("online", EventListener { updateIndicator("from online") })
    window.addEventListener("offline", EventListener { updateIndicator("from offline") })

    window.onload = {
        updateIndicator("from onload")

        val tableIpBody = document.getElementById("ipBody")

        MainScope().launch {
            val ipInfo = fetchIpInfo()
            ipInfo.split("\n").forEach {
                val info = it.split("=")
                if(info.size == 2){
                    when(info[0]) {
                        "ip", "ts", "loc", "uag" -> addTr(info, tableIpBody)
                    }
                }
            }
        }

//        var connection = window.navigator.connection || window.navigator.mozConnection || window.navigator.webkitConnection
//        if(connection) {
//            connection.addEventListener('change', logNetworkInfo);
//            logNetworkInfo()
//        }

        val tableNetworkBody = document.getElementById("networkBody")




        MainScope().launch {
            val ipInfo = fetchIpInfo()
            ipInfo.split("\n").forEach {
                val info = it.split("=")
                if(info.size == 2){
                    when(info[0]) {
                        "ip", "ts", "loc", "uag" -> addTr(info, tableNetworkBody)
                    }
                }
            }
        }
    }
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

/**
 *  Please note this will check all adapter. So if you have virtual adapter like docker.
 *  It will still be online although your hardware adapter is offline
 */
fun  updateIndicator(source: String) {
    console.log("from $source")
    val status = document.getElementById("status")
    if(window.navigator.onLine) {
        status?.textContent = "Online"
//        status?.style = "color:green"
    }
    else {
        status?.textContent = "Offline"
//        status?.style = "color:red"
    }
}

//fun logNetworkInfo() {
//    // Network type that browser uses
//    log('         type: ' + navigator.connection.type);
//
//    // Effective bandwidth estimate
//    log('     downlink: ' + navigator.connection.downlink + 'Mb/s');
//
//    // Effective round-trip time estimate
//    log('          rtt: ' + navigator.connection.rtt + 'ms');
//
//    // Upper bound on the downlink speed of the first network hop
//    log('  downlinkMax: ' + navigator.connection.downlinkMax + 'Mb/s');
//
//    // Effective connection type determined using a combination of recently
//    // observed rtt and downlink values: ' +
//    log('effectiveType: ' + navigator.connection.effectiveType);
//
//    // True if the user has requested a reduced data usage mode from the user
//    // agent.
//    log('     saveData: ' + navigator.connection.saveData);
//}


