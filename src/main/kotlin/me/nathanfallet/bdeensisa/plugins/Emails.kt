package me.nathanfallet.bdeensisa.plugins

import io.ktor.server.application.*
import kotlinx.coroutines.*
import org.apache.commons.mail.HtmlEmail;

object Emails {

    lateinit var host: String
    lateinit var username: String
    lateinit var password: String

    fun sendEmail(destination: String, subject: String, content: String) {
        CoroutineScope(Job()).launch {
            val email = HtmlEmail()
            email.hostName = host
            email.setSmtpPort(587)
            email.setAuthentication(username, password)
            email.setFrom(username)
            email.addTo(destination)
            email.subject = subject
            email.setHtmlMsg(content)
            email.send()
        }
    }

}

fun Application.configureEmails() {
    Emails.host = environment.config.property("email.host").getString()
    Emails.username = environment.config.property("email.username").getString()
    Emails.password = environment.config.property("email.password").getString()
}
