package com.example.mixin;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.text.Text;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.example.PlayerEnterZoneCallback;

@Mixin(ServerPlayerEntity.class)
public class PlayerMoveMixin {

    private boolean wasInZone = false; // Флаг, чтобы отслеживать, был ли игрок в зоне на предыдущем тике
    private BlockPos lastValidPos; // Последняя допустимая позиция игрока
    private final Random random = new Random(); // Переменная для генерации случайных чисел
    private String massage;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onPlayerMove(CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        BlockPos playerPos = player.getBlockPos();
        ServerWorld world = (ServerWorld) player.getWorld(); // Приведение типов
        int x1 = 100;
        int y1 = 64;
        int z1 = 100;
        int x2 = 110;
        int y2 = 70;
        int z2 = 110;

        // Определяем границы зоны (Box)
        Box restrictedZone = new Box(new BlockPos(x1, y1, z1), new BlockPos(x2, y2, z2));

        // Проверяем, находится ли игрок в зоне
        boolean isInZone = restrictedZone.contains(playerPos.getX(), playerPos.getY(), playerPos.getZ());
        boolean allowed = PlayerEnterZoneCallback.EVENT.invoker().allowEnter(player, restrictedZone);
        double teleportX = random.nextInt(x1 - 1, x1);
        double teleportZ = random.nextInt(z1 - 1, z1);
        String allowedMessage = allowed ? "Вход разрешен" : "Вход запрещен";
        // Если игрок заходит в зону
        if (isInZone && !wasInZone) {
            massage = "Вход в запрещенную зону";
            player.sendMessage(Text.of(massage), false);
            player.sendMessage(Text.of((allowedMessage)), false);
        }

        // Если игрок в зоне, блокируем его движение
        if (isInZone && !allowed) {
            // Возвращаем игрока на последнюю допустимую позицию
            player.teleport(teleportX, (y1+y2)/2, teleportZ);
        }
        for (int x = x1 + 1; x < x2; x++) {
            for (int z = z1 + 1; z < z2; z++) {
                world.spawnParticles(ParticleTypes.CRIMSON_SPORE, (double) x, 64, (double) z, 1, 0, 0, 0, 0);
            }
        }
        // Обновляем флаг
        wasInZone = isInZone;
    }
}
