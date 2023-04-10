package net.minecraftforge.v2.common;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.v2.commands.Commands;
import net.minecraftforge.v2.server.permission.PermissionAPI;

import javax.annotation.Nonnull;

public class ForgeHooks {

    public static boolean canUseEntitySelectors(@Nonnull ISuggestionProvider provider){

        if (provider.hasPermission(Commands.LEVEL_GAMEMASTERS)) {
            return true;
        }
        else if (provider instanceof CommandSource) {
            CommandSource source = (CommandSource) provider;
            if(source.getEntity() instanceof ServerPlayerEntity){
                return PermissionAPI.getPermission((ServerPlayerEntity) source.getEntity(), ForgeMod.USE_SELECTORS_PERMISSION);
            }
        }
        return false;
    }
}
