package com.telegram.personal.bot.command.impl

import com.pengrad.telegrambot.model.Update
import com.telegram.personal.bot.command.BotCommand
import com.telegram.personal.bot.command.BotCommandHandler
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.math.abs
import kotlin.random.Random

private const val DICTIONARY_URL = "https://studynow.ru/dicta/allwords"
private const val TOTAL_WORDS = 5000

class EnglishWordBotCommandHandler : BotCommandHandler() {
    override fun handle(update: Update): String {
        val doc = Jsoup.connect(DICTIONARY_URL).get()
        val wordsAmount = getCommandParameter(update, 1, "1").toInt()
        val content = getEngToRusRandomPair(doc, wordsAmount).entries.joinToString("\n") { "${it.key} - ${it.value}" }

        return "<b>+$wordsAmount English word</b>\n\n$content"
    }

    private fun getEngToRusRandomPair(doc: Document, wordsAmount: Int): Map<String, String> {
        return 1.rangeTo(wordsAmount)
            .map {
                val wordIndex = abs(Random(System.currentTimeMillis()).nextInt()) % TOTAL_WORDS
                val cells = doc.select("#wordlist tr:nth-child($wordIndex)")[0].getElementsByTag("td")
                val wordInEnglish = cells[1].text()
                val wordInRussian = cells[2].text()

                wordInEnglish to wordInRussian
            }
            .toMap()
    }

    override fun canHandle(command: BotCommand): Boolean = command == BotCommand.DICTIONARY
}