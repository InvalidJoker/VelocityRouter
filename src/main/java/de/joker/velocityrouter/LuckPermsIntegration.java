package de.joker.velocityrouter;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuckPermsIntegration {
    private LuckPerms luckPerms;
    private static final Logger logger = LoggerFactory.getLogger(LuckPermsIntegration.class);

    void loadLuckPermsApi() {
        try {
            this.luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            logger.warn("LuckPerms API not available, some features may not work.", e);
            return;
        }

        logger.info("LuckPerms API loaded successfully.");
    }


    public LuckPerms getLuckPerms() {
        return luckPerms;
    }
}
