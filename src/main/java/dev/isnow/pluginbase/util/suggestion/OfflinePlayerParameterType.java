package dev.isnow.pluginbase.util.suggestion;

import dev.isnow.pluginbase.data.impl.PlayerData;
import dev.isnow.pluginbase.database.impl.result.MultipleEntityResult;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import studio.mevera.imperat.BukkitSource;
import studio.mevera.imperat.command.parameters.CommandParameter;
import studio.mevera.imperat.command.parameters.type.BaseParameterType;
import studio.mevera.imperat.context.ExecutionContext;
import studio.mevera.imperat.context.internal.CommandInputStream;
import studio.mevera.imperat.resolvers.SuggestionResolver;

import java.util.List;
import java.util.stream.Collectors;

public class OfflinePlayerParameterType extends BaseParameterType<BukkitSource, OfflinePlayer> {
    @Override
    public SuggestionResolver<BukkitSource> getSuggestionResolver() {
        return (context, parameter) -> {
            final String arg = context.getArgToComplete().value();
            if (arg == null || arg.isEmpty()) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }

            final MultipleEntityResult<PlayerData> result = PlayerData.findByNameLike(arg, 10);
            if (result == null || result.getEntity().isEmpty()) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            }

            final List<String> players = result.getEntity().stream().map(PlayerData::getName).collect(Collectors.toList());
            result.getSession().closeSession();
            return players;
        };
    }

    @Override
    public @Nullable OfflinePlayer resolve(@NotNull final ExecutionContext<BukkitSource> context, @NotNull final CommandInputStream<BukkitSource> inputStream, @NotNull final String input) {
        return Bukkit.getOfflinePlayerIfCached(input);
    }

    @Override
    public boolean matchesInput(final String input, final CommandParameter<BukkitSource> parameter) {
        return Bukkit.getOfflinePlayerIfCached(input) != null;
    }
}
