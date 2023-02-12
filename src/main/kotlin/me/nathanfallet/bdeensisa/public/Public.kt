package me.nathanfallet.bdeensisa.public

import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Route.public() {
    publicPages()
    publicTopics()
    publicQuestions()
    publicEvents()
    publicUploads()
    publicClubs()
}
