import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.html.ThScope
import kotlinx.html.classes
import kotlinx.html.dom.create
import kotlinx.html.js.tr
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.serialization.DynamicObjectParser
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import org.w3c.dom.Element
import org.w3c.dom.events.EventListener
import kotlin.browser.document
import kotlin.browser.window

@ImplicitReflectionSerializer
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
                if (info.size == 2) {
                    when (info[0]) {
                        "ip", "ts", "loc", "uag" -> addTr(Pair(info[0], info[1]), tableIpBody)
                    }
                }
            }
        }

        val connection = getNavigatorConnection()
        if(connection != null) {
            val networkInfo  = parseConnection(connection)

            val tableNetworkBody = document.getElementById("networkBody")
            addTr(Pair("Effective Type", networkInfo.effectiveType), tableNetworkBody)
            addTr(Pair("Downlink", "${networkInfo.downlink}Mb/s"), tableNetworkBody)
            addTr(Pair("Rtt", "${networkInfo.rtt}ms"), tableNetworkBody)

//            connection.addEventListener('change' function() {
//                // network change
//            });
//            connection.addEventListener('change', logNetworkInfo);
//            logNetworkInfo()
        }
    }
}

@ImplicitReflectionSerializer
fun parseConnection(connection: Unit): NetworkInformation {
    return DynamicObjectParser().parse<NetworkInformation>(connection)
}

@Serializable
class NetworkInformation(val effectiveType: String,
                         val downlink: Int,
                         val rtt: Int
)

private fun addTr(info: Pair<String, String>, table: Element?) {
    var tr = document.create.tr {
        th {
            scope = ThScope.row
            +info.first
        }
        td {
            classes = setOf("text-center", "mark")
            +info.second
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
fun updateIndicator(source: String) {
    console.log("from $source")
    val status = document.getElementById("status")
    if (window.navigator.onLine) {
        status?.textContent = "Online"
//        status?.style = "color:green"
    } else {
        status?.textContent = "Offline"
//        status?.style = "color:red"
    }
}

fun getNavigatorConnection() : Unit? {
    // fixme kotlin/js doesn't have navigator.connection object yet. So we must create it manually
    js(
            """var connection = window.navigator.connection || window.navigator.mozConnection || window.navigator.webkitConnection; 
                console.log('con : ', connection);
            """
    )

    return js("window.navigator.connection || window.navigator.mozConnection || window.navigator.webkitConnection")
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

