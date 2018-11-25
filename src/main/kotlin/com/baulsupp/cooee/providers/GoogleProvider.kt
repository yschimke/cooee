package com.baulsupp.cooee.providers

class GoogleProvider: Provider {
  override fun targets(command: String, args: List<String>): List<Target> = listOf()

  override fun url(command: String, args: List<String>): RedirectResult =
        RedirectResult("https://google.com/?q=$args")

    override fun matches(command: String): Boolean = command == "g" || command == "google"
}
