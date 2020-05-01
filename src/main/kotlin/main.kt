import kotlinx.coroutines.*
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
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.events.EventListener
import kotlin.browser.document
import kotlin.browser.localStorage
import kotlin.browser.window
import kotlin.js.Date

@ImplicitReflectionSerializer
fun main() {
//    document.write("Hello, world!")
//    document.bgColor = "FFAA12"

// Update the online status icon based on connectivity
    window.addEventListener("online", EventListener { MainScope().launch { updateStatus("from online") }})
    window.addEventListener("offline", EventListener { MainScope().launch { updateStatus("from offline") }})

    window.onload = {
        MainScope().launch {
            val ip = updateStatus("from onload")
            val info = updateBrowserBody()

            info?.let {
                val history = History(Date.now(), ip, it.effectiveType, it.downlink, it.rtt)
                saveToLocal(history)
            }

            loadData()
            setClearButton()
        }

//        loadChart()
    }
}

private fun setClearButton() {
    val button = document.getElementById("clearHistory") as HTMLButtonElement
    button.addEventListener("click", {
        console.log("Clearing history")
        localStorage.clear()
        loadData()
    })
}

suspend fun updateStatus(source: String): String {
    updateConnectionStatus(source)
    return updateIpBody()
}

/**
 *  Please note this will check all adapter. So if you have virtual adapter like docker.
 *  It will still be online although your hardware adapter is offline
 */
fun updateConnectionStatus(source: String) {
    console.log("$source")
    val status = document.getElementById("connectionStatus")
    if (window.navigator.onLine) {
        status?.textContent = "Connection Status: Online"
//        status?.style = "color:green"
    } else {
        status?.textContent = "Connection Status: Offline"
//        status?.style = "color:red"
    }
}

private suspend fun updateIpBody() : String = coroutineScope {
    val tableIpBody = document.getElementById("ipBody")

    val ipDef = async {
        val ipInfo = fetchIpInfo()

        var ip = "unknown"

        val status = document.getElementById("internetStatus")
        status?.textContent = "Internet Status: Online"

        ipInfo.split("\n").forEach {
            val info = it.split("=")
            if (info.size == 2) {
                when (info[0]) {
                    "ip" -> {
                        ip = info[1]
                        addTr(Pair(info[0], info[1]), tableIpBody)
                    }
                    "loc", "uag" -> addTr(Pair(info[0], info[1]), tableIpBody)
                    "ts" -> {
                        val ts = info[1].toDouble() * 1000 // change from second to millisecond
                        addTr(Pair("ts", Date(ts).toString()), tableIpBody)
                    }
                }
            }
        }
        ip
    }

    ipDef.await()
}

@ImplicitReflectionSerializer
private fun updateBrowserBody(): NetworkInformation? {
    val connection = getNavigatorConnection()
    if (connection != null) {
        val networkInfo = parseConnection(connection)

        val tableNetworkBody = document.getElementById("browserBody")
        addTr(Pair("Effective Type", networkInfo.effectiveType), tableNetworkBody)
        addTr(Pair("Down link", "${networkInfo.downlink}Mb/s"), tableNetworkBody)
        addTr(Pair("Round trip time", "${networkInfo.rtt}ms"), tableNetworkBody)
        addTr(Pair("Save Data", "${networkInfo.saveData}"), tableNetworkBody)

        return networkInfo
//            connection.addEventListener('change' function() {
//                // network change
//            });
//            connection.addEventListener('change', logNetworkInfo);
//            logNetworkInfo()
    }

    return null
}

@ImplicitReflectionSerializer
fun parseConnection(connection: Unit): NetworkInformation {
    return DynamicObjectParser().parse<NetworkInformation>(connection)
}

@Serializable
class NetworkInformation(val effectiveType: String,
                         val downlink: Float,
                         val rtt: Int,
                         val saveData: Boolean
)

fun addTr(info: Pair<String, String>, table: Element?) {
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

