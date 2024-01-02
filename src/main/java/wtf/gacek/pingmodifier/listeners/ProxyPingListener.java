package wtf.gacek.pingmodifier.listeners;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Nullable;
import wtf.gacek.pingmodifier.PingModifier;
import wtf.gacek.pingmodifier.cache.HostnameProtocol;
import wtf.gacek.pingmodifier.config.Configuration;
import wtf.gacek.pingmodifier.config.ServerMOTD;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@SuppressWarnings("SameParameterValue")
public class ProxyPingListener implements EventHandler<ProxyPingEvent> {
    public ProxyPingListener(PingModifier plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.defaultFavicon = getFavicon(config.getDefaultFavicon());
    }
    private final PingModifier plugin;
    private Favicon defaultFavicon;
    private Configuration config;
    private final HashMap<String, Favicon> faviconCache = new HashMap<>();
    private final HashMap<HostnameProtocol, ServerPing.Builder> pingCache = new HashMap<>();

    public void clearCache() {
        config = plugin.getConfig();
        defaultFavicon = getFavicon(config.getDefaultFavicon());
        faviconCache.clear();
        pingCache.clear();
    }

    private BufferedImage resize(BufferedImage original, int width, int height) {
        Image img = original.getScaledInstance(width, height, Image.SCALE_SMOOTH); // Resize the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); // Create a new image buffer

        // Draw the image to the new image buffer
        Graphics2D graphics2D = image.createGraphics();
        graphics2D.drawImage(img, 0, 0, null);
        graphics2D.dispose();

        return image;
    }

    private BufferedImage readAndScaleImage(File imageFile, int width, int height) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);

        if (image.getWidth() != width || image.getHeight() != height) {
            image = resize(image, width, height);
        }

        return image;
    }

    private @Nullable Favicon getFavicon(String path) {
        File faviconFile = new File(plugin.dataFolder, path);
        if (!faviconFile.exists()) {
            plugin.logger.warn(String.format("The favicon file at \"%s\" doesn't exist!", faviconFile.getAbsolutePath()));
            return null;
        }

        if (!faviconFile.isFile()) {
            plugin.logger.warn(String.format("The favicon file at \"%s\" is not a file!", faviconFile.getAbsolutePath()));
            return null;
        }

        if (!faviconFile.canRead()) {
            plugin.logger.warn(String.format("Cannot read favicon file at \"%s\"!", faviconFile.getAbsolutePath()));
            return null;
        }

        try {
            BufferedImage image = readAndScaleImage(faviconFile, 64, 64);
            return Favicon.create(image);
        } catch (IOException e) {
            plugin.logger.error(String.format("Failed to create favicon! (%s)", faviconFile.getAbsolutePath()), e);
        }

        return null;
    }

    private void applyMOTD(ServerPing.Builder builder, @Nullable ServerMOTD motd) {
        if (motd == null) {
            return;
        }

        if (motd.getMotd() != null) {
            builder.description(MiniMessage.miniMessage().deserialize(motd.getMotd()));
        }

        if (motd.getFavicon() != null) {
            Favicon favicon = faviconCache.containsKey(motd.getFavicon()) ? faviconCache.get(motd.getFavicon()) : getFavicon(motd.getFavicon());

            if (favicon != null) {
                builder.favicon(favicon);
                faviconCache.putIfAbsent(motd.getFavicon(), favicon);
            }
        }
    }

    @Override
    public void execute(ProxyPingEvent event) {
        String hostname = event.getConnection().getVirtualHost().isPresent() ? event.getConnection().getVirtualHost().get().getHostName() : null;
        int protocol = event.getConnection().getProtocolVersion().getProtocol();

        HostnameProtocol hostnameProtocol = new HostnameProtocol(hostname, protocol);
        boolean cached = pingCache.containsKey(hostnameProtocol);

        ServerPing.Builder builder = cached ? pingCache.get(hostnameProtocol) : event.getPing().asBuilder();

        if (!cached) {
            if (defaultFavicon != null) {
                builder.favicon(defaultFavicon);
            }

            ServerMOTD hostnameMOTD = config.getServerMotdMap().get(hostname);
            ServerMOTD protocolMOTD = config.getMOTDByProtocol(protocol);

            applyMOTD(builder, hostnameMOTD);
            applyMOTD(builder, protocolMOTD);
        }

        if (config.isOnlineCountIncremental()) {
            int playerCount = plugin.server.getPlayerCount();
            builder.onlinePlayers(playerCount).maximumPlayers(playerCount + 1);
        }

        pingCache.putIfAbsent(hostnameProtocol, builder);
        event.setPing(builder.build());
    }
}