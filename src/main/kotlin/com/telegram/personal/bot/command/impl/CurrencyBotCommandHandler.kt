package com.telegram.personal.bot.command.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.pengrad.telegrambot.model.Update
import com.telegram.personal.bot.command.BotCommand
import com.telegram.personal.bot.command.BotCommandHandler
import java.lang.StringBuilder
import java.math.RoundingMode
import java.net.URL

private const val BTC_PRICE_LINK = "https://api.coinmarketcap.com/v1/ticker/bitcoin?convert=USD"
private const val ETH_PRICE_LINK = "https://api.coinmarketcap.com/v1/ticker/ethereum?convert=USD"
private const val PRIVAT_BANK_COURSE = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5"
private val excludedCurrencies = listOf("BTC")

class CurrencyBotCommandHandler : BotCommandHandler() {
    override fun handle(update: Update): String {
        return "${getPrivatBankInfo(update)}\n\n${getCryptoInfo(update)}"
    }

    private fun getCryptoInfo(update: Update): String {
        val res = StringBuilder("<b>Cryptocurrency prices</b>\n\n")

        val btcInfo = getCryptocurrencyInfo(BTC_PRICE_LINK)
        res.append("1 BTC = ${btcInfo.price}$ (${btcInfo.dailyChanges})\n")

        val ethInfo = getCryptocurrencyInfo(ETH_PRICE_LINK)
        res.append("1 ETH = ${ethInfo.price}$ (${ethInfo.dailyChanges})")

        return res.toString()
    }

    private fun getCryptocurrencyInfo(link: String): CryptocurrencyInfo {
        val resp = ObjectMapper().readTree(URL(link)).path(0)
        return CryptocurrencyInfo(
            price = resp.path("price_usd").textValue().split(".")[0],
            dailyChanges = resp.path("percent_change_24h").textValue().let { if (it.startsWith("-")) "$it%" else "+$it%" }
        )
    }

    private fun getPrivatBankInfo(update: Update): String {
        val doc = ObjectMapper().readTree(URL(PRIVAT_BANK_COURSE))
        val content = doc
            .filter { !excludedCurrencies.contains(it.path("ccy").asText()) }
            .joinToString(separator = "\n") {
                val currency = it.path("ccy").asText()
                val buyPrice = it.path("buy").asText().toBigDecimal().setScale(2, RoundingMode.DOWN)
                val sellPrice = it.path("sale").asText().toBigDecimal().setScale(2, RoundingMode.DOWN)

                "$currency -> $buyPrice / $sellPrice"
            }

        return "<b>Privat24 currencies</b>\n\n$content"
    }

    override fun canHandle(command: BotCommand): Boolean = command == BotCommand.CURRENCY
}

data class CryptocurrencyInfo(val price: String, val dailyChanges: String)