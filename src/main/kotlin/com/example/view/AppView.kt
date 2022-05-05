package com.example.view

import com.example.controller.EmailController
import com.example.datamodels.MessageUi
import javafx.scene.Parent
import javafx.scene.layout.BorderPane
import tornadofx.*
import javax.mail.Message

class AppView : View() {
    override val root = BorderPane()
    val header: TopControlView by inject()
    val newMessageView: SendMessageView by inject()
    val controller: EmailController by inject()

    init {
        with(root) {
            top = header.root
            center {
                tableview(controller.messagesList) {
                    makeIndexColumn()

                    readonlyColumn("Дата", MessageUi::sentDate)
                    readonlyColumn("Отправитель", MessageUi::senderEmail)
                    readonlyColumn("Содержание", MessageUi::content)

                    smartResize()
                }
            }
            bottom {
                hbox {
                    textarea {
                        isEditable = false
                    }
                    add(newMessageView.root)
                }
            }
        }
    }
}

