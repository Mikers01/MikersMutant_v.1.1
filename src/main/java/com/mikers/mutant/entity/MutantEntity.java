package com.mikers.mutant.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class MutantEntity extends Monster implements GeoEntity {
    
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int breakBlockCooldown = 0;
    private int sunDamageTimer = 0;
    
    public MutantEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setHealth(50.0f);
        this.maxUpStep = 2.0f;
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 50.0)
            .add(Attributes.MOVEMENT_SPEED, 0.45)
            .add(Attributes.ATTACK_DAMAGE, 10.0)
            .add(Attributes.ATTACK_KNOCKBACK, 1.5)
            .add(Attributes.JUMP_STRENGTH, 0.8)
            .add(Attributes.FOLLOW_RANGE, 32.0);
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LeapAtTargetGoal(this, 0.8f));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.5, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, true));
    }
    
    @Override
    public void tick() {
        super.tick();
        
        if (!this.level().isClientSide) {
            // Проверка на солнце
            if (this.level().canSeeSky(this.blockPosition()) && this.level().isDay()) {
                sunDamageTimer++;
                if (sunDamageTimer >= 20) {
                    this.hurt(this.damageSources().inFire(), 1000.0f);
                    sunDamageTimer = 0;
                }
            } else {
                sunDamageTimer = 0;
            }
            
            // Прыжок на 2 блока
            if (this.getTarget() != null && this.horizontalCollision && this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().x, 1.2, this.getDeltaMovement().z);
            }
            
            // Разрушение блоков
            if (breakBlockCooldown <= 0 && this.getTarget() != null) {
                breakBlocksAround();
                breakBlockCooldown = 20;
            } else {
                breakBlockCooldown--;
            }
            
            // Убийство мирных существ
            List<Animal> animals = this.level().getEntitiesOfClass(Animal.class, this.getBoundingBox().inflate(5));
            for (Animal animal : animals) {
                if (animal != this) {
                    animal.hurt(this.damageSources().mobAttack(this), 100.0f);
                }
            }
        }
    }
    
    private void breakBlocksAround() {
        BlockPos.betweenClosed(
            this.blockPosition().offset(-2, -1, -2),
            this.blockPosition().offset(2, 2, 2)
        ).forEach(pos -> {
            BlockState state = this.level().getBlockState(pos);
            Block block = state.getBlock();
            
            if (block == Blocks.DIRT || block == Blocks.GRASS_BLOCK || block == Blocks.STONE || 
                block == Blocks.COBBLESTONE || block == Blocks.OAK_LOG || block == Blocks.OAK_PLANKS ||
                block == Blocks.DIRT_PATH || block == Blocks.COARSE_DIRT) {
                this.level().destroyBlock(pos, true);
            }
        });
    }
    
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt) {
            // Прыжок на цель при атаке
            double d0 = target.getX() - this.getX();
            double d1 = target.getZ() - this.getZ();
            double d2 = Math.sqrt(d0 * d0 + d1 * d1);
            this.setDeltaMovement(this.getDeltaMovement().x, 0.8, this.getDeltaMovement().z);
        }
        return hurt;
    }
    
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, this::movementController));
        controllers.add(new AnimationController<>(this, "attack", 0, this::attackController));
    }
    
    private <T extends GeoAnimatable> PlayState movementController(AnimationState<T> state) {
        if (state.isMoving()) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("animation.mutant.walk"));
        }
        return state.setAndContinue(RawAnimation.begin().thenLoop("animation.mutant.idle"));
    }
    
    private <T extends GeoAnimatable> PlayState attackController(AnimationState<T> state) {
        if (this.swinging) {
            return state.setAndContinue(RawAnimation.begin().then("animation.mutant.attack", Animation.LoopType.PLAY_ONCE));
        }
        state.getController().forceAnimationReset();
        return PlayState.STOP;
    }
    
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}