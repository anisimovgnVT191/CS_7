package com.example

import com.example.controller.EmailController
import com.example.view.AppView
import com.example.view.MainView
import com.example.view.SendMessageView
import javafx.stage.Stage
import tornadofx.*

class MyApp: App(AppView::class, Styles::class) {
    private val controller: EmailController by inject()

    override fun start(stage: Stage) {
        super.start(stage)
        stage.width = 1280.0
        stage.height = 720.0
        stage.isResizable = false

        stage.setOnCloseRequest {
            controller.freeResources()
        }
    }


}