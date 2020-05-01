import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
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

fun saveToLocal(history: History, index: Int) {
    // fixme we should use indexedDb but kotlin js has not support it
    val json = Json(JsonConfiguration.Stable)
    val jHistory = json.stringify(History.serializer(), history)
    console.log("json $jHistory")

//    localStorage["history$index"] = history.toString() // do we need to serialize it to json?
}

fun loadFromLocal() {
    val histories = mutableListOf<History>()
//    for(i in 1..10) {
//        val history = localStorage["history$i"]
//        if(history != null) {
//            histories.add(history)
//        }
//    }
}

@Serializable
data class History(val timestamp:Long, val ip:String, val type:String, val downlink:Float, val rtt:Int)

fun isLocalStorageAvailable() {
//    if (typeof(Storage) !== "undefined") {
//        // Code for localStorage/sessionStorage.
//    } else {
//        // Sorry! No Web Storage support..
//    }
}