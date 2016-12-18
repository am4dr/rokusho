package com.github.am4dr.image.tagger.util

import javafx.beans.property.SimpleListProperty
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UtilsTest {
    @Test
    @DisplayName("ListProperty generated by no args SimpleListProperty() is not modifiable")
    fun modifySimpleListProperty() {
        assertThrows<UnsupportedOperationException>(UnsupportedOperationException::class.java) {
            SimpleListProperty<Any>().add("test")
        }
    }
    @Test
    @DisplayName("ListProperty generated by createEmptyListProperty() is modifiable")
    fun modifyEmptyListProperty() {
        createEmptyListProperty<Any>().add("test")
    }
}