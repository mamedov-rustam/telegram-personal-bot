package com.telegram.personal.bot.command.impl

import com.pengrad.telegrambot.model.Update
import com.telegram.personal.bot.command.BotCommand
import com.telegram.personal.bot.command.BotCommandHandler

class StartBotCommandHandler : BotCommandHandler() {
    override fun handle(update: Update): String = """
        Bot has started and ready to handle your commands.
    """.trimIndent()

    override fun canHandle(command: BotCommand): Boolean = command == BotCommand.START
}