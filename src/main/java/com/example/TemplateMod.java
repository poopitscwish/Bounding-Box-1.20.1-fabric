package com.example;

import net.fabricmc.api.ModInitializer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;

public class TemplateMod implements ModInitializer {
    @Override
    public void onInitialize() {
        PlayerEnterZoneCallback.EVENT.register(this::onPlayerEnterZone);
    }

    private boolean onPlayerEnterZone(ServerPlayerEntity player, Box zone) {
        // Ваши условия для разрешения/запрещения входа в зону
        // Например, запретить вход игрокам с именем "Notch"
        if (player.getName().getString().equals("KISEL03ru")) {
            return true;
        }
        return false;
    }
}
