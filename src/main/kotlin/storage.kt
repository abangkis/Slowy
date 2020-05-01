import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.parse
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.browser.localStorage
import kotlin.browser.window

/*
 * window.localStorage - stores data with no expiration date
 * window.sessionStorage - stores data for one session per tab (data is lost when the browser tab is closed)
 *
 * https://web.dev/storage-for-the-web/
 * https://truetocode.com/web-storage-a-comparative-study-of-local-storagesession-storagecookiesindexeddb-and-websql/
 */
fun saveToSession() {

}

fun saveToLocal(history: History) {
    // fixme we should use indexedDb but kotlin js has not support it
    val json = Json(JsonConfiguration.Stable)
    val jHistory = json.stringify(History.serializer(), history)
    console.log("json $jHistory")

    val index = getIndex()
    localStorage["history$index"] = jHistory
    localStorage["index"] = index.toString()
}

private fun getIndex(): Int {
    var index = localStorage["index"]
    index = if (index != null) {
        (index.toInt() + 1).toString()
    } else "1"
    return index.toInt()
}

@OptIn(UnstableDefault::class)
fun loadFromLocal(): MutableList<History> {
    val histories = mutableListOf<History>()

    val index = getIndex()

    for(i in 1..index) {
        val history = localStorage["history$i"]
        if(history != null) {
            histories.add(Json.parse(History.serializer(), history))
        }
    }

    return histories
}

@Serializable
data class History(val timestamp:Double, val ip:String, val type:String, val downlink:Float, val rtt:Int)

fun isLocalStorageAvailable() {
//    if (typeof(Storage) !== "undefined") {
//        // Code for localStorage/sessionStorage.
//    } else {
//        // Sorry! No Web Storage support..
//    }
}