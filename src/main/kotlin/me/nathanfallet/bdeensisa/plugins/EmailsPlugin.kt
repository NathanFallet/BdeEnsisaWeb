package me.nathanfallet.bdeensisa.plugins

import io.ktor.server.application.*
import kotlinx.coroutines.*
import org.apache.commons.mail.HtmlEmail;

object EmailsPlugin {

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
    EmailsPlugin.host = environment.config.property("email.host").getString()
    EmailsPlugin.username = environment.config.property("email.username").getString()
    EmailsPlugin.password = environment.config.property("email.password").getString()
}
