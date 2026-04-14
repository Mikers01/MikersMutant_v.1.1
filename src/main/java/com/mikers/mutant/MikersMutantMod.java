package com.mikers.mutant;

import com.mikers.mutant.entity.MutantEntity;
import com.mikers.mutant.world.MobSpawnHandler;
import com.mikers.mutant.world.ModifiedVillageGeneration;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.geckolib.GeckoLib;

@Mod(MikersMutantMod.MOD_ID)
public class MikersMutantMod {
    public static final String MOD_ID = "mikers_mutant";
    
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);
    
    public static final RegistryObject<EntityType<MutantEntity>> MUTANT = 
        ENTITY_TYPES.register("mutant",
            () -> EntityType.Builder.of(MutantEntity::new, MobCategory.MONSTER)
                .sized(1.2f, 3.0f)
                .build("mutant"));
    
    public MikersMutantMod() {
        GeckoLib.initialize();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        ENTITY_TYPES.register(modEventBus);
        
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new MobSpawnHandler());
        MinecraftForge.EVENT_BUS.register(new ModifiedVillageGeneration());
        
        modEventBus.addListener(this::commonSetup);
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModifiedVillageGeneration.modifyVillages();
        });
    }
}