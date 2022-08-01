package me.ruyeo.socketme.channel

import io.socket.client.Socket
import me.ruyeo.socketme.EchoCallback
import me.ruyeo.socketme.EchoOptions

class SocketIOPresenceChannel

    (socket: Socket?, name: String?, options: EchoOptions?) :
    SocketIOPrivateChannel(socket, name, options), IPresenceChannel {
    override fun here(callback: EchoCallback?): IPresenceChannel {
        on("presence:subscribed", callback!!)
        return this
    }

    override fun joining(callback: EchoCallback?): IPresenceChannel {
        on("presence:joining", callback!!)
        return this
    }

    override fun leaving(callback: EchoCallback?): IPresenceChannel {
        on("presence:leaving", callback!!)
        return this
    }
}