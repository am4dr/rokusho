package com.github.am4dr.rokusho.core

import com.github.am4dr.image.tagger.core.TagType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class StubTag(override val id: String) : Tag {
    override val type: TagType
        get() = throw UnsupportedOperationException()
    override val data: Map<String, Any>
        get() = throw UnsupportedOperationException()
}
class StubLibraryItemMetaData(override val id: String) : LibraryItemMetaData {
    override val tags: List<Tag>
        get() = throw UnsupportedOperationException()
}
class SimpleLibraryTest {
    @Test
    fun addAndRemove() {
        val lib = SimpleLibrary()
        val tag = StubTag("tagA")
        val item = StubLibraryItemMetaData("itemA")
        lib.updateTag(tag)
        lib.updateItemMetaData(item)
        assertNotNull(lib.getTags().find { it.id == "tagA" })
        assertNotNull(lib.getItemMetaData().find { it.id == "itemA" })
        lib.removeTag(tag.id)
        lib.removeItem(item.id)
        assertNull(lib.getTags().find { it.id == "tagA" })
        assertNull(lib.getItemMetaData().find { it.id == "itemA" })
    }
    @Test
    fun duplicatedUpdate() {
        val lib = SimpleLibrary()
        val tag = StubTag("tagA")
        val item = StubLibraryItemMetaData("itemA")
        lib.updateTagAll(tag, tag, tag)
        lib.updateItemMetaDataAll(item, item, item)

        assertNotNull(lib.getTags().find { it.id == "tagA" })
        assertNotNull(lib.getItemMetaData().find { it.id == "itemA" })
        assertEquals(1, lib.getTags().size)
        assertEquals(1, lib.getItemMetaData().size)
    }
    @Test
    fun tagsAndItemsAreImmutable() {
        val lib = SimpleLibrary()

        val emptyTags = lib.getTags()
        val emptyItems = lib.getItemMetaData()
        assertTrue(emptyTags.isEmpty())
        assertTrue(emptyItems.isEmpty())

        val tag = StubTag("tagA")
        val item = StubLibraryItemMetaData("itemA")
        lib.updateTag(tag)
        lib.updateItemMetaData(item)
        assertNotNull(lib.getTags().find { it.id == "tagA" })
        assertNotNull(lib.getItemMetaData().find { it.id == "itemA" })

        assertTrue(emptyTags.isEmpty())
        assertTrue(emptyItems.isEmpty())
    }
}