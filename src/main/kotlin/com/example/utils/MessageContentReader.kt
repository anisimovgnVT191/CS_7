package com.example.utils

import org.jsoup.Jsoup
import javax.mail.Message
import javax.mail.internet.MimeMultipart

object MessageContentReader {
    private const val TEXT_PLAIN_MIME = "text/plain"
    private const val TEXT_HTML_MIME = "text/html"
    private const val MULTIPART_MIME = "multipart/*"
    fun readContent(message: Message): String {
        var result = ""

        if (message.isMimeType(TEXT_PLAIN_MIME)) {
            result += message.content.toString()
        } else if (message.isMimeType(MULTIPART_MIME)) {
            result += getTextFromMultipart(message.content as MimeMultipart)
        }

        return result
    }

    private fun getTextFromMultipart(mimeMultipart: MimeMultipart): String {
        var result = ""

        if (mimeMultipart.count <= 1) return result

        for (i in 0..mimeMultipart.count) {
            val bodyPart = mimeMultipart.getBodyPart(i)

            if (bodyPart.isMimeType(TEXT_PLAIN_MIME)) {
                result += "$\\n ${bodyPart.content}"
                break
            } else if (bodyPart.isMimeType(TEXT_HTML_MIME)) {
                val html = bodyPart.content.toString()
                result += "$\\n +${Jsoup.parse(html).text()}"
            } else if (bodyPart.content is MimeMultipart) {
                result += getTextFromMultipart(bodyPart.content as MimeMultipart)
            }
        }

        return result
    }
}