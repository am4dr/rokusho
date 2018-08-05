package com.github.am4dr.rokusho.core.provider

import kotlin.reflect.KClass

sealed class DefaultProviderDescriptors : ProviderDescriptor {

    companion object {
        fun of(string: String): DefaultProviderDescriptors = StringDescriptor(string)
        fun of(clazz: KClass<*>): DefaultProviderDescriptors = FQCNDescriptor(clazz)
    }

    data class StringDescriptor(override val value: String) : DefaultProviderDescriptors()
    data class FQCNDescriptor(override val value: String) : DefaultProviderDescriptors() {
        constructor(clazz: KClass<*>) : this(clazz.qualifiedName ?: throw IllegalArgumentException())
    }
}
