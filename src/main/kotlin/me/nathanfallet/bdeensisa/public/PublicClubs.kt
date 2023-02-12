package me.nathanfallet.bdeensisa.public

import kotlinx.datetime.*
import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import me.nathanfallet.bdeensisa.account.getUser
import me.nathanfallet.bdeensisa.database.Database
import me.nathanfallet.bdeensisa.models.*
import org.jetbrains.exposed.sql.*

fun Route.publicClubs() {
    route("/clubs") {
        get {
            val mine = getUser()?.let { user ->
                Database.dbQuery {
                    ClubMemberships
                        .join(Clubs, JoinType.INNER, ClubMemberships.clubId, Clubs.id)
                        .select { ClubMemberships.userId eq user.id }
                        .orderBy(Clubs.createdAt, SortOrder.DESC)
                        .map { ClubMembership(it, null, Club(it)) }
                }
            } ?: emptyList()
            val clubs = Database.dbQuery {
                Clubs
                    .select { Clubs.validated eq true and (Clubs.id notInList mine.map { it.clubId }) }
                    .orderBy(Clubs.createdAt, SortOrder.DESC)
                    .map { Club(it) }
            }
            call.respond(FreeMarkerContent(
                "public/clubs/list.ftl",
                mapOf(
                    "title" to "Clubs",
                    "clubs" to clubs,
                    "mine" to mine,
                    "menu" to MenuItems.fetch()
                )
            ))
        }
        get("/suggest") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/clubs/suggest")
                return@get
            }
            if (user.cotisant == null) {
                call.respond(FreeMarkerContent(
                    "public/clubs/suggest.ftl",
                    mapOf(
                        "title" to "Proposer un club",
                        "menu" to MenuItems.fetch(),
                        "error" to "Vous devez être cotisant pour pouvoir proposer un club."
                    )
                ))
                return@get
            }
            call.respond(FreeMarkerContent(
                "public/clubs/suggest.ftl",
                mapOf(
                    "title" to "Proposer un club",
                    "menu" to MenuItems.fetch()
                )
            ))
        }
        post("/suggest") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/clubs/suggest")
                return@post
            }
            if (user.cotisant == null) {
                call.respond(FreeMarkerContent(
                    "public/clubs/suggest.ftl",
                    mapOf(
                        "title" to "Proposer un club",
                        "menu" to MenuItems.fetch(),
                        "error" to "Vous devez être cotisant pour pouvoir proposer un club."
                    )
                ))
                return@post
            }
            Database.dbQuery {
                ClubMemberships
                    .join(Clubs, JoinType.INNER, ClubMemberships.clubId, Clubs.id)
                    .select {
                        ClubMemberships.userId eq user.id and
                        (Clubs.createdAt greater Clock.System.now().minus(1, DateTimeUnit.DAY, TimeZone.currentSystemDefault()).toString())
                    }
                    .firstOrNull()
            }?.let {
                call.respond(FreeMarkerContent(
                    "public/clubs/suggest.ftl",
                    mapOf(
                        "title" to "Proposer un club",
                        "menu" to MenuItems.fetch(),
                        "error" to "Vous avez déjà proposé un club il y a moins de 24h, veuillez attendre avant d'en proposer un nouveau."
                    )
                ))
                return@post
            }
            val params = call.receiveParameters()
            val name = params["name"]
            val description = params["description"]
            val information = params["information"]
            if (name == null || description == null || information == null) {
                call.respond(FreeMarkerContent(
                    "public/clubs/suggest.ftl",
                    mapOf(
                        "title" to "Proposer un club",
                        "menu" to MenuItems.fetch(),
                        "error" to "Veuillez remplir tous les champs du formulaire."
                    )
                ))
                return@post
            }
            Database.dbQuery {
                val id = Clubs.generateId()
                Clubs.insert {
                    it[Clubs.id] = id
                    it[Clubs.name] = name
                    it[Clubs.description] = description
                    it[Clubs.information] = information
                    it[Clubs.validated] = false
                    it[Clubs.createdAt] = Clock.System.now().toString()
                }
                ClubMemberships.insert {
                    it[ClubMemberships.clubId] = id
                    it[ClubMemberships.userId] = user.id
                    it[ClubMemberships.role] = "owner"
                }
            }
            call.respond(FreeMarkerContent(
                "public/clubs/suggest.ftl",
                mapOf(
                    "title" to "Proposer un club",
                    "menu" to MenuItems.fetch(),
                    "success" to true
                )
            ))
        }
    }
}
