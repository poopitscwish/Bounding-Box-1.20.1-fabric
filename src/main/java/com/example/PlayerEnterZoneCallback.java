package com.example;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PlayerEnterZoneCallback {
    Event<PlayerEnterZoneCallback> EVENT = EventFactory.createArrayBacked(PlayerEnterZoneCallback.class,
        (listeners) -> (player, zone) -> {
            for (PlayerEnterZoneCallback listener : listeners) {
                if (!listener.allowEnter(player, zone)) {
                    return false;
                }
            }
            return true;
        });

    boolean allowEnter(ServerPlayerEntity player, Box zone);
}
