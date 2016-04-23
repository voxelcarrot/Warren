package engineer.carrot.warren.warren.handler

import engineer.carrot.warren.kale.IKaleHandler
import engineer.carrot.warren.kale.irc.message.rfc1459.QuitMessage
import engineer.carrot.warren.kale.irc.message.rfc1459.PongMessage
import engineer.carrot.warren.warren.IMessageSink
import engineer.carrot.warren.warren.state.ChannelsState
import engineer.carrot.warren.warren.state.ConnectionState
import engineer.carrot.warren.warren.state.LifecycleState

class QuitHandler(val connectionState: ConnectionState, val channelsState: ChannelsState) : IKaleHandler<QuitMessage> {
    override val messageType = QuitMessage::class.java

    override fun handle(message: QuitMessage) {
        val from = message.source?.nick

        if (from == null) {
            println("from nick was missing, not doing anything: $message")
            return
        }

        if (from == connectionState.nickname) {
            // We quit the server

            println("we quit the server")
            connectionState.lifecycle = LifecycleState.DISCONNECTED
        } else {
            // Someone else quit

            for ((name, channel) in channelsState.joined) {
                if (channel.users.contains(from)) {
                    channel.users.remove(from)
                }
            }
        }

        println("someone quit, new states: $connectionState, $channelsState")
    }
}