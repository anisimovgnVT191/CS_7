package com.example.view

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import tornadofx.*

class SendMessageView: View("SendMessageView") {
    override val root = vbox()
    private val userObservable = SimpleStringProperty()
    private val themeObservable = SimpleStringProperty()
    private val messageObservable = SimpleStringProperty()
    private val attachmentsObservable = SimpleStringProperty()

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
                    alignment = Pos.CENTER_RIGHT
                }
            }
            hbox {
                text("Сообщение")
                textfield(messageObservable) {
                    alignment = Pos.CENTER_RIGHT
                }
            }
            hbox {
                text("Вложения")
                textfield()
                button("...")
            }
            button("Отправить") {
                useMaxSize = true
                this.cancelButtonProperty()
            }
        }
    }

}