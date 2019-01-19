package com.github.am4dr.rokusho.presenter

import kotlin.reflect.KClass

typealias ItemListViewerFactory = (KClass<out Any>) -> ItemListViewer