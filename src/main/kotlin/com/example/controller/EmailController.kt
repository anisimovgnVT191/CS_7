package com.example.controller

import com.example.datamodels.MessageUi
import com.example.utils.MessageContentReader
import tornadofx.*
import java.io.File
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class EmailController : Controller() {
    private val pop3Store: Store
    private var inbox: Folder? = null
    private lateinit var sessionAuth: Session

    val messagesList = mutableListOf<MessageUi>().asObservable()
    val isAuthorized = false.toProperty()
    val shouldFetchMessages = false.toProperty()

    private lateinit var email: String
    private lateinit var password: String

    init {
        val properties = System.getProperties()
        val session = Session.getDefaultInstance(properties)
        pop3Store = session.getStore(POP3S_PROTOCOL)
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

    fun readMessages() {
        inbox?.open(Folder.READ_ONLY) ?: return
        val messageCount = inbox?.messageCount ?: return

        val messages = inbox?.getMessages(messageCount - MAX_MESSAGE_FETCH_COUNT + 1, messageCount) ?: emptyArray()
        println(messages.size)
        val uiMessages = messages.map {
            MessageUi(
                it.sentDate.toString(),
                (it.from[0] as InternetAddress).personal,
                MessageContentReader.readContent(it),
                it
            )
        }

        messagesList.addAll(uiMessages)
    }

    fun sendMessage(email: String, subject: String, content: String, attachments: List<File>) {
        println("$email $subject $content")
        val message = MimeMessage(sessionAuth).apply {
            setFrom(InternetAddress(this@EmailController.email))
            addRecipient(Message.RecipientType.TO, InternetAddress(email))
            setSubject(subject)
            sentDate = Date()
        }

        val multipart = MimeMultipart().apply { addBodyPart(MimeBodyPart().apply { setText(content) }) }

        if (attachments.isNotEmpty()) {
            attachments.forEach { file ->
                multipart.addBodyPart(createFileBodyPart(file))
            }
        }

        message.setContent(multipart)

        sessionAuth.transport.apply {
            connect()
            sendMessage(message, arrayOf(InternetAddress(email)))
            close()
        }
    }

    private fun createFileBodyPart(file: File): MimeBodyPart {
        return MimeBodyPart().apply {
            dataHandler = DataHandler(FileDataSource(file))
            fileName = file.absolutePath
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

        private const val MAX_MESSAGE_FETCH_COUNT = 30
    }
}