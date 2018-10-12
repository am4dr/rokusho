package com.github.am4dr.rokusho.core.metadata

import com.github.am4dr.rokusho.core.datastore.DataStore
import com.github.am4dr.rokusho.core.util.DataObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DefaultMetaDataRepositoryImplTest {

    class AddTests {

        lateinit var sut: DefaultMetaDataRepositoryImpl
        lateinit var store: LoggingDataStore

        @BeforeEach
        fun init() {
            store = LoggingDataStore()
            sut = DefaultMetaDataRepositoryImpl(setOf(), setOf(), store)
        }

        @Test
        fun addBaseTag() {
            val tag = BaseTag("tag", mapOf())
            sut.add(tag)
            assertEquals(1, store.saved.size)
            assertEquals(tag.name, store.saved.last().getTags().first().name)

            sut.add(tag)
            assertEquals(1, store.saved.size)
        }

        @Test
        fun addRecord() {
            val record = Record("key")
            sut.add(record)
            assertEquals(1, store.saved.size)
            assertEquals(record.key, store.saved.last().getRecordKeys().first())

            sut.add(record)
            assertEquals(1, store.saved.size)

            val updatedRecord = record.copy(tags = setOf(PatchedTag(BaseTag("tag", mapOf()), DataObject())))
            sut.add(updatedRecord)
            assertEquals(2, store.saved.size)
            assertEquals(1, store.saved.last().getRecords().size)
        }
    }
    class RemoveTests {

        val baseTag = BaseTag("tag", mapOf())
        val record = Record("key", setOf(PatchedTag(baseTag, DataObject())))
        lateinit var sut: DefaultMetaDataRepositoryImpl
        lateinit var store: LoggingDataStore

        @BeforeEach
        fun init() {
            store = LoggingDataStore()
            sut = DefaultMetaDataRepositoryImpl(
                    setOf(baseTag),
                    setOf(record),
                    store)
        }

        @Test
        fun removeBaseTag() {
            sut.remove(baseTag.name)
            assertEquals(1, store.saved.size)
            assertEquals(0, store.saved.last().getTags().size)

            sut.remove(baseTag.name)
            assertEquals(1, store.saved.size)
        }

        @Test
        fun removeRecord() {
            sut.remove(record.key)
            assertEquals(1, store.saved.size)
            assertEquals(0, store.saved.last().getRecords().size)

            sut.remove(record.key)
            assertEquals(1, store.saved.size)
        }
    }

    class LoggingDataStore : DataStore<MetaDataRepository> {

        val saved: MutableList<MetaDataRepository> = mutableListOf()

        override fun save(data: MetaDataRepository) {
            saved.add(data)
        }
        override fun load(): MetaDataRepository? {
            throw UnsupportedOperationException("load() is not supported")
        }
    }
}