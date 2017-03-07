package com.github.am4dr.rokusho.app

import com.github.am4dr.rokusho.core.Tag
import com.github.am4dr.rokusho.core.TagType
import javafx.beans.binding.MapBinding
import javafx.beans.binding.ObjectBinding
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableObjectValue
import javafx.beans.value.ObservableStringValue
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections.observableMap
import javafx.collections.ObservableMap

interface ObservableTag : Tag, ObservableValue<Tag> {
    fun putAllData(data: Map<String, Any>)
    fun putData(key: String, value: Any)
    fun removeData(key: String)
}

class SimpleObservableTag(
        id: String,
        type: TagType,
        data: Map<String, Any> = mapOf()) : ObservableTag, ObjectBinding<Tag>() {
    constructor(tag: Tag) : this(tag.id, tag.type, tag.data)

    private val _id: ObservableStringValue = SimpleStringProperty(id)
    private val _type: ObservableObjectValue<TagType> = SimpleObjectProperty<TagType>(type)
    private val _data: ObservableMap<String, Any> = observableMap(data.toMutableMap())
    override val id: String get() = _id.value
    override val type: TagType get() = _type.value
    override val data: Map<String, Any> = _data

    init { super.bind(_id, _type, _data) }
    override fun computeValue(): Tag = this

    override fun putAllData(data: Map<String, Any>) { _data.putAll(data) }
    override fun putData(key: String, value: Any) { _data.put(key, value) }
    override fun removeData(key: String) { _data.remove(key) }
    override fun toString(): String =
            "SimpleObservableTag(id: $id, type: $type, data: $data)"
}
class DerivedObservableTag(
        private val base: ObservableValue<Tag>,
        data: Map<String, Any> = mapOf()) : ObservableTag, ObjectBinding<Tag>() {

    private val _data = observableMap(data.toMutableMap())
    private val mergedData = object : MapBinding<String, Any>() {
        init { super.bind(base, _data) }
        override fun computeValue(): ObservableMap<String, Any> = observableMap((base.value.data + _data).toMutableMap())
    }
    init { super.bind(mergedData) }
    override fun computeValue(): Tag = this

    override val id: String get() = base.value.id
    override val type: TagType get() = base.value.type
    override val data: Map<String, Any> get() = mergedData

    override fun putAllData(data: Map<String, Any>) { _data.putAll(data) }
    override fun putData(key: String, value: Any) { _data.put(key, value) }
    override fun removeData(key: String) { _data.remove(key) }
    override fun toString(): String =
            "DerivedObservableTag(id: $id, type: $type, data: $data, base: $base)"
}