package com.example

import com.example.view.AppView
import javafx.stage.Stage
import tornadofx.*

class MyApp: App(AppView::class, Styles::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        stage.width = 1280.0
        stage.height = 720.0
    }
}