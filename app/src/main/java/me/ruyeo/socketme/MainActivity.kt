package me.ruyeo.socketme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import me.ruyeo.Echo
import me.ruyeo.socketme.util.Message

const val SERVER_URL = "https://194.58.123.195:6001"
const val CHANNEL_MESSAGES = "messages"
const val EVENT_MESSAGE_CREATED = ".newMessage"
const val TAG = "msg"

open class MainActivity : AppCompatActivity() {
    private var echo: Echo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        connectToSocket()

    }

    private fun connectToSocket() {
        val options = EchoOptions()
        options.host = SERVER_URL

        echo = Echo(options)
        echo?.connect(object : EchoCallback {
            override fun call(vararg args: Any?) {
                log("successful connect")
                listenForEvents()
            }
        }, object : EchoCallback {
            override fun call(vararg args: Any?) {
                log("error while connecting: " + args[0])
            }
        })
    }

    private fun listenForEvents() {
        Log.d(TAG, "listenForEvents: ok")
        echo?.let {
            it.channel(CHANNEL_MESSAGES)
                ?.listen(EVENT_MESSAGE_CREATED, object : EchoCallback{
                    override fun call(vararg args: Any?) {
                    //    val newEvent = MessageCreated.parseFrom(arrayOf(args))
                        val message = Gson().fromJson(args.component2().toString(),Message::class.java)
                        displayNewEvent(message)
                    }
                })
        }
    }

    private fun displayNewEvent(component1: Message) {
        log(component1.message)
    }

    protected fun disconnectFromSocket() {
        echo?.disconnect()
    }

    private fun log(message: String) {
        Log.i(TAG, message)
    }


}