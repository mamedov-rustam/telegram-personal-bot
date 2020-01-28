package com.telegram.personal.bot.command.impl

import com.pengrad.telegrambot.model.Update
import com.telegram.personal.bot.command.BotCommand
import com.telegram.personal.bot.command.BotCommandHandler

class HelpBotCommandHandler : BotCommandHandler() {
    override fun handle(update: Update): String {
        val content = BotCommand.values().joinToString("\n") { "${it.value} - ${it.description}" }
        return "<b>Supported commands</b>\n\n$content"
    }

    override fun canHandle(command: BotCommand): Boolean = command == BotCommand.HELP
}