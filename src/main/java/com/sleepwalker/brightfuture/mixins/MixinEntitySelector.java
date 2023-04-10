package com.sleepwalker.brightfuture.mixins;

import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraftforge.v2.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nonnull;

@Mixin(EntitySelector.class)
public class MixinEntitySelector {

    @Redirect(
        method = "checkPermissions(Lnet/minecraft/command/CommandSource;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/command/CommandSource;hasPermission(I)Z")
    )
    private boolean hasPermissionRedirect(@Nonnull CommandSource source, int level){
        return ForgeHooks.canUseEntitySelectors(source);
    }
}
