package com.sleepwalker.brightfuture.mixins;

import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraftforge.v2.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nonnull;

@Mixin(MessageArgument.class)
public class MixinMessageArgument {

    @Redirect(
        method = "getMessage(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Lnet/minecraft/util/text/ITextComponent;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/command/CommandSource;hasPermission(I)Z")
    )
    private static boolean hasPermissionRedirect(@Nonnull CommandSource source, int level){
        return ForgeHooks.canUseEntitySelectors(source);
    }
}
