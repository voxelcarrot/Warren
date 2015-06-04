package engineer.carrot.warren.warren;

import com.google.common.collect.Maps;
import engineer.carrot.warren.warren.irc.Channel;

import javax.annotation.Nullable;
import java.util.Map;

public class ChannelManager {
    private final Map<String, Channel> channels;

    public ChannelManager() {
        this.channels = Maps.newHashMap();
    }

    public Map<String, Channel> getAllChannels() {
        return this.channels;
    }

    public boolean containsChannel(String channelName) {
        return this.channels.containsKey(channelName);
    }

    @Nullable
    public Channel getChannel(String channelName) {
        if (!this.containsChannel(channelName)) {
            return null;
        }

        return this.channels.get(channelName);
    }

    public boolean addChannel(Channel channel) {
        if (this.containsChannel(channel.name)) {
            return false;
        }

        this.channels.put(channel.name, channel);
        return true;
    }

    public void removeChannel(String channelName) {
        if (this.containsChannel(channelName)) {
            this.channels.remove(channelName);
        }
    }
}
