package com.mrbysco.tinytasks.mixin;

import com.mrbysco.tinytasks.handler.TaskHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodProperties.class)
public class FoodPropertiesMixin {
	@Inject(method = "onConsume(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/component/Consumable;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V",
					shift = At.Shift.AFTER
			)
	)
	public void tinytasks$eat(Level level, LivingEntity livingEntity, ItemStack stack, Consumable consumable, CallbackInfo ci) {
		TaskHandler.onEat(livingEntity, stack);
	}
}
