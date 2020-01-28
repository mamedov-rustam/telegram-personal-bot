package com.telegram.personal.bot

import com.telegram.personal.bot.command.impl.*
import java.util.*
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    if (args.size < 2) {
        System.err.println("At least two params are required: <bot_token> <allowed_usernames>[]")
        exitProcess(-1)
    }

    val botCommandHandlers = listOf(
        StartBotCommandHandler(),
        HelpBotCommandHandler(),
        WhetherBotCommandHandler(),
        EnglishWordBotCommandHandler(),
        CurrencyBotCommandHandler()
    )

    PersonalBot(
        botToken = args[0],
        allowedUsernames = args.drop(1),
        botCommandHandlers = botCommandHandlers
    ).start()

    println("[${Date()}] Bot has started.")
}




