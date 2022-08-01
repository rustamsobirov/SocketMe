package me.ruyeo.socketme.connector

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import me.ruyeo.socketme.EchoCallback
import me.ruyeo.socketme.EchoException
import me.ruyeo.socketme.EchoOptions
import me.ruyeo.socketme.channel.AbstractChannel
import me.ruyeo.socketme.channel.SocketIOChannel
import me.ruyeo.socketme.channel.SocketIOPresenceChannel
import me.ruyeo.socketme.channel.SocketIOPrivateChannel
import okhttp3.OkHttpClient
import java.net.URISyntaxException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class SocketIOConnector(options: EchoOptions) : AbstractConnector(options) {
    /**
     * The socket.
     */
    private var mSocket: Socket? = null
    private var TAG = "TAG"

    /**
     * All of the subscribed channel names.
     */
    private val channels: MutableMap<String, SocketIOChannel>
    override fun connect(success: EchoCallback?, error: EchoCallback?) {
        try {
            val mySSLContext = SSLContext.getInstance("TLS")
            mySSLContext.init(null, trustAllCerts, SecureRandom())
            val okHttpClient = OkHttpClient.Builder()
                .hostnameVerifier(myHostnameVerifier)
                .sslSocketFactory(mySSLContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .build()
            IO.setDefaultOkHttpWebSocketFactory(okHttpClient)
            IO.setDefaultOkHttpCallFactory(okHttpClient)
            val opts = IO.Options()
            opts.callFactory = okHttpClient
            opts.webSocketFactory = okHttpClient
            opts.timeout = (60 * 1000).toLong()
            opts.forceNew = false
            opts.secure = true
            opts.reconnection = true
            mSocket = IO.socket("https://194.58.123.195:6001", opts)
            mSocket?.connect()
            mSocket?.on(
                Socket.EVENT_CONNECT
            ,success)?.on(
                Socket.EVENT_DISCONNECT
            ) { Log.e(TAG, "socket disconnected") }
                ?.on(
                    Socket.EVENT_CONNECT_ERROR
                ) { args -> Log.e(TAG, "socket error " + args[0]) }
                ?.on(
                    Socket.EVENT_DISCONNECT
                ) { Log.e(TAG, "socket connection timeout") }
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }

        @Throws(CertificateException::class)
        override fun checkClientTrusted(
            chain: Array<X509Certificate>,
            authType: String
        ) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(
            chain: Array<X509Certificate>,
            authType: String
        ) {
        }
    })
    var myHostnameVerifier =
        HostnameVerifier { hostname, session -> true }


    fun on(eventName: String?, callback: EchoCallback?) {
        mSocket?.on(eventName, callback)
    }


    fun off(eventName: String?) {
        mSocket?.off(eventName)
    }

    fun listen(channel: String, event: String?, callback: EchoCallback?): SocketIOChannel {
        return channel(channel)!!.listen(event, callback) as SocketIOChannel
    }

    override fun channel(channel: String?): AbstractChannel? {
        if (!channels.containsKey(channel)) {
            channels[channel!!] = SocketIOChannel(mSocket, channel, options)
        }
        return channels[channel]
    }


    override fun privateChannel(channel: String?): AbstractChannel? {
        val name = "private-$channel"
        if (!channels.containsKey(name)) {
            channels[name] =
                SocketIOPrivateChannel(mSocket, name, options)
        }
        return channels[name]
    }


    override fun presenceChannel(channel: String?): AbstractChannel? {
        val name = "presence-$channel"
        if (!channels.containsKey(name)) {
            channels[name] = SocketIOPresenceChannel(mSocket, name, options)
        }
        return channels[name]
    }

    override fun leave(channel: String?) {
        val privateChannel = "private-$channel"
        val presenceChannel = "presence-$channel"
        val iterator: MutableIterator<Map.Entry<String, SocketIOChannel>> =
            channels.entries.iterator()
        while (iterator.hasNext()) {
            val (subscribed, socketIOChannel) = iterator.next()
            if (subscribed == channel || subscribed == privateChannel || subscribed == presenceChannel) {
                try {
                    socketIOChannel.unsubscribe(null)
                } catch (e: EchoException) {
                    e.printStackTrace()
                }
                iterator.remove()
            }
        }
    }

    override val isConnected: Boolean
        get() = mSocket != null && mSocket!!.connected()

    override fun disconnect() {
        for (subscribed in channels.keys) {
            try {
                channels[subscribed]!!.unsubscribe(null)
            } catch (e: EchoException) {
                e.printStackTrace()
            }
        }
        channels.clear()
        mSocket?.disconnect()
    }

    /**
     * Create a new Socket.IO connector.
     *
     * @param options options
     */
    init {
        channels = HashMap()
    }
}