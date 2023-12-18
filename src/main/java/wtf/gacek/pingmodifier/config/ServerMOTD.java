package wtf.gacek.pingmodifier.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class ServerMOTD {
    private String address;
    private String favicon;
    private String motd;

    public String getAddress() {
        return address;
    }

    public String getFavicon() {
        return favicon;
    }

    public String getMotd() {
        return motd;
    }
}
