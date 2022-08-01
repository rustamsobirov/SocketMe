package me.ruyeo.socketme.channel

import io.socket.client.Manager.EVENT_RECONNECT
import io.socket.client.Socket
import me.ruyeo.socketme.EchoCallback
import me.ruyeo.socketme.EchoException
import me.ruyeo.socketme.EchoOptions
import me.ruyeo.socketme.util.EventFormatter
import org.json.JSONObject


open class SocketIOChannel(
    /**
     * The socket.
     */
    protected var socket: Socket?,
    /**
     * The channel name.
     */
    var name: String, options: EchoOptions
) :
    AbstractChannel() {
    /**
     * @return the channel name.
     */

    /**
     * Echo options.
     */
    protected var options: EchoOptions

    /**
     * Event formatter.
     */
    protected var formatter: EventFormatter

    /**
     * Events callback.
     */
    private val eventsCallbacks: MutableMap<String?, MutableList<EchoCallback>>

    /**
     * Subscribe to a Socket.io channel.
     *
     * @param callback callback with response from the server
     * @throws EchoException if error when subscribing to the channel.
     */
    @Throws(EchoException::class)
    fun subscribe(callback: EchoCallback?) {
        val `object` = JSONObject()
        try {
            `object`.put("channel", name)
            `object`.put("auth", options.auth)
            if (callback == null) {
                socket?.emit("subscribe", `object`)
            } else {
                socket?.emit("subscribe", `object`, callback)
            }
        } catch (e: Exception) {
            throw EchoException("Cannot subscribe to channel '" + name + "' : " + e.message)
        }
    }

    /**
     * Unsubscribe from channel and unbind event callbacks.
     *
     * @param callback callback with response from the server
     * @throws EchoException if error when unsubscribing to the channel.
     */
    @Throws(EchoException::class)
    fun unsubscribe(callback: EchoCallback?) {
        unbind()
        val `object` = JSONObject()
        try {
            `object`.put("channel", name)
            `object`.put("auth", options.auth)
            if (callback == null) {
                socket?.emit("unsubscribe", `object`)
            } else {
                socket?.emit("unsubscribe", `object`, callback)
            }
        } catch (e: Exception) {
            throw EchoException("Cannot unsubscribe to channel '" + name + "' : " + e.message)
        }
    }

    override fun listen(event: String?, callback: EchoCallback?): AbstractChannel {
        on(formatter.format(event!!), callback)
        return this
    }

    /**
     * Bind the channel's socket to an event and store the callback.
     *
     * @param event    event name
     * @param callback callback
     */
    fun on(event: String?, callback: EchoCallback?) {
        val listener: EchoCallback = object : EchoCallback {
            override fun call(vararg objects: Any) {
                if (objects.size > 0 && objects[0] is String) {
                    val channel = objects[0] as String
                    if (channel == name) {
                        callback!!.call(*objects)
                    }
                }
            }
        }
        socket?.on(event, listener)
        bind(event, listener)
    }

    /**
     * Attach a 'reconnect' listener and bind the event.
     */
    private fun configureReconnector() {
        val callback: EchoCallback = object : EchoCallback {
            override fun call(vararg objects: Any) {
                try {
                    subscribe(null)
                } catch (e: EchoException) {
                    e.printStackTrace()
                }
            }
        }
        socket?.on(Socket.EVENT_CONNECT, callback)
        bind(Socket.EVENT_CONNECT, callback)
    }

    /**
     * Bind the channel's socket to an event and store the callback.
     *
     * @param event    event name
     * @param callback callback when event is triggered
     */
    fun bind(event: String?, callback: EchoCallback) {
        if (!eventsCallbacks.containsKey(event)) {
            eventsCallbacks[event] = ArrayList()
        }
        eventsCallbacks[event]!!.add(callback)
    }

    /**
     * Unbind the channel's socket from all stored event callbacks.
     */
    fun unbind() {
        val iterator = eventsCallbacks.keys.iterator()
        while (iterator.hasNext()) {
            socket?.off(iterator.next())
            iterator.remove()
        }
    }

    /**
     * Create a new Socket.IO channel.
     *
     * @param socket  the socket
     * @param name    channel name
     * @param options Echo options
     */
    init {
        name = name
        this.options = options
        formatter = EventFormatter(options.eventNamespace)
        eventsCallbacks = HashMap()
        try {
            subscribe(null)
        } catch (e: EchoException) {
            e.printStackTrace()
        }
        configureReconnector()
    }
}