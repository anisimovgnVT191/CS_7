package com.example.datamodels

import javax.mail.Message

data class MessageUi(
    val sentDate: String,
    val senderEmail: String,
    val content: String,
    val originalMessageRef: Message
)