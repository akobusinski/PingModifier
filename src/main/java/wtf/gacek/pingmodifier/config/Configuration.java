package wtf.gacek.pingmodifier.config;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import wtf.gacek.pingmodifier.PingModifier;
import wtf.gacek.pingmodifier.math.Signs;

import java.util.Map;

@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "unused"})
public class Configuration {
    @Comment("The default favicon that's going to show up if there's no server favicon set")
    private String defaultFavicon = "favicon.png";
    @Comment("Map so you can change the MOTD for a server")
    private Map<String, ServerMOTD> serverMotdMap = Map.of("example.com", ServerMOTD.of("favicon.png", "Example MOTD!"));
    @Comment("Map so you can change the MOTD for a certain protocol version\nTIP: You can prefix the protocol version with either \"<=\", \">=\", \"<\", \">\", so you don't have to type every single version out!")
    private Map<ProtocolVersion, ServerMOTD> protocolMotdMap = Map.of(new ProtocolVersion(Signs.MORE_OR_EQUAL, 765), ServerMOTD.of("favicon2.png", "Example Protocol MOTD!"));
    @Comment("Whether or not have the max player count one more player than the current online players")
    private boolean incrementalOnlineCount = true;

    public boolean isOnlineCountIncremental() {
        return incrementalOnlineCount;
    }

    public String getDefaultFavicon() {
        return defaultFavicon;
    }

    public Map<String, ServerMOTD> getServerMotdMap() {
        return serverMotdMap;
    }

    public boolean motdMapContainsServer(String server) {
        return serverMotdMap.containsKey(server);
    }

    public Map<ProtocolVersion, ServerMOTD> getProtocolMotdMap() {
        return protocolMotdMap;
    }

    public boolean motdMapContainsProtocol(int protocol) {
        for (ProtocolVersion version: protocolMotdMap.keySet()) {
            if (version.contains(protocol)) {
                return true;
            }
        }
        return false;
    }

    public @Nullable ServerMOTD getMOTDByProtocol(int protocol) {
        for (Map.Entry<ProtocolVersion, ServerMOTD> entry: protocolMotdMap.entrySet()) {
            if (entry.getKey().contains(protocol)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
