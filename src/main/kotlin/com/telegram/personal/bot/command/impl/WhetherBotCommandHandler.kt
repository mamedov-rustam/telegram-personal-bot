package com.telegram.personal.bot.command.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.request.SendPhoto
import com.telegram.personal.bot.command.BotCommand
import com.telegram.personal.bot.command.BotCommandHandler
import java.io.DataOutputStream
import java.io.InputStream
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection


private const val WHETHER_WEBSITE_URL_TEMPLATE = "https://sinoptik.ua/погода-%s"

class WhetherBotCommandHandler : BotCommandHandler() {
    override fun handle(bot: TelegramBot, update: Update) {
        val chatId = update.message().chat().id()
        try {
            val city = getCommandParameter(update, 1, "харьков")
            bot.execute(SendPhoto(chatId, getWhetherImageFromScreenshotmachine(city).readBytes()))
        } catch (e: Exception) {
            e.printStackTrace()
            bot.execute(SendMessage(chatId, "Invalid city. Try to enter full city name without spaces."))
        }
    }

    override fun canHandle(command: BotCommand): Boolean = command == BotCommand.WHETHER

    private fun getWhetherImageFromScreenshotmachine(city: String): InputStream {
        val baseUrl = "https://www.screenshotmachine.com"

        // Create screenshot
        val postRequest = createRequest("$baseUrl/capture.php", "POST")
        val params = mapOf(
            "url" to URLEncoder.encode(WHETHER_WEBSITE_URL_TEMPLATE.format(city), "UTF-8"),
            "device" to "desktop", "format" to "png",
            "width" to "1024", "height" to "768", "zoom" to "100",
            "timeout" to "2000", "cacheLimit" to "0"
        )
        val formParameters = params.entries.joinToString("&") { "${it.key}=${it.value}" }
        postRequest.doOutput = true
        DataOutputStream(postRequest.outputStream).use { wr ->
            wr.writeBytes(formParameters)
            wr.flush()
        }

        // Extract access cookie
        val scmResultCookie = postRequest.getHeaderField("set-cookie")
        val imageSuffix = ObjectMapper().readTree(postRequest.inputStream).path("link").textValue()

        // Grab screenshot
        val getRequest = createRequest("$baseUrl/$imageSuffix")
        getRequest.setRequestProperty("cookie", scmResultCookie)

        return getRequest.inputStream
    }

    private fun createRequest(url: String, method: String = "GET"): HttpsURLConnection {
        val request: HttpsURLConnection = URL(url).openConnection() as HttpsURLConnection
        request.requestMethod = method
        request.setRequestProperty(
            "user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36"
        )

        return request
    }
}