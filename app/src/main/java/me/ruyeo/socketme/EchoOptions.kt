package me.ruyeo.socketme

import org.json.JSONObject


class EchoOptions {
    /**
     * Host of the Echo server.<br></br>
     * **Default : **http://localhost:6001
     */
    var host: String
  
    var authEndpoint: String

    /**
     * Event namespace.<br></br>
     * **Default : **App.Events
     */
    var eventNamespace: String

    /**
     * Request headers.
     */
    var headers: Map<String, String>

    /**
     * @return the auth JSON object.
     * @throws Exception if error creating the JSON.
     */
    @get:Throws(Exception::class)
    val auth: JSONObject
        get() {
            val auth = JSONObject()
            val headers = JSONObject()
            for (header in this.headers.keys) {
                headers.put(header, this.headers[header])
            }
            auth.put("headers", headers)
            return auth
        }

    /**
     * Create default object of options.
     */
    init {
        headers = HashMap()
        host = "http://localhost:6001"
        authEndpoint = "/broadcasting/auth"
        eventNamespace = "App.Events"
    }
}