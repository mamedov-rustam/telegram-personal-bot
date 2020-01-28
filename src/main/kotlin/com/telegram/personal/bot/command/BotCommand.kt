package com.telegram.personal.bot.command

enum class BotCommand(val value: String, val description: String) {
    START("/start", "Start bot"),
    HELP("/help", "Get help manual"),
    WHETHER("/whether", "Get whether (Params: city_in_russian_lang[default=харьков])"),
    CURRENCY("/cur", "Show actual currency rates"),
    DICTIONARY("/dict", "Get word(s) from list with 5000 most popular english words (Params: words_amount[default=1])");

    companion object {
        fun isValid(command: String): Boolean = values().any { it.value == command }
        fun fromString(command: String): BotCommand = values().find { it.value == command }!!
    }
}