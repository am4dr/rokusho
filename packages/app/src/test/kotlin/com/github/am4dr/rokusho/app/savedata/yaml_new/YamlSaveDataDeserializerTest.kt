package com.github.am4dr.rokusho.app.savedata.yaml_new

import com.github.am4dr.rokusho.app.savedata.store.yaml_new.SaveData
import com.github.am4dr.rokusho.app.savedata.store.yaml_new.deserialize
import com.github.am4dr.rokusho.app.savedata.store.yaml_new.detectVersion
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class YamlSaveDataDeserializerTest {

    @Test
    fun emptyStringParsedIntoEmptySaveData() {
        val data = deserialize("").result
        assertEquals(SaveData.EMPTY, data)
    }

    @Nested
    class VersionDetection {
        companion object {
            @JvmStatic
            fun versionDetection(): Stream<Arguments> = Stream.of(
                    Arguments.of(SaveData.Version.VERSION_1, mapOf("version" to "1")),
                    Arguments.of(SaveData.Version.UNKNOWN, mapOf("version" to "future version")))
        }
        @ParameterizedTest
        @MethodSource
        fun versionDetection(expected: SaveData.Version?, map: Map<*, *>) {
            assertEquals(expected, detectVersion(map))
        }
    }
}
