package com.example.view

import com.example.controller.EmailController
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.scene.control.Alert
import tornadofx.*

class LoginForm : View() {
    private val controller: EmailController by inject()
    private val emailProperty = SimpleStringProperty()
    private val passwordProperty = SimpleStringProperty()

    override val root = Form()

    init {
        with(root) {
            title = "Вход"

            fieldset {
                field("Логин", Orientation.VERTICAL) {
                    textfield {
                        bind(emailProperty)
                    }
                }

                field("Пароль", Orientation.VERTICAL) {
                    passwordfield {
                        bind(passwordProperty)
                    }
                }

                buttonbar {
                    button("Войти") {
                        disableProperty().bind(emailProperty.isEmpty.or(passwordProperty.isEmpty))
                        onLeftClick {
                            runAsyncWithProgress {
                                controller.login(emailProperty.value, passwordProperty.value)
                            }.ui {
                                if (controller.isAuthorized.value) {
                                    close()
                                } else {
                                    alert(
                                        type = Alert.AlertType.INFORMATION,
                                        header = "Ошибка авторизации",
                                        content = """Не удалось войти в аккаунт.
                                            |Проверьте правильность логина/пароля и повторите попытку.
                                        """.trimMargin()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}