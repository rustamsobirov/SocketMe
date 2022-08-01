package me.ruyeo.socketme

import com.google.gson.Gson

class MessageCreated(
    val message: String
) {

    companion object {
        fun parseFrom(value: Array<MessageCreated>): MessageCreated? {
            val messageData = value[0] as org.json.JSONObject
            try {
                return Gson().fromJson(messageData.toString(), MessageCreated::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

}