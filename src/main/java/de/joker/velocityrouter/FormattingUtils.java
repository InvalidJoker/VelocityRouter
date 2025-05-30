package de.joker.velocityrouter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class FormattingUtils {
    public static Component sendMessage(String message) {
        return MiniMessage.miniMessage().deserialize(message);
    }
}
