package com.github.am4dr.rokusho.core.provider

import kotlin.reflect.KClass

sealed class ProviderDescriptor {

    companion object {
        fun of(string: String): ProviderDescriptor = StringDescriptor(string)
        fun of(clazz: KClass<*>): ProviderDescriptor = FQCNDescriptor(clazz)
    }

    data class StringDescriptor(val value: String) : ProviderDescriptor()
    data class FQCNDescriptor(val value: String) : ProviderDescriptor() {
        constructor(clazz: KClass<*>) : this(clazz.qualifiedName ?: throw IllegalArgumentException())
    }
}
