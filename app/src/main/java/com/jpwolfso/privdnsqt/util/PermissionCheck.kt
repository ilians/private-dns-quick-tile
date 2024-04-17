package com.jpwolfso.privdnsqt.util

import android.Manifest
import android.content.ContextWrapper
import android.content.pm.PackageManager

fun ContextWrapper.hasPermission(): Boolean =
    checkCallingOrSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
