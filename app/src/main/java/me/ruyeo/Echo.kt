package me.ruyeo

import me.ruyeo.socketme.EchoCallback
import me.ruyeo.socketme.EchoOptions
import me.ruyeo.socketme.channel.SocketIOChannel
import me.ruyeo.socketme.channel.SocketIOPresenceChannel
import me.ruyeo.socketme.channel.SocketIOPrivateChannel
import me.ruyeo.socketme.connector.SocketIOConnector


class Echo @JvmOverloads constructor(options: EchoOptions? = EchoOptions()) {
    /**
     * The broadcasting connector.
     */
    private val connector: SocketIOConnector

    /**
     * Connect to the Echo server.
     *
     * @param success success callback
     * @param error   error callback
     */
    fun connect(success: EchoCallback?, error: EchoCallback?) {
        connector.connect(success, error)
    }

    /**
     * Listen for general event on the socket.
     *
     * @param eventName event name
     * @param callback  callback
     * @see io.socket.client.Socket list of event types to listen to
     */
    fun on(eventName: String?, callback: EchoCallback?) {
        connector.on(eventName, callback)
    }

    /**
     * Remove all listeners for a general event.
     *
     * @param eventName event name
     */
    fun off(eventName: String?) {
        connector.off(eventName)
    }

    /**
     * Listen for an event on a channel instance.
     *
     * @param channel  channel name
     * @param event    event name
     * @param callback callback when event is triggered
     * @return the channel
     */
    fun listen(channel: String?, event: String?, callback: EchoCallback?): SocketIOChannel {
        return connector.listen(channel!!, event, callback)
    }

    /**
     * Get a channel by name.
     *
     * @param channel channel name
     * @return the channel
     */
    fun channel(channel: String?): SocketIOChannel? {
        return connector.channel(channel!!) as SocketIOChannel?
    }

    /**
     * Get a private channel by name.
     *
     * @param channel channel name
     * @return the channel
     */
    fun privateChannel(channel: String?): SocketIOPrivateChannel? {
        return connector.privateChannel(channel!!) as SocketIOPrivateChannel?
    }

    /**
     * Get a presence channel by name.
     *
     * @param channel channel name
     * @return the channel
     */
    fun presenceChannel(channel: String?): SocketIOPresenceChannel? {
        return connector.presenceChannel(channel!!) as SocketIOPresenceChannel?
    }

    /**
     * Leave the given channel.
     *
     * @param channel channel name
     */
    fun leave(channel: String?) {
        connector.leave(channel!!)
    }

    /**
     * @return if currently connected to server.
     */
    val isConnected: Boolean
        get() = connector.isConnected

    /**
     * Disconnect from the Echo server.
     */
    fun disconnect() {
        connector.disconnect()
    }
    /**
     * Create a new Echo instance.
     *
     * @param options options
     */
    /**
     * Creates a new Echo instance with default options.
     */
    init {
        connector = SocketIOConnector(options!!)
    }
}