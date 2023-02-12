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
            val user = getUser()
            val mine = user?.let {
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
                    "join" to (user?.cotisant != null),
                    "menu" to MenuItems.fetch(),
                    "error" to call.request.queryParameters["error"]
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
                    it[ClubMemberships.role] = "admin"
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
        get("/{id}") {
            val club = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Clubs
                        .select { Clubs.id eq id and (Clubs.validated eq true) }
                        .map { Club(it) }
                        .firstOrNull()
                }
            } ?: run {
                call.respond(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@get
            }
            val members = Database.dbQuery {
                ClubMemberships
                    .join(Users, JoinType.INNER, ClubMemberships.userId, Users.id)
                    .select { ClubMemberships.clubId eq club.id }
                    .map { ClubMembership(it, User(it)) }
            }
            val membership = getUser()?.let { user ->
                members.find { it.userId == user.id }
            }
            call.respond(FreeMarkerContent("public/clubs/details.ftl", mapOf(
                "title" to club.name,
                "club" to club,
                "members" to members,
                "membership" to membership,
                "menu" to MenuItems.fetch()
            )))
        }
        get("/{id}/edit") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/clubs/suggest")
                return@get
            }
            val club = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Clubs
                        .join(ClubMemberships, JoinType.INNER, Clubs.id, ClubMemberships.clubId)
                        .select {
                            Clubs.id eq id and
                            (Clubs.validated eq true) and
                            (ClubMemberships.userId eq user.id) and
                            (ClubMemberships.role eq "admin")
                        }
                        .map { Club(it) }
                        .firstOrNull()
                }
            } ?: run {
                call.respond(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@get
            }
            val members = Database.dbQuery {
                ClubMemberships
                    .join(Users, JoinType.INNER, ClubMemberships.userId, Users.id)
                    .select { ClubMemberships.clubId eq club.id }
                    .map { ClubMembership(it, User(it)) }
            }
            call.respond(FreeMarkerContent("public/clubs/edit.ftl", mapOf(
                "title" to club.name,
                "club" to club,
                "members" to members,
                "menu" to MenuItems.fetch()
            )))
        }
        post("/{id}/edit") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/clubs/suggest")
                return@post
            }
            val club = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Clubs
                        .join(ClubMemberships, JoinType.INNER, Clubs.id, ClubMemberships.clubId)
                        .select {
                            Clubs.id eq id and
                            (Clubs.validated eq true) and
                            (ClubMemberships.userId eq user.id) and
                            (ClubMemberships.role eq "admin")
                        }
                        .map { Club(it) }
                        .firstOrNull()
                }
            } ?: run {
                call.respond(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@post
            }
            val params = call.receiveParameters()
            val name = params["name"]
            val description = params["description"]
            val information = params["information"]
            if (name == null || description == null || information == null) {
                call.response.status(HttpStatusCode.BadRequest)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Requête invalide")))
                return@post
            }
            Database.dbQuery {
                Clubs.update({ Clubs.id eq club.id }) {
                    it[Clubs.name] = name
                    it[Clubs.description] = description
                    it[Clubs.information] = information
                }
            }
            call.respondRedirect("/clubs/${club.id}")
        }
        get("/{id}/join") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/clubs")
                return@get
            }
            if (user.cotisant == null) {
                call.respondRedirect("/clubs?error=Vous devez %C3%AAtre cotisant pour pouvoir rejoindre un club.")
                return@get
            }
            val club = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Clubs
                        .select { Clubs.id eq id }
                        .map { Club(it) }
                        .firstOrNull()
                }
            } ?: run {
                call.respond(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@get
            }
            Database.dbQuery {
                ClubMemberships
                    .select { ClubMemberships.clubId eq club.id and (ClubMemberships.userId eq user.id) }
                    .firstOrNull()
            }?.let {
                call.respondRedirect("/clubs?error=Vous %C3%AAtes d%C3%A9j%C3%A0 membre de ce club.")
                return@get
            }
            Database.dbQuery {
                ClubMemberships.insert {
                    it[ClubMemberships.clubId] = club.id
                    it[ClubMemberships.userId] = user.id
                    it[ClubMemberships.role] = "member"
                }
            }
            call.respondRedirect("/clubs")
        }
        get("/{id}/leave") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/clubs")
                return@get
            }
            val club = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Clubs
                        .select { Clubs.id eq id }
                        .map { Club(it) }
                        .firstOrNull()
                }
            } ?: run {
                call.respond(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@get
            }
            Database.dbQuery {
                ClubMemberships
                    .select { ClubMemberships.clubId eq club.id and (ClubMemberships.userId eq user.id) }
                    .firstOrNull()
            } ?: run {
                call.respondRedirect("/clubs?error=Vous n'%C3%AAtes pas membre de ce club.")
                return@get
            }
            Database.dbQuery {
                ClubMemberships.deleteWhere {
                    Op.build { ClubMemberships.clubId eq club.id and (ClubMemberships.userId eq user.id) }
                }
            }
            call.respondRedirect("/clubs")
        }
        get ("/{id}/members/{userId}/role/{role}") {
            val user = getUser() ?: run {
                call.respondRedirect("/account/login?redirect=/clubs")
                return@get
            }
            val club = call.parameters["id"]?.let { id ->
                Database.dbQuery {
                    Clubs
                        .join(ClubMemberships, JoinType.INNER, Clubs.id, ClubMemberships.clubId)
                        .select {
                            Clubs.id eq id and
                            (Clubs.validated eq true) and
                            (ClubMemberships.userId eq user.id) and
                            (ClubMemberships.role eq "admin")
                        }
                        .map { Club(it) }
                        .firstOrNull()
                }
            } ?: run {
                call.respond(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@get
            }
            val membership = call.parameters["userId"]?.let { userId ->
                Database.dbQuery {
                    ClubMemberships
                        .select { ClubMemberships.clubId eq club.id and (ClubMemberships.userId eq userId) }
                        .map { ClubMembership(it) }
                        .singleOrNull()
                }
            } ?: run {
                call.response.status(HttpStatusCode.NotFound)
                call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                return@get
            }
            when (call.parameters["role"]) {
                "admin", "member" -> {
                    Database.dbQuery {
                        ClubMemberships.update({
                            ClubMemberships.clubId eq membership.clubId and
                            (ClubMemberships.userId eq membership.userId)
                        }) {
                            it[ClubMemberships.role] = call.parameters["role"]!!
                        }
                    }
                }
                "remove" -> {
                    Database.dbQuery {
                        ClubMemberships.deleteWhere {
                            Op.build {
                                ClubMemberships.clubId eq membership.clubId and
                                (ClubMemberships.userId eq membership.userId)
                            }
                        }
                    }
                }
                else -> {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Requête invalide")))
                    return@get
                }
            }
            call.respondRedirect("/clubs/${club.id}")
        }
    }
}
