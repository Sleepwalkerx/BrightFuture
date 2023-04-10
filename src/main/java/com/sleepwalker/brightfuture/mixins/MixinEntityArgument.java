package com.sleepwalker.brightfuture.mixins;

import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraftforge.v2.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nonnull;

@Mixin(EntityArgument.class)
public class MixinEntityArgument {

    @Redirect(
        method = "listSuggestions(Lcom/mojang/brigadier/context/CommandContext;Lcom/mojang/brigadier/suggestion/SuggestionsBuilder;)Ljava/util/concurrent/CompletableFuture;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/command/ISuggestionProvider;hasPermission(I)Z")
    )
    public boolean hasPermissionRedirect(@Nonnull ISuggestionProvider provider, int level){
        return ForgeHooks.canUseEntitySelectors(provider);
    }
}
