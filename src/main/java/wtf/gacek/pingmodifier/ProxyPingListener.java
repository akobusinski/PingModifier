package wtf.gacek.pingmodifier;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Nullable;
import wtf.gacek.pingmodifier.config.ServerMOTD;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ProxyPingListener implements EventHandler<ProxyPingEvent> {
    public ProxyPingListener(PingModifier plugin) {
        this.plugin = plugin;
    }
    private final PingModifier plugin;
    private Favicon defaultFavicon;
    private HashMap<String, Favicon> serverFaviconMap = new HashMap<>();

    public void clearCache() {
        defaultFavicon = null;
        serverFaviconMap = new HashMap<>();
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

    private @Nullable Favicon getFavicon(String server) {
        if (serverFaviconMap.containsKey(server)) {
            return serverFaviconMap.get(server);
        }

        boolean isDefaultIcon = plugin.getConfig().motdListContainsServer(server);

        if (isDefaultIcon && defaultFavicon != null) {
            return defaultFavicon;
        }

        ServerMOTD motd = plugin.getConfig().getMOTDByServer(server);

        if (motd == null) {
            return null;
        }

        File faviconFile = new File(plugin.dataFolder, isDefaultIcon ? plugin.getConfig().getDefaultFavicon() : motd.getFavicon());
        if (!faviconFile.exists()) return null;

        if (!faviconFile.canRead()) {
            plugin.logger.warn("Cannot read favicon file!");
            return null;
        }

        try {
            BufferedImage image = readAndScaleImage(faviconFile, 64, 64);

            Favicon favicon = Favicon.create(image);

            if (!isDefaultIcon) {
                serverFaviconMap.put(server, favicon);
            } else {
                defaultFavicon = favicon;
            }
            return favicon;
        } catch (IOException e) {
            plugin.logger.error("Failed to create favicon!", e);
        }

        return null;
    }

    @Override
    public void execute(ProxyPingEvent event) {
        ServerPing.Builder builder = event.getPing().asBuilder();

        String hostname = null;

        if (event.getConnection().getVirtualHost().isPresent()) {
            hostname = event.getConnection().getVirtualHost().get().getHostName();
        }

        ServerMOTD motd = plugin.getConfig().getMOTDByServer(hostname);


        Favicon icon = getFavicon(hostname);
        if (icon != null) builder.favicon(icon);

        int playerCount = plugin.server.getPlayerCount();
        builder.onlinePlayers(playerCount).maximumPlayers(playerCount + 1);
        if (motd != null) {
            builder.description(LegacyComponentSerializer.legacyAmpersand().deserialize(motd.getMotd()));
        }

        event.setPing(builder.build());
    }
}
