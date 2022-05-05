package com.example.controller

import com.example.datamodels.MessageUi
import com.example.utils.MessageContentReader
import javafx.beans.property.BooleanProperty
import tornadofx.*
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.search.AndTerm
import javax.mail.search.FlagTerm
import javax.mail.search.SearchTerm

class EmailController: Controller() {
    private val pop3Store: Store
    private var inbox: Folder? = null
    private val searchTerm: SearchTerm
    private lateinit var sessionAuth: Session

    val messagesList = mutableListOf<MessageUi>().asObservable()
    val isAuthorized = false.toProperty()

    private lateinit var email: String
    private lateinit var password: String

    init {
        val properties = System.getProperties()
        val session = Session.getDefaultInstance(properties)
        pop3Store = session.getStore(POP3S_PROTOCOL)

        val unseenFlagTerm = FlagTerm(Flags(Flags.Flag.SEEN), false)
        val lastTwoDaysTerm = FlagTerm(Flags(Flags.Flag.RECENT), true)
        searchTerm = AndTerm(unseenFlagTerm, lastTwoDaysTerm)

    }

    fun login(email: String, password: String) {
        try {
            pop3Store.connect(DEFAULT_HOST, DEFAULT_PORT, email, password)
            inbox = pop3Store.getFolder(INBOX_FOLDER)

            isAuthorized.set(true)
            this.email = email
            this.password = password
            sessionAuth = createSessionAuth(email, password)
        } catch (e: AuthenticationFailedException) {
            println(e.message)
        }
    }

    fun freeResources() {
        pop3Store.close()
        inbox?.close()
    }

    fun readMessages() {
        inbox?.open(Folder.READ_ONLY)

        val messages = inbox?.getMessages(1, 30) ?: emptyArray()
        println(messages.size)
        val uiMessages = messages.map {
            MessageUi(
                it.sentDate.toString(),
                it.from[0].toString(),
                MessageContentReader.readContent(it),
                it
            )
        }
        println(uiMessages)
        messagesList.addAll(uiMessages)
    }

    fun sendMessage(email: String, subject: String, content: String) {
        println("$email $subject $content")
        val message = MimeMessage(sessionAuth).apply {
            setFrom(InternetAddress(this@EmailController.email))
            addRecipient(Message.RecipientType.TO, InternetAddress(email))
            setSubject(subject, )
            setText(content)
            sentDate = Date()
        }
        sessionAuth.transport.apply {
            connect()
            sendMessage(message, arrayOf(InternetAddress(email)))
            close()
        }
    }

    private fun createSessionAuth(email: String, password: String): Session {
        val props = Properties().apply {
            put(MAIL_SMTP_AUTH, "true")
            put(MAIL_SMTP_STARTTLS_ENABLE, "true")
            put(MAIL_SMTP_HOST, SMTP_HOST)
            put(MAIL_SMTP_PORT, SMTP_PORT)
            put(MAIL_SMTP_SOCKET_FACTORY_CLASS, SMTP_SOCKET_FACTORY)
        }
        return Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(email, password)
            }
        })
    }
    companion object {
        private const val DEFAULT_HOST = "pop.gmail.com"
        private const val DEFAULT_PORT = 995
        private const val POP3S_PROTOCOL = "pop3s"
        private const val INBOX_FOLDER = "Inbox"

        private const val MAIL_SMTP_AUTH = "mail.smtp.auth"
        private const val MAIL_SMTP_STARTTLS_ENABLE = "mail.starttls.enable"
        private const val MAIL_SMTP_PORT = "mail.smtp.port"
        private const val MAIL_SMTP_HOST = "mail.smtp.host"
        private const val MAIL_SMTP_SOCKET_FACTORY_CLASS = "mail.smtp.socketFactory.class"

        private const val SMTP_HOST = "smtp.gmail.com"
        private const val SMTP_PORT = "465"
        private const val SMTP_SOCKET_FACTORY = "javax.net.ssl.SSLSocketFactory"
    }
}