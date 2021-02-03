package dev.throwouterror.util.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.*
import com.google.gson.*

fun json(build: JsonObjectBuilder.() -> Unit): JsonObject {
    return JsonObjectBuilder().json(build).asJsonObject
}

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
