package com.sleepwalker.brightfuture.mixins;

import com.sleepwalker.brightfuture.permission.PermissionAdapter;
import net.minecraftforge.server.permission.IPermissionHandler;
import net.minecraftforge.server.permission.PermissionAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;

@Mixin(PermissionAPI.class)
public class MixinPermissionAPI {

    @Shadow(remap = false)
    private static IPermissionHandler permissionHandler;

    static {
        permissionHandler = PermissionAdapter.INSTANCE;
    }

    @Inject(method = "setPermissionHandler", cancellable = true, remap = false, at = @At("HEAD"))
    public static void setPermissionHandler(@Nonnull IPermissionHandler handler, @Nonnull CallbackInfo ci){
        ci.cancel();
    }
}
