package me.ruyeo.socketme

import io.socket.client.Ack

import io.socket.emitter.Emitter


interface EchoCallback : Emitter.Listener, Ack