package wtf.gacek.pingmodifier;

import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import wtf.gacek.pingmodifier.commands.MainCommand;
import wtf.gacek.pingmodifier.config.Configuration;
import wtf.gacek.pingmodifier.constants.BuildConstants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Plugin(
        id = "pingmodifier",
        name = "PingModifier",
        version = BuildConstants.VERSION,
        authors = "GacekKosmatek",
        description = "Lets you customize the server's MOTD"
)
public class PingModifier {
    public final ProxyServer server;
    public final Logger logger;
    public final File dataFolder;
    private final File configFile;
    private Configuration config;
    private HoconConfigurationLoader configLoader;
    private CommentedConfigurationNode configurationNode;
    private ProxyPingListener pingListener;
    private static PingModifier instance;
    @Inject
    public PingModifier(ProxyServer server, Logger logger, @DataDirectory Path folder) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = folder.toFile();
        this.configFile = dataFolder.toPath().resolve("config.yml").toFile();
        this.configLoader = HoconConfigurationLoader
                .builder()
                .defaultOptions(options -> options
                        .shouldCopyDefaults(true)
                        .header("PingModifier Config")
                )
                .path(configFile.toPath())
                .build();
        pingListener = new ProxyPingListener(this);
        instance = this;
    }

    public static PingModifier getInstance() {
        return instance;
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent event) throws IOException {
        if (!dataFolder.isDirectory()) {
            throw new IOException("The data folder is not a directory!");
        }

        if (!dataFolder.exists()) {
            if (!dataFolder.mkdirs()) {
                throw new IOException("Failed to create the data directory!");
            }
        }

        loadConfig();
        if (!configFile.exists()) {
            saveConfig();
        }

        EventManager eventManager = server.getEventManager();
        eventManager.register(this, ProxyPingEvent.class, PostOrder.LATE, pingListener);

        CommandManager commandManager = server.getCommandManager();
        BrigadierCommand mainCommand = MainCommand.createBrigadierCommand();
        CommandMeta commandMeta = commandManager.metaBuilder(mainCommand).build();
        commandManager.register(commandMeta, mainCommand);
    }

    public void reload() throws ConfigurateException {
        loadConfig();
        pingListener.clearCache();
    }

    public void saveConfig() throws ConfigurateException {
        configurationNode.set(Configuration.class, config);
        configLoader.save(configurationNode);
    }

    public void loadConfig() throws ConfigurateException {
        configurationNode = configLoader.load();
        this.config = configurationNode.get(Configuration.class);
    }

    public Configuration getConfig() {
        return this.config;
    }
}
