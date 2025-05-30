package de.joker.velocityrouter.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.joker.velocityrouter.FormattingUtils;
import de.joker.velocityrouter.VelocityRouter;
import de.joker.velocityrouter.config.PermissionRoute;
import de.joker.velocityrouter.config.RouteEntry;
import de.joker.velocityrouter.config.RoutingConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Optional;

public class LoginListener {
    private final VelocityRouter velocityRouter;
    private final Logger logger = LoggerFactory.getLogger(LoginListener.class);

    public LoginListener(VelocityRouter velocityRouter) {
        this.velocityRouter = velocityRouter;
    }

    @Subscribe
    public void onPlayerChooseInitialServer(PlayerChooseInitialServerEvent event) {
        Player player = event.getPlayer();
        String connectingHost = player.getVirtualHost()
                .map(InetSocketAddress::getHostString)
                .orElse("unknown");

        logger.info("Player {} connecting from hostname: {}", player.getUsername(), connectingHost);

        RoutingConfig config = velocityRouter.getConfig();
        if (config == null || config.getRoutes() == null || config.getRoutes().isEmpty()) {
            logger.warn("Routing configuration is not loaded or empty.");
            return;
        }

        RouteEntry route = findRoute(config, connectingHost).orElse(null);
        if (route == null) {
            logger.warn("No routing rule found for hostname '{}' for player {}. Using Velocity default.",
                    connectingHost, player.getUsername());
            return;
        }

        if (velocityRouter.getLuckPermsIntegration().getLuckPerms() != null && !player.hasPermission("velocity.admin")) {
            Optional<RegisteredServer> permRouteServer = findPermissionRouteServer(player, route);
            if (permRouteServer.isPresent()) {
                logger.info("Routing player {} with permission route to '{}'", player.getUsername(), permRouteServer.get().getServerInfo().getName());
                event.setInitialServer(permRouteServer.get());
                return;
            }
        } else if (velocityRouter.getLuckPermsIntegration().getLuckPerms() != null) {
            logger.info("Admin {} bypasses permission checks for routing.", player.getUsername());
        }

        routeDefaultServer(event, player, connectingHost, route);
    }

    private Optional<RouteEntry> findRoute(RoutingConfig config, String hostname) {
        return config.getRoutes().stream()
                .filter(route -> hostname.equalsIgnoreCase(route.getHostname()))
                .findFirst()
                .or(() -> config.getRoutes().stream()
                        .filter(route -> "*".equals(route.getHostname()))
                        .findFirst());
    }

    private Optional<RegisteredServer> findPermissionRouteServer(Player player, RouteEntry route) {
        for (PermissionRoute permRoute : route.getPermissionRoutes()) {
            if (player.hasPermission(permRoute.getPermission())) {
                Optional<RegisteredServer> targetServer = velocityRouter.getServer().getServer(permRoute.getTargetServer());
                if (targetServer.isPresent()) {
                    return targetServer;
                } else {
                    player.sendMessage(FormattingUtils.sendMessage(
                            permRoute.getDenyMessage() != null
                                    ? permRoute.getDenyMessage()
                                    : "<red>Target server for your permission is offline or not configured."));
                    logger.warn("Target server '{}' for permission '{}' is offline or not configured.",
                            permRoute.getTargetServer(), permRoute.getPermission());
                }
            }
        }
        return Optional.empty();
    }

    private void routeDefaultServer(PlayerChooseInitialServerEvent event, Player player, String connectingHost, RouteEntry route) {
        Optional<RegisteredServer> defaultServer = velocityRouter.getServer().getServer(route.getDefaultServer());
        if (defaultServer.isPresent()) {
            logger.info("Routing player {} to default server '{}' for hostname '{}'.",
                    player.getUsername(), route.getDefaultServer(), connectingHost);
            event.setInitialServer(defaultServer.get());
        } else {
            player.disconnect(MiniMessage.miniMessage().deserialize(
                    "<red>Failed to connect: Default server for " + connectingHost + " is offline or not configured."));
            logger.error("Default server '{}' for hostname '{}' is offline or not configured for player {}.",
                    route.getDefaultServer(), connectingHost, player.getUsername());
        }
    }
}
