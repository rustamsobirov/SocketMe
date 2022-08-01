package me.ruyeo.socketme.channel

import me.ruyeo.socketme.EchoCallback

interface IPresenceChannel {

    fun here(callback: EchoCallback?): IPresenceChannel?

    fun joining(callback: EchoCallback?): IPresenceChannel?

    fun leaving(callback: EchoCallback?): IPresenceChannel?
}