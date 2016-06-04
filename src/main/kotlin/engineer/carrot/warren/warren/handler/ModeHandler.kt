package engineer.carrot.warren.warren.handler

import engineer.carrot.warren.kale.IKaleHandler
import engineer.carrot.warren.kale.irc.message.rfc1459.ModeMessage
import engineer.carrot.warren.warren.event.ChannelModeEvent
import engineer.carrot.warren.warren.event.IWarrenEventDispatcher
import engineer.carrot.warren.warren.event.UserModeEvent
import engineer.carrot.warren.warren.loggerFor
import engineer.carrot.warren.warren.state.CaseMappingState
import engineer.carrot.warren.warren.state.ChannelTypesState
import engineer.carrot.warren.warren.state.JoinedChannelsState
import engineer.carrot.warren.warren.state.UserPrefixesState

class ModeHandler(val eventDispatcher: IWarrenEventDispatcher, val channelTypesState: ChannelTypesState, val channelsState: JoinedChannelsState, val userPrefixesState: UserPrefixesState, val caseMappingState: CaseMappingState) : IKaleHandler<ModeMessage> {
    private val LOGGER = loggerFor<ModeHandler>()

    override val messageType = ModeMessage::class.java

    override fun handle(message: ModeMessage, tags: Map<String, String?>) {
        val target = message.target

        if (channelTypesState.types.any { char -> target.startsWith(char) }) {
            // Channel mode

            for (modifier in message.modifiers) {
                if (userPrefixesState.prefixesToModes.values.contains(modifier.mode)) {
                    // User mode changed

                    val channel = channelsState[target]
                    if (channel == null) {
                        LOGGER.warn("user mode changed for a channel we don't think we're in, bailing: $message")
                        continue
                    }

                    val nick = modifier.parameter
                    if (nick == null) {
                        LOGGER.warn("user mode changed but missing users name from mode modifier, bailing: $message")
                        continue
                    }

                    val user = channel.users[nick]
                    if (user == null) {
                        LOGGER.warn("user mode changed but not tracking that user, bailing: $message")
                        continue
                    }

                    when {
                        modifier.isAdding -> {
                            user.modes += modifier.mode
                        }

                        modifier.isRemoving -> {
                            user.modes -= modifier.mode
                        }
                    }

                    LOGGER.debug("user mode state changed: $user")
                }

                eventDispatcher.fire(ChannelModeEvent(user = message.source, channel = target, modifier = modifier))
            }
        } else {
            // User mode

            LOGGER.info("user changed modes: $message")

            for (modifier in message.modifiers) {
                eventDispatcher.fire(UserModeEvent(user = target, modifier = modifier))
            }
        }
    }
}