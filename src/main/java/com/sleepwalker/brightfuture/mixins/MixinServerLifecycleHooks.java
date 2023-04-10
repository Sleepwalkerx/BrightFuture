package com.sleepwalker.brightfuture.mixins;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.v2.server.permission.PermissionAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;

@Mixin(ServerLifecycleHooks.class)
public class MixinServerLifecycleHooks {

    @Inject(method = "handleServerStarting", remap = false, at = @At("RETURN"))
    private static void handleServerStartingInject(@Nonnull MinecraftServer server, @Nonnull CallbackInfoReturnable<Boolean> cir){
        PermissionAPI.initializePermissionAPI();
    }
}
