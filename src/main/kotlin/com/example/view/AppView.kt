package com.example.view

import com.example.controller.EmailController
import com.example.datamodels.MessageUi
import javafx.scene.layout.BorderPane
import tornadofx.*

class AppView : View() {
    override val root = BorderPane()
    val controller: EmailController by inject()

    init {
        with(root) {
            top = menubar {
                menu("Действия") {
                    item("Войти") {
                        action {
                            LoginForm().openModal()
                        }
                    }
                    item("Новое сообщение") {
                        action {
                            NewMessageForm().openModal()
                        }
                        disableProperty().bind(controller.isAuthorized.not())
                    }
                }
            }
            center {
                tableview(controller.messagesList) {
                    makeIndexColumn()

                    readonlyColumn("Дата", MessageUi::sentDate)
                    readonlyColumn("Отправитель", MessageUi::senderEmail)
                    readonlyColumn("Содержание", MessageUi::content)

                    smartResize()
                }
            }
        }
    }
}

