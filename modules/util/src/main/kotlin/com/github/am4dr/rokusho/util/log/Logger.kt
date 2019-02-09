package com.github.am4dr.rokusho.util.log

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.String.format

inline fun <reified T> getLogger(): Logger = LoggerFactory.getLogger(T::class.java)
fun <T : Any> T.getLogger(): Logger = LoggerFactory.getLogger(this::class.java)

val Any.idHash: String get() = format("%x", System.identityHashCode(this))