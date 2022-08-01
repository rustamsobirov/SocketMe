package me.ruyeo.socketme.connector

import me.ruyeo.socketme.EchoCallback
import me.ruyeo.socketme.EchoOptions
import me.ruyeo.socketme.channel.AbstractChannel


abstract class AbstractConnector
/**
 * Create a new connector.
 *
 * @param options options
 */(
    /**
     * Echo options.
     */
    protected var options: EchoOptions
) {
    /**
     * Create a fresh connection.
     *
     * @param success callback when success
     * @param error   callback when error
     */
    abstract fun connect(success: EchoCallback?, error: EchoCallback?)

    /**
     * Get a channel instance by name.
     *
     * @param channel channel name
     * @return the channel
     */
    abstract fun channel(channel: String?): AbstractChannel?

    /**
     * Get a private channel instance by name.
     *
     * @param channel channel name
     * @return the channel
     */
    abstract fun privateChannel(channel: String?): AbstractChannel?

    /**
     * Get a presence channel instance by name.
     *
     * @param channel channel name
     * @return the channel
     */
    abstract fun presenceChannel(channel: String?): AbstractChannel?

    /**
     * Leave the given channel.
     *
     * @param channel channel name
     */
    abstract fun leave(channel: String?)

    /**
     * @return if the socket is connected to the server.
     */
    abstract val isConnected: Boolean

    /**
     * Disconnect from the Echo server.
     */
    abstract fun disconnect()
}