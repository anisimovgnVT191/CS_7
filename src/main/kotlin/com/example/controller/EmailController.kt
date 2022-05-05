package com.example.controller

import com.example.datamodels.MessageUi
import com.example.utils.MessageContentReader
import javafx.beans.property.BooleanProperty
import tornadofx.*
import javax.mail.*
import javax.mail.search.AndTerm
import javax.mail.search.FlagTerm
import javax.mail.search.SearchTerm

class EmailController: Controller() {
    private val pop3Store: Store
    private var inbox: Folder? = null
    private val searchTerm: SearchTerm

    val messagesList = mutableListOf<MessageUi>().asObservable()
    val isAuthorized = false.toProperty()

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
               MessageContentReader.readContent(it)
            )
        }
        println(uiMessages)
        messagesList.addAll(uiMessages)
    }

    companion object {
        private const val DEFAULT_HOST = "pop.gmail.com"
        private const val DEFAULT_PORT = 995
        private const val POP3S_PROTOCOL = "pop3s"
        private const val INBOX_FOLDER = "Inbox"
    }
}