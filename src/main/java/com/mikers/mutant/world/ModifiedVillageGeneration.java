package com.mikers.mutant.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModifiedVillageGeneration {
    
    public static void modifyVillages() {
        // Отключаем спавн жителей в деревнях
        System.setProperty("forge.disable_villager_spawn", "true");
    }
    
    @SubscribeEvent
    public void onVillageGeneration(BlockEvent.EntityPlaceEvent event) {
        // Здесь можно добавить логику замены обычных деревень на заброшенные
        // Это упрощенная версия - для полноценной реализации нужно изменять генерацию структур
    }
}