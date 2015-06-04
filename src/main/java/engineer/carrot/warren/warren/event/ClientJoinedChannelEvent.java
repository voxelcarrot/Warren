package engineer.carrot.warren.warren.event;

import engineer.carrot.warren.warren.irc.Channel;

public class ClientJoinedChannelEvent extends Event {
    public final Channel channel;

    public ClientJoinedChannelEvent(Channel channel) {
        super();

        this.channel = channel;
    }
}
