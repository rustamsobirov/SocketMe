package me.ruyeo.socketme.channel

import me.ruyeo.socketme.EchoCallback


abstract class AbstractChannel {

    abstract fun listen(event: String?, callback: EchoCallback?): AbstractChannel


    fun notification(callback: EchoCallback?): AbstractChannel {
        return listen(
            ".Illuminate\\\\Notifications\\\\Events\\\\BroadcastNotificationCreated",
            callback
        )
    }

    fun listenForWhisper(event: String, callback: EchoCallback?): AbstractChannel {
        return listen(".client-$event", callback)
    }
}