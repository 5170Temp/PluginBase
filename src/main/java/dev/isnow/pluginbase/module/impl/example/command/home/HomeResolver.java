package dev.isnow.pluginbase.module.impl.example.command.home;

import dev.isnow.pluginbase.module.impl.example.data.HomeData;
import dev.velix.imperat.BukkitSource;
import dev.velix.imperat.command.parameters.CommandParameter;
import dev.velix.imperat.context.SuggestionContext;
import dev.velix.imperat.resolvers.SuggestionResolver;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HomeResolver implements SuggestionResolver<BukkitSource> {

    @Override
    public List<String> autoComplete(SuggestionContext<BukkitSource> context,
            CommandParameter<BukkitSource> parameter) {
        return List.of();
    }

    @Override
    public CompletableFuture<List<String>> asyncAutoComplete(SuggestionContext<BukkitSource> context, CommandParameter<BukkitSource> parameter) {
        final Player player = context.source().asPlayer();

        CompletableFuture<List<String>> future = new CompletableFuture<>();


        HomeData.findByUuidAsync(player.getUniqueId()).whenCompleteAsync((data, throwable) -> {
            if(data == null) {
                player.sendMessage("§cWystąpił błąd podczas ładowania danych gracza. Spróbuj ponownie później.");
                future.complete(List.of());
                return;
            }

            future.complete(data.getHomes().keySet().stream().toList());
        });

        return future;
    }
}
