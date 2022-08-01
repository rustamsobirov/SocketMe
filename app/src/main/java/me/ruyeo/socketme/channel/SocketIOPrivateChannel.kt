package me.ruyeo.socketme.channel

import io.socket.client.Socket
import me.ruyeo.socketme.EchoCallback
import me.ruyeo.socketme.EchoException
import me.ruyeo.socketme.EchoOptions
import org.json.JSONObject


open class SocketIOPrivateChannel
/**
 * Create a new Socket.IO private channel.
 *
 * @param socket  the socket
 * @param name    channel name
 * @param options Echo options
 */
    (socket: Socket?, name: String?, options: EchoOptions?) :
    SocketIOChannel(socket!!, name!!, options!!) {
    /**
     * Trigger client event on the channel.
     *
     * @param event    event name
     * @param data     data to send
     * @param callback callback from the server
     * @return this channel
     * @throws EchoException if error creating the whisper
     */
    @Throws(EchoException::class)
    fun whisper(event: String, data: JSONObject?, callback: EchoCallback?): SocketIOPrivateChannel {
        val `object` = JSONObject()
        try {
            `object`.put("channel", name)
            `object`.put("event", "client-$event")
            if (data != null) {
                `object`.put("data", data)
            }
            if (callback != null) {
                socket?.emit("client event", `object`, callback)
            } else {
                socket?.emit("client event", `object`)
            }
        } catch (e: Exception) {
            throw EchoException("Cannot whisper o, channel '" + name + "' : " + e.message)
        }
        return this
    }
}