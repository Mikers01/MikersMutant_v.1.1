package com.mikers.mutant.entity;

import com.mikers.mutant.MikersMutantMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MutantRenderer extends GeoEntityRenderer<MutantEntity> {
    
    public MutantRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MutantModel());
        this.shadowRadius = 0.8f;
    }
}