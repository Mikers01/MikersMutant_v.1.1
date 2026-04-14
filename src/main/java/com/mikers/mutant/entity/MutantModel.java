package com.mikers.mutant.entity;

import com.mikers.mutant.MikersMutantMod;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MutantModel extends GeoModel<MutantEntity> {
    
    @Override
    public ResourceLocation getModelResource(MutantEntity object) {
        return new ResourceLocation(MikersMutantMod.MOD_ID, "geo/mutant.geo.json");
    }
    
    @Override
    public ResourceLocation getTextureResource(MutantEntity object) {
        return new ResourceLocation(MikersMutantMod.MOD_ID, "textures/entity/mutant.png");
    }
    
    @Override
    public ResourceLocation getAnimationResource(MutantEntity animatable) {
        return new ResourceLocation(MikersMutantMod.MOD_ID, "animations/mutant.animation.json");
    }
}