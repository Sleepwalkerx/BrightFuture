package net.minecraftforge.v2.common;

import net.minecraft.v2.commands.Commands;
import net.minecraftforge.v2.server.permission.nodes.PermissionNode;
import net.minecraftforge.v2.server.permission.nodes.PermissionTypes;

public class ForgeMod {

    public static final PermissionNode<Boolean> USE_SELECTORS_PERMISSION = new PermissionNode<>("forge", "use_entity_selectors",
        PermissionTypes.BOOLEAN, (player, uuid, contexts) -> player != null && player.hasPermissions(Commands.LEVEL_GAMEMASTERS));
}
