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
import kotlin.js.Date

@ImplicitReflectionSerializer
fun main() {
//    document.write("Hello, world!")
//    document.bgColor = "FFAA12"

// Update the online status icon based on connectivity
    window.addEventListener("online", EventListener { updateStatus("from online") })
    window.addEventListener("offline", EventListener { updateStatus("from offline") })

    window.onload = {
        updateStatus("from onload")
        updateNetworkBody()
    }
}

fun updateStatus(source: String) {
    updateConnectionStatus(source)
    updateIpBody()
}

/**
 *  Please note this will check all adapter. So if you have virtual adapter like docker.
 *  It will still be online although your hardware adapter is offline
 */
fun updateConnectionStatus(source: String) {
    console.log("from $source")
    val status = document.getElementById("connectionStatus")
    if (window.navigator.onLine) {
        status?.textContent = "Connection Status: Online"
//        status?.style = "color:green"
    } else {
        status?.textContent = "Connection Status: Offline"
//        status?.style = "color:red"
    }
}

private fun updateIpBody() {
    val tableIpBody = document.getElementById("ipBody")

    MainScope().launch {
        val ipInfo = fetchIpInfo()

        val status = document.getElementById("internetStatus")
        status?.textContent = "Internet Status: Online"

        ipInfo.split("\n").forEach {
            val info = it.split("=")
            if (info.size == 2) {
                when (info[0]) {
                    "ip", "loc", "uag" -> addTr(Pair(info[0], info[1]), tableIpBody)
                    "ts" -> {
                        console.log("ts ${info[1]}")
                        val ts = info[1].toDouble() * 1000 // change from second to millisecond
                        console.log("ts $ts")
                        addTr(Pair("ts", Date(ts).toString()), tableIpBody)
                    }
                }
            }
        }
    }
}

@ImplicitReflectionSerializer
private fun updateNetworkBody() {
    val connection = getNavigatorConnection()
    if (connection != null) {
        val networkInfo = parseConnection(connection)

        val tableNetworkBody = document.getElementById("networkBody")
        addTr(Pair("Effective Type", networkInfo.effectiveType), tableNetworkBody)
        addTr(Pair("Down link", "${networkInfo.downlink}Mb/s"), tableNetworkBody)
        addTr(Pair("Round trip time", "${networkInfo.rtt}ms"), tableNetworkBody)
        addTr(Pair("Save Data", "${networkInfo.saveData}"), tableNetworkBody)

//            connection.addEventListener('change' function() {
//                // network change
//            });
//            connection.addEventListener('change', logNetworkInfo);
//            logNetworkInfo()
    }
}

@ImplicitReflectionSerializer
fun parseConnection(connection: Unit): NetworkInformation {
    return DynamicObjectParser().parse<NetworkInformation>(connection)
}

@Serializable
class NetworkInformation(val effectiveType: String,
                         val downlink: Int,
                         val rtt: Int,
                         val saveData: Boolean
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

