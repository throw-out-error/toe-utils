package dev.throwouterror.util.data


import com.google.gson.Gson
import com.google.gson.GsonBuilder

object JsonUtils {
    val builder: Gson = GsonBuilder().setPrettyPrinting().create()
}
