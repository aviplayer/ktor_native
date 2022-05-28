import dto.UserDto
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.contentnegotiation.*

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.request.*
import kotlinx.serialization.json.*



fun main() {
    val db: DB = DB()
    embeddedServer(CIO, port = 8083) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        routing {
            get("/users") {
                call.respond(db.getUsers())
            }
            post("/users") {
                val usr = call.receive<UserDto>()
                call.respondText {  db.addUser(usr)!! }
            }
        }
    }.start(wait = true)
}