package com.example.view

import com.example.controller.EmailController
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class TopControlView : View() {
    override val root = vbox()
    private val controller: EmailController by inject()
    private val email = SimpleStringProperty()
    private val password = SimpleStringProperty()
    init {
        with(root) {
            hbox {
                button("Открыть")
                button("Удалить")
                text("Логин")
                textfield(email)
                text("Пароль")
                passwordfield(password)
                button("Войти") {
                    setOnMouseClicked {
                        runAsyncWithProgress {
                            controller.login(email.value, password.value)
                        }.setOnSucceeded {
                            if (controller.isAuthorized.value) {
                                runAsyncWithProgress {
                                    controller.readMessages()
                                }
                            }
                        }
                    }
                    disableProperty().bind(email.isEmpty.or(password.isEmpty))
                }
            }
        }
    }
}

