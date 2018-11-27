package com.baulsupp.cooee.providers

interface Provider {
    fun url(command: String, args: List<String>): RedirectResult

    fun targets(command: String, args: List<String>): List<Target>

    fun matches(command: String): Boolean
}
