package com.sleepwalker.brightfuture.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.FMLHandshakeHandler;
import net.minecraftforge.v2.event.entity.player.PlayerNegotiationEvent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Mixin(FMLHandshakeHandler.class)
public class MixinFMLHandshakeHandler {

    @Shadow(remap = false)
    private NetworkManager manager;

    @Shadow(remap = false)
    private static Logger LOGGER;

    private boolean negotiationStarted;
    @Nonnull
    private final List<Future<Void>> pendingFutures = new ArrayList<>();

    @Inject(method = "tickServer", remap = false, at = @At("HEAD"))
    private void tickServerInject1(@Nonnull CallbackInfoReturnable<Boolean> cir){

        if(!negotiationStarted){
            GameProfile profile = ((ServerLoginNetHandler)manager.getPacketListener()).gameProfile;
            PlayerNegotiationEvent event = new PlayerNegotiationEvent(manager, profile, pendingFutures);
            MinecraftForge.EVENT_BUS.post(event);
            negotiationStarted = true;
        }
    }

    @Inject(method = "tickServer", remap = false, cancellable = true, at = @At(value = "INVOKE", target = "isEmpty"))
    private void tickServerInject2(@Nonnull CallbackInfoReturnable<Boolean> cir){

        pendingFutures.removeIf(future -> {
            if (!future.isDone()) {
                return false;
            }

            try {
                future.get();
            } catch (ExecutionException ex) {
                LOGGER.error("Error during negotiation", ex.getCause());
            } catch (CancellationException | InterruptedException ex) {
                // no-op
            }

            return true;
        });

        if(!pendingFutures.isEmpty()){
            cir.setReturnValue(false);
        }
    }
}
