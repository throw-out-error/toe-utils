package dev.throwouterror.util.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.*

class JsonObjectBuilder(val gson: Gson = GsonBuilder().setPrettyPrinting().create()) {
    private val deque: Deque<JsonElement> = ArrayDeque()

    fun json(build: JsonObjectBuilder.() -> Unit): JsonElement {
        deque.push(JsonObject())
        this.build()
        return deque.pop()
    }

    infix fun <T> String.to(value: T) {
        deque.peek().asJsonObject.add(this, gson.toJsonTree(value))
    }
}
