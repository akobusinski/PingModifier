package wtf.gacek.pingmodifier.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.spongepowered.configurate.ConfigurateException;
import wtf.gacek.pingmodifier.PingModifier;
import wtf.gacek.pingmodifier.constants.BuildConstants;
import wtf.gacek.pingmodifier.constants.Colors;
import wtf.gacek.pingmodifier.constants.Permissions;

public class MainCommand {
    public static BrigadierCommand createBrigadierCommand() {
        LiteralCommandNode<CommandSource> command = LiteralArgumentBuilder.<CommandSource>literal("pingmodifier")
                .then(LiteralArgumentBuilder.<CommandSource>literal("reload")
                        .requires(source -> source.hasPermission(Permissions.RELOAD_CONFIG.getPermission()))
                        .executes(context -> {
                            CommandSource source = context.getSource();

                            try {
                                PingModifier.getInstance().reload();
                            } catch (ConfigurateException e) {
                                source.sendMessage(Component.text("Failed to reload config!", Colors.RED));
                                return 1;
                            }

                            source.sendMessage(Component.text("Successfully reloaded!", Colors.GREEN));
                            return 1;
                        })
                )
                .then(LiteralArgumentBuilder.<CommandSource>literal("help").executes(MainCommand::helpExecutor))
                .executes(MainCommand::helpExecutor)
                .build();

        return new BrigadierCommand(command);
    }

    private static int helpExecutor(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();

        TextComponent.Builder builder = Component.text();

        builder.append(Component.text(String.format("PingModifier v%s help\n", BuildConstants.VERSION), Colors.GOLD));

        if (source.hasPermission(Permissions.RELOAD_CONFIG.getPermission())) { // Reload subcommand
            builder.append(
                    Component.text("\t- ", Colors.GRAY),
                    Component.text("/pingmodifier reload", Colors.GOLD),
                    Component.text(" - ", Colors.GRAY),
                    Component.text("Reloads the configuration file\n", Colors.GOLD)
            );
        }

        // Help Subcommand
        builder.append(
                Component.text("\t- ", Colors.GRAY),
                Component.text("/pingmodifier help", Colors.GOLD),
                Component.text(" - ", Colors.GRAY),
                Component.text("Shows this help command\n", Colors.GOLD)
        );

        source.sendMessage(builder.build());
        return 1;
    }
}
