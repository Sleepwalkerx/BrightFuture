package net.minecraftforge.v2.event.entity.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class PlayerNegotiationEvent extends Event {

    @Nonnull
    private final NetworkManager networkManager;
    @Nonnull
    private final GameProfile profile;
    @Nonnull
    private final List<Future<Void>> futures;

    public PlayerNegotiationEvent(@Nonnull NetworkManager networkManager, @Nonnull GameProfile profile, @Nonnull List<Future<Void>> futures) {
        this.networkManager = networkManager;
        this.profile = profile;
        this.futures = futures;
    }

    @Nonnull
    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    public void enqueueWork(@Nonnull Runnable runnable) {
        enqueueWork(CompletableFuture.runAsync(runnable));
    }

    public void enqueueWork(@Nonnull Future<Void> future) {
        futures.add(future);
    }

    @Nonnull
    public GameProfile getProfile() {
        return profile;
    }
}
