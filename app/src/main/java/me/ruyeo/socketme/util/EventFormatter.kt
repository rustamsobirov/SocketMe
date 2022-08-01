package me.ruyeo.socketme.util

class EventFormatter {

    private var namespace: String? = null


    constructor() {}

    constructor(namespace: String?) {
        this.namespace = namespace
    }


    fun format(event: String): String {
        var result = event
        if (result[0] == '.' || result[0] == '\\') {
            return result.substring(1)
        } else if (!namespace!!.isEmpty()) {
            result = "$namespace.$event"
        }
        return result.replace('.', '\\')
    }

    fun setNamespace(namespace: String?) {
        this.namespace = namespace
    }
}