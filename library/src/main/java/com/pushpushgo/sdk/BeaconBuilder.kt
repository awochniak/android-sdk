package com.pushpushgo.sdk

import com.pushpushgo.sdk.exception.PushPushException
import com.pushpushgo.sdk.work.UploadDelegate
import org.json.JSONArray
import org.json.JSONObject

class BeaconBuilder internal constructor(private val uploadDelegate: UploadDelegate) {

    private val selectors = mutableMapOf<String, Any>()

    private val tags = mutableListOf<Pair<String, String>>()

    private val tagsToDelete = mutableListOf<Pair<String, String>>()

    private var customId = ""

    /**
     * Add beacon selector
     *
     * @param key
     * @param value Selector value. Supported types: boolean, string, char, number
     * @return instance of builder
     */
    fun set(key: String, value: Any): BeaconBuilder {
        selectors[key] = value

        return this
    }

    /**
     * @param tag Tag name
     * @param label Tag label
     *
     * @return instance of builder
     */
    fun appendTag(tag: String, label: String): BeaconBuilder {
        tags.add(tag to label)

        return this
    }

    fun appendTag(tag: String): BeaconBuilder {
        tags.add(tag to "default")

        return this
    }

    fun getTags(): MutableList<Pair<String, String>> {
        return tags
    }

    /**
     * @param name Tag name
     *
     * @return instance of builder
     */
    fun removeTag(tag: String, label: String): BeaconBuilder {
        tagsToDelete.add(tag to label)

        return this
    }

    fun removeTag(tag: String): BeaconBuilder {
        tagsToDelete.add(tag to "default")

        return this
    }

    fun getTagsToDelete(): MutableList<Pair<String, String>> {
        return tagsToDelete
    }

    /**
     * Set custom beacon ID
     *
     * @param id Custom ID
     *
     * @return instance of builder
     */
    fun setCustomId(id: String?): BeaconBuilder {
        customId = id.orEmpty()

        return this
    }

    fun setCustomId(id: Int?): BeaconBuilder {
        customId = (id ?: 0).toString().takeIf { it != "0" }.orEmpty()

        return this
    }

    /**
     * @throws PushPushException on unsupported selector value type
     */
    fun send() {
        uploadDelegate.sendBeacon(JSONObject().apply {
            addSelectors()
            addTags()
            addTagsToDelete()
            if (customId.isNotEmpty()) put("customId", customId)
        })
    }

    private fun JSONObject.addSelectors() {
        selectors.forEach { (key, value) ->
            when (value) {
                is Boolean -> put(key, value)
                is String -> put(key, value)
                is Char -> put(key, value)
                is Number -> put(key, value)
                else -> throw PushPushException("Invalid type of beacon selector value. Supported types: boolean, string, char, number")
            }
        }
    }

    private fun JSONObject.addTags() {
        tags.ifEmpty { return }

        put("tags", JSONArray().apply {
            tags.forEach { (tag, label) ->
                put(JSONObject().apply {
                    put("tag", tag)
                    put("label", label)
                })
            }
        })
    }

    private fun JSONObject.addTagsToDelete() {
        tagsToDelete.ifEmpty { return }

        put("tagsToDelete", JSONArray().apply {
            tagsToDelete.forEach { (tag, label) ->
                put(JSONObject().apply {
                    put("tag", tag)
                    put("label", label)
                })
            }
        })
    }
}
