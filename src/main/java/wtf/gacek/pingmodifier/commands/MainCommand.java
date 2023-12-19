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
import wtf.gacek.pingmodifier.constants.Constants;
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
                                source.sendMessage(Component.text("Failed to reload config!", Constants.RED_COLOR));
                                return 1;
                            }

                            source.sendMessage(Component.text("Successfully reloaded!", Constants.GREEN_COLOR));
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

        builder.append(Component.text(String.format("PingModifier v%s help\n", Constants.VERSION), Constants.GOLD_COLOR));

        if (source.hasPermission(Permissions.RELOAD_CONFIG.getPermission())) { // Reload subcommand
            builder.append(
                    Component.text("\t- ", Constants.GRAY_COLOR),
                    Component.text("/pingmodifier reload", Constants.GOLD_COLOR),
                    Component.text(" - ", Constants.GRAY_COLOR),
                    Component.text("Reloads the configuration file\n", Constants.GOLD_COLOR)
            );
        }

        // Help Subcommand
        builder.append(
                Component.text("\t- ", Constants.GRAY_COLOR),
                Component.text("/pingmodifier help", Constants.GOLD_COLOR),
                Component.text(" - ", Constants.GRAY_COLOR),
                Component.text("Shows this help command\n", Constants.GOLD_COLOR)
        );

        source.sendMessage(builder.build());
        return 1;
    }
}
