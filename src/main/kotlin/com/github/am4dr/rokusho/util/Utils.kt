package com.github.am4dr.rokusho.util

import javafx.beans.property.ListProperty
import javafx.beans.property.SimpleListProperty
import javafx.collections.FXCollections

fun <T> createEmptyListProperty(): ListProperty<T> =
        SimpleListProperty(FXCollections.observableList(mutableListOf<T>()))