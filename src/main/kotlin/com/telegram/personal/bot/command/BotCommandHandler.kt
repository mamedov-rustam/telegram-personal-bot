package com.telegram.personal.bot.command

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage

abstract class BotCommandHandler {

    open fun handle(bot: TelegramBot, update: Update) {
        val chatId = update.message().chat().id()
        val messageText = handle(update)
        bot.execute(SendMessage(chatId, messageText).parseMode(ParseMode.HTML))
    }

    protected open fun handle(update: Update): String = "//ToDo: implement command"

    abstract fun canHandle(command: BotCommand): Boolean

    fun getCommandParameter(update: Update, index: Int, default: String): String = update.message().text()
        .split(" ")
        .let { if (it.size < index + 1) default else it[index] }
        .trim()
}