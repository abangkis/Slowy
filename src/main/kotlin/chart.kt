import kotlinx.html.Entities
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date
import kotlin.js.Json


fun loadData() {
    val tableNetworkBody = document.getElementById("historyBody")


    loadFromLocal().asReversed().forEachIndexed { index, history ->
        val date = Date(history.timestamp)

        addTr(
            Pair((index+1).toString(),
                "$date | ${history.ip} | ${history.type} | ${history.downlink}Mb/s | ${history.rtt}ms"),
            tableNetworkBody
        )
    }
}

fun loadChart() {

//    val chart = Chart()

//    val canvas = document.getElementById("myChart") as HTMLCanvasElement
//    val ctx = canvas.getContext("2d") as CanvasRenderingContext2D


    // window object is now is a dynamic object like in javascript
//    val lottie = window.asDynamic().lottie
//    val chart  = window.asDynamic().Chart(ctx)



//    var chart = Chart(ctx, {
//
//        // The data for our dataset
//        data: {
//        labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
//        datasets: [{
//        label: 'My First dataset',
//        backgroundColor: 'rgb(255, 99, 132)',
//        borderColor: 'rgb(255, 99, 132)',
//        data: [0, 10, 5, 2, 20, 30, 45]
//    }]
//    },
//
//        // Configuration options go here
//        options: {}
//    });

//    val div = HTMLDivElement()
//    val animData = AnimData(div, "svg")
//    lottie.loadAnimation(animData)
}

//external val chart : ChartJs
//
//external class ChartJs {
//
//}

external val lottie: Lottie

external class Lottie {
//    fun loadAnimation(animData: Json)
    fun loadAnimation(animData: AnimData)
}

data class AnimData(val container: HTMLElement,
                    val renderer: String
)