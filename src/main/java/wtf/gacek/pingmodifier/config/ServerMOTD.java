package wtf.gacek.pingmodifier.config;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class ServerMOTD {
    private @Nullable String favicon = null;
    private @Nullable String motd = null;

    public @Nullable String getFavicon() {
        return favicon;
    }
    public @Nullable String getMotd() {
        return motd;
    }

    public static ServerMOTD of(String favicon, String motd) {
        ServerMOTD serverMotd = new ServerMOTD();
        serverMotd.favicon = favicon;
        serverMotd.motd = motd;
        return serverMotd;
    }
}
