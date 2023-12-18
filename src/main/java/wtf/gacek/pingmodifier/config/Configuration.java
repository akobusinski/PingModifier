package wtf.gacek.pingmodifier.config;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "unused"})
public class Configuration {
    @Comment("The default favicon that's going to show up if there's no server favicon set")
    private String defaultFavicon = "favicon.png";
    @Comment("Map for so you can change the MOTD for a server")
    private List<ServerMOTD> serverMotdList = List.of();

    public String getDefaultFavicon() {
        return defaultFavicon;
    }

    public List<ServerMOTD> getServerMotdList() {
        return serverMotdList;
    }

    public boolean motdListContainsServer(String server) {
        return getMOTDByServer(server) == null;
    }

    public @Nullable  ServerMOTD getMOTDByServer(String server) {
        for (ServerMOTD motd : serverMotdList) {
            if (Objects.equals(motd.getAddress(), server)) {
                return motd;
            }
        }

        return null;
    }
}
