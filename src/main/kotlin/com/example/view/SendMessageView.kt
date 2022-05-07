package com.example.view

import com.example.controller.EmailController
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class SendMessageView: View("SendMessageView") {
    override val root = vbox()
    private val userObservable = SimpleStringProperty()
    private val themeObservable = SimpleStringProperty()
    private val messageObservable = SimpleStringProperty()
    private val attachmentsObservable = mutableListOf<File>().toProperty()
    private val controller: EmailController by inject()

    init {
        with(root) {
            useMaxWidth = true
            hbox {
                useMaxWidth = true
                text("Получатель")
                textfield(userObservable)
            }
            hbox {
                useMaxWidth = true
                text("Тема")
                textfield(themeObservable) {

                }
            }
            hbox {
                text("Сообщение")
                textfield(messageObservable) {
                }
            }
            hbox {
                text("Вложения")
                textfield() {
                    attachmentsObservable.addListener { observable, oldValue, newValue ->
                        text = newValue.joinToString(separator = ", ") { it.name }
                    }
                }
                button("...") {
                    onLeftClick {
                        attachmentsObservable.value = chooseFile(filters = emptyArray()).toMutableList()
                    }
                }

            }
            button("Отправить") {
                useMaxSize = true

                onLeftClick {
                    runAsyncWithProgress {
                        controller.sendMessage(
                            email = userObservable.value,
                            subject = themeObservable.value,
                            content = messageObservable.value,
                            attachments = attachmentsObservable.value
                        )
                    }
                }
            }
        }
    }

}