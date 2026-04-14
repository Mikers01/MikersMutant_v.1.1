package com.mikers.mutant.world;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MobSpawnHandler {
    
    @SubscribeEvent
    public void onMobSpawn(MobSpawnEvent.FinalizeSpawn event) {
        EntityType<?> type = event.getEntity().getType();
        
        if (type == EntityType.ZOMBIE || type == EntityType.SKELETON || 
            type == EntityType.CREEPER || type == EntityType.ENDERMAN) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void onMobSpawnCheck(MobSpawnEvent.PositionCheck event) {
        EntityType<?> type = event.getEntityType();
        
        if (type == EntityType.ZOMBIE || type == EntityType.SKELETON || 
            type == EntityType.CREEPER || type == EntityType.ENDERMAN) {
            event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
        }
    }
}