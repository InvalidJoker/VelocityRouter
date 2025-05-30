package de.joker.velocityrouter;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import de.joker.velocityrouter.config.RoutingConfig;
import de.joker.velocityrouter.listener.LoginListener;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(id = "velocityrouter", name = "VelocityRouter", version = "0.1.0-SNAPSHOT", authors = {"InvalidJoker"}, dependencies = {
        @Dependency(id = "luckperms", optional = true)
})
public class VelocityRouter {

    private final ProxyServer server;
    private final Logger logger;
    private RoutingConfig routingConfig;
    private final Path dataDirectory;
    private final LuckPermsIntegration luckPermsIntegration;

    @Inject
    public VelocityRouter(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.luckPermsIntegration = new LuckPermsIntegration();


        logger.info("Hello there! I made my first plugin with Velocity.");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("VelocityRouter is initializing...");

        if (!Files.exists(dataDirectory)) {
            try {
                Files.createDirectories(dataDirectory);
            } catch (IOException e) {
                logger.error("Failed to create data directory!", e);
            }
        }
        loadConfig();
        if (server.getPluginManager().isLoaded("luckperms")) {
            luckPermsIntegration.loadLuckPermsApi();
        } else {
            logger.warn("LuckPerms is not registered, some features may not work.");
        }

        if (routingConfig == null) {
            logger.error("Failed to load routing configuration. Plugin will not function correctly.");
            return;
        }

        LoginListener loginListener = new LoginListener(this);

        server.getEventManager().register(this, loginListener);
    }

    private void loadConfig() {
        Path configFile = dataDirectory.resolve("config.yml");
        if (!Files.exists(configFile)) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                if (in != null) {
                    Files.copy(in, configFile);
                    logger.info("Default config file created at: {}", configFile.toAbsolutePath());
                } else {
                    logger.error("Could not find default config.yml in resources!");
                    return;
                }
            } catch (IOException e) {
                logger.error("Failed to copy default config.yml!", e);
                return;
            }
        }

        try (InputStream in = Files.newInputStream(configFile)) {
            Yaml yaml = new Yaml(new Constructor(RoutingConfig.class));

            routingConfig = yaml.load(in);

            logger.info("Successfully loaded {} routing configurations", routingConfig.getRoutes() != null ? routingConfig.getRoutes().size() : 0);
        } catch (IOException e) {
            logger.error("Failed to load routing configuration from {}!", configFile.toAbsolutePath(), e);
        }
    }


    public RoutingConfig getConfig() {
        return routingConfig;
    }

    public ProxyServer getServer() {
        return server;
    }

    public LuckPermsIntegration getLuckPermsIntegration() {
        return luckPermsIntegration;
    }
}