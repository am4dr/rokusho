package com.github.am4dr.rokusho.app.savedata.yaml

import com.github.am4dr.rokusho.app.datastore.yaml.deserialize
import com.github.am4dr.rokusho.app.datastore.yaml.detectVersion
import com.github.am4dr.rokusho.app.savedata.SaveData
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
                    Arguments.of(Versions.V1, mapOf("version" to "1")),
                    Arguments.of(Versions.UNKNOWN, mapOf("version" to "future version")))
        }
        @ParameterizedTest
        @MethodSource
        fun versionDetection(expected: Versions?, map: Map<*, *>) {
            assertEquals(expected, detectVersion(map))
        }
    }
}
