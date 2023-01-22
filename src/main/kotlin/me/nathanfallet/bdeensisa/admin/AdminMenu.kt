package me.nathanfallet.bdeensisa.admin

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

fun Route.adminMenu() {
    route("/menu") {
        get {
            getUser()?.let { user ->
                if (user.hasPermission("admin.menu.view")) {
                    val menu = Database.dbQuery {
                        MenuItems.selectAll().orderBy(MenuItems.position).map { MenuItem(it) }
                    }
                    call.respond(FreeMarkerContent("admin/menu/list.ftl", mapOf(
                        "title" to "Menu",
                        "menuitems" to menu,
                        "menu" to MenuItems.fetchAdmin(user)
                    )))
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/menu")
            }
        }
        get ("/new") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.menu.new")) {
                    val parents = Database.dbQuery {
                        MenuItems.select {
                            MenuItems.parent eq null
                        }.map { MenuItem(it) }
                    }
                    call.respond(FreeMarkerContent("admin/menu/form.ftl", mapOf(
                        "title" to "Nouvel élément de menu",
                        "menu" to MenuItems.fetchAdmin(user),
                        "parents" to parents
                    )))
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/menu")
            }
        }
        post ("/new") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.menu.new")) {
                    val params = call.receiveParameters()
                    val title = params["title"]
                    val url = params["url"]
                    val position = params["position"]
                    val parent = params["parent"]
                    if (title != null && url != null && position != null) {
                        Database.dbQuery {
                            MenuItems.insert {
                                it[MenuItems.id] = MenuItems.generateId()
                                it[MenuItems.title] = title
                                it[MenuItems.url] = url
                                it[MenuItems.position] = position.toInt()
                                it[MenuItems.parent] = parent?.let { if (it == "") null else it }
                            }
                        }
                        call.respondRedirect("/admin/menu")
                    } else {
                        call.response.status(HttpStatusCode.BadRequest)
                        call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                    }
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/menu")
            }
        }
        get ("/{id}") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.menu.edit")) {
                    call.parameters["id"]?.let { id ->
                        Database.dbQuery {
                            MenuItems.select { MenuItems.id eq id }.map { MenuItem(it) }.singleOrNull()
                        }?.let { item ->
                            val parents = Database.dbQuery {
                                MenuItems.select {
                                    MenuItems.parent eq null
                                }.map { MenuItem(it) }
                            }
                            call.respond(FreeMarkerContent("admin/menu/form.ftl", mapOf(
                                "title" to "Modifier une page",
                                "item" to item,
                                "menu" to MenuItems.fetchAdmin(user),
                                "parents" to parents
                            )))
                        } ?: run {
                            call.response.status(HttpStatusCode.NotFound)
                            call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                        }
                    } ?: run {
                        call.response.status(HttpStatusCode.NotFound)
                        call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                    }
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/menu")
            }
        }
        post ("/{id}") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.menu.edit")) {
                    call.parameters["id"]?.let { id ->
                        Database.dbQuery {
                            MenuItems.select { MenuItems.id eq id }.map { MenuItem(it) }.singleOrNull()
                        }?.let { item ->
                            val params = call.receiveParameters()
                            val title = params["title"]
                            val url = params["url"]
                            val position = params["position"]
                            val parent = params["parent"]
                            if (title != null && url != null && position != null) {
                                Database.dbQuery {
                                    MenuItems.update({ MenuItems.id eq item.id }) {
                                        it[MenuItems.title] = title
                                        it[MenuItems.url] = url
                                        it[MenuItems.position] = position.toInt()
                                        it[MenuItems.parent] = parent?.let { if (it == "") null else it }
                                    }
                                }
                                call.respondRedirect("/admin/menu")
                            } else {
                                call.response.status(HttpStatusCode.BadRequest)
                                call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Requête invalide")))
                            }
                        } ?: run {
                            call.response.status(HttpStatusCode.NotFound)
                            call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                        }
                    } ?: run {
                        call.response.status(HttpStatusCode.NotFound)
                        call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                    }
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/menu")
            }
        }
        get ("/{id}/delete") {
            getUser()?.let { user ->
                if (user.hasPermission("admin.menu.delete")) {
                    call.parameters["id"]?.let { id ->
                        Database.dbQuery {
                            MenuItems.deleteWhere {
                                Op.build { MenuItems.id eq id }
                            }
                        }
                        call.respondRedirect("/admin/menu")
                    } ?: run {
                        call.response.status(HttpStatusCode.NotFound)
                        call.respond(FreeMarkerContent("public/error.ftl", mapOf("title" to "Page non trouvée")))
                    }
                } else {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respond(FreeMarkerContent("admin/error.ftl", mapOf("title" to "Accès non autorisé")))
                }
            } ?: run {
                call.respondRedirect("/account/login?redirect=/admin/menu")
            }
        }
    }
}
