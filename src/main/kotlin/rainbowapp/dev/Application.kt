package rainbowapp.dev

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.delay
import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {

        install(ContentNegotiation) {
            json()
        }

        install(CallLogging) {
            level = Level.TRACE
        }

        routing {

            val state = mutableMapOf<String, String>()
            val errors = mutableSetOf<String>()

            get {
                val parameters = call.parameters
                val id = parameters["state"]
                val code = parameters["code"]
                if (id != null) {
                    if (code == null) {
                        errors.add(id)
                        call.respondText("Login failed.")
                    } else {
                        state[id] = code
                        call.respondText("Congrats, you have logged in. Now you can go back to Rainbow.")
                    }
                }
            }

            get("/code") {
                val id = call.parameters["id"]
                while (state[id] == null) {
                    if (errors.contains(id))
                        call.respond(HttpStatusCode.Unauthorized, mapOf("code" to state[id]))
                    else
                        delay(500)
                }
                call.respond(mapOf("code" to state[id]))
            }
        }
    }.start(wait = true)
}
