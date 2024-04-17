package com.jpwolfso.privdnsqt.util

import android.content.ContextWrapper
import android.provider.Settings

private const val PRIVATE_DNS_MODE = "private_dns_mode"
private const val PRIVATE_DNS_SPECIFIER = "private_dns_specifier"

var ContextWrapper.dnsProvider: String?
    get() = getGlobalString(PRIVATE_DNS_SPECIFIER)
    set(value) = setGlobalString(PRIVATE_DNS_SPECIFIER, value)

var ContextWrapper.dnsMode: String?
    get() = getGlobalString(PRIVATE_DNS_MODE)
    set(value) = setGlobalString(PRIVATE_DNS_MODE, value)

private fun ContextWrapper.getGlobalString(name: String): String? =
    Settings.Global.getString(contentResolver, name)

private fun ContextWrapper.setGlobalString(name: String, value: String?) {
    Settings.Global.putString(contentResolver, name, value)
}
