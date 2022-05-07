package com.example.view

import com.example.controller.EmailController
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.scene.layout.Priority
import tornadofx.*
import java.io.File

class NewMessageForm : View() {
    private val controller: EmailController by inject()

    private val recipientProperty = SimpleStringProperty()
    private val themeProperty = SimpleStringProperty("")
    private val messageProperty = SimpleStringProperty()
    private val attachmentsProperty = mutableListOf<File>().toProperty()

    override val root = Form()

    init {
        with(root) {
            title = "Новое сообшение"
            fieldset {
                field("Получатель", Orientation.HORIZONTAL) {
                    textfield().bind(recipientProperty)
                }

                field("Тема", Orientation.HORIZONTAL) {
                    textfield().bind(themeProperty)
                }

                field("Сообщение", Orientation.VERTICAL) {
                    textarea {
                        prefRowCount = 10
                        vgrow = Priority.ALWAYS

                        bind(messageProperty)
                    }
                }

                field("Вложения", Orientation.HORIZONTAL) {
                    textfield()
                    button("...") {
                        onLeftClick {
                            attachmentsProperty.value = chooseFile(filters = emptyArray()).toMutableList()
                        }
                    }
                }

                button("Отправить") {
                    disableProperty().bind(recipientProperty.isEmpty.or(messageProperty.isEmpty))

                    onLeftClick {
                        runAsyncWithProgress {
                            controller.sendMessage(
                                email = recipientProperty.value,
                                subject = themeProperty.value,
                                content = messageProperty.value,
                                attachments = attachmentsProperty.value
                            )
                        }.ui {

                        }
                    }
                }
            }
        }
    }
}