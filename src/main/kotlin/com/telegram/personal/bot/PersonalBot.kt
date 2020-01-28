package com.telegram.personal.bot

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import com.telegram.personal.bot.command.BotCommand
import com.telegram.personal.bot.command.BotCommandHandler

class PersonalBot(
    private val botToken: String,
    private val allowedUsernames: List<String>,
    private val botCommandHandlers: List<BotCommandHandler>
) {
    fun start() {
        val bot = TelegramBot(botToken)
        bot.setUpdatesListener { updates ->
            if (updates == null || updates.isEmpty()) {
                UpdatesListener.CONFIRMED_UPDATES_ALL
            }

            updates.forEach { update ->
                if (update.message() == null) {
                    return@forEach
                }

                val chatId = update.message().chat().id()
                val username = update.message().from().username()
                if (!allowedUsernames.contains(username)) {
                    bot.execute(SendMessage(chatId, "Sorry, you are not allowed to use this bot."));
                    return@forEach
                }

                var messageText = ""
                var commandString = update.message().text().split(" ")[0]
                if (!commandString.matches(Regex("/.+")) || !BotCommand.isValid(commandString)) {
                    messageText = "<i>Unknown command. Please check available commands below.</i>\n\n"
                    bot.execute(SendMessage(chatId, messageText).parseMode(ParseMode.HTML))
                    commandString = BotCommand.HELP.value
                }

                val command = BotCommand.fromString(commandString)
                botCommandHandlers.find { it.canHandle(command) }.let { it?.handle(bot, update) }
            }

            UpdatesListener.CONFIRMED_UPDATES_ALL
        }
    }
}