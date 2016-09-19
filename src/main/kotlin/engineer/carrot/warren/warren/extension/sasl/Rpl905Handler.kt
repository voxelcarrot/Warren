package engineer.carrot.warren.warren.extension.sasl

import engineer.carrot.warren.kale.IKaleHandler
import engineer.carrot.warren.kale.irc.message.ircv3.sasl.Rpl905Message
import engineer.carrot.warren.warren.IMessageSink
import engineer.carrot.warren.warren.extension.cap.CapState
import engineer.carrot.warren.warren.handler.helper.RegistrationHelper
import engineer.carrot.warren.warren.loggerFor
import engineer.carrot.warren.warren.state.AuthLifecycle

class Rpl905Handler(val capState: CapState, val saslState: SaslState, val sink: IMessageSink) : IKaleHandler<Rpl905Message> {

    private val LOGGER = loggerFor<Rpl905Handler>()

    override val messageType = Rpl905Message::class.java

    override fun handle(message: Rpl905Message, tags: Map<String, String?>) {
        LOGGER.warn("sasl auth failed: ${message.contents}")

        saslState.lifecycle = AuthLifecycle.AUTH_FAILED

        if (RegistrationHelper.shouldEndCapNegotiation(saslState, capState)) {
            RegistrationHelper.endCapNegotiation(sink, capState)
        } else {
            LOGGER.debug("didn't think we should end the registration process, waiting")
        }
    }

}
