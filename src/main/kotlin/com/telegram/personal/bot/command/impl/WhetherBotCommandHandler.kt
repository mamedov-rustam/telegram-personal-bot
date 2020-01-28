package com.telegram.personal.bot.command.impl

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.request.SendPhoto
import com.telegram.personal.bot.command.BotCommand
import com.telegram.personal.bot.command.BotCommandHandler
import java.lang.Exception
import java.net.URL

private const val WHETHER_IMAGE_URL_TEMPLATE =
    "http://mini.s-shot.ru/1024x768/JPEG/1024/Z100/?https://sinoptik.ua/погода-%s"

class WhetherBotCommandHandler : BotCommandHandler() {
    override fun handle(bot: TelegramBot, update: Update) {
        val chatId = update.message().chat().id()
        try {
            val city = getCommandParameter(update, 1, "харьков")
            val imageUrl = WHETHER_IMAGE_URL_TEMPLATE.format(city)
            bot.execute(SendPhoto(chatId, URL(imageUrl).readBytes()))
        } catch (e: Exception) {
            bot.execute(SendMessage(chatId, "Invalid city. Try to enter full city name without spaces."))
        }

    }

    override fun canHandle(command: BotCommand): Boolean = command == BotCommand.WHETHER
}