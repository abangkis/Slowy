import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import kotlinx.html.ThScope
import kotlinx.html.classes
import kotlinx.html.dom.create
import kotlinx.html.js.tr
import kotlinx.html.td
import kotlinx.html.th
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window
import kotlin.random.Random

fun main() {
    window.onload = {
        var table = document.getElementById("tableBody")

        MainScope().launch {
            fetchUsers().forEach {
                var tr = document.create.tr {
                    th {
                        scope = ThScope.row
                        +"${it.id}"
                    }
                    td {
                        classes = setOf("text-center", "mark")
                        +"${it.username}"
                    }
//                    td { +"${it.name}" }
//                    td { +"${it.email}" }
//                    td { +"${it.website}" }
//                    td { +"${it.phone}" }
                }
                table?.appendChild(tr)
            }
        }
        drawCanvas()
    }
//    document.body!!.append.div {
//        h1 {
//            +"Welcome to Kotlin/JS!"
//        }
//        p {
//            +"Fancy joining this year's "
//            a("https://kotlinconf.com/") {
//                +"KotlinConf"
//            }
//            +"?"
//        }
//    }

//    document.write("Hello, world!")
//    document.bgColor = "FFAA12"
//
//    val email = document.getElementById("email") as HTMLInputElement
//    email.value = "hadi@jetbrains.com"
}

data class JphUser(
    var id: Int,
    var name: String,
    var username: String
)


suspend fun fetchUsers() = window.fetch("https://jsonplaceholder.typicode.com/users")
    .await()
    .json()
    .await()
    .unsafeCast<Array<JphUser>>()

private fun drawCanvas() {
    val canvasEl = document.getElementById("myCanvas")
    console.log("Canvas $canvasEl")

    if(canvasEl != null) {
        val canvas = canvasEl as HTMLCanvasElement
        val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
        with(ctx) {
            repeat(30) {
                beginPath()
                fillStyle = listOf("red", "green", "orange", "blue").random()
                rect(randomCoordinate(), randomCoordinate(), 20.0, 20.0)
                fill()
                closePath()
            }
        }
    }


}

private fun randomCoordinate() = Random.nextDouble(0.0, 200.0)
