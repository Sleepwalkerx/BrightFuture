/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.v2.server.permission.handler;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.v2.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.v2.server.permission.nodes.PermissionNode;

import java.util.*;

public final class DefaultPermissionHandler implements IPermissionHandler
{
    public static final ResourceLocation IDENTIFIER = new ResourceLocation("forge", "default_handler");;
    private final Set<PermissionNode<?>> registeredNodes = new HashSet<>();
    private Set<PermissionNode<?>> immutableRegisteredNodes = Collections.unmodifiableSet(this.registeredNodes);

    public DefaultPermissionHandler(Collection<PermissionNode<?>> permissions)
    {
        this.registeredNodes.addAll(permissions);
    }

    @Override
    public ResourceLocation getIdentifier()
    {
        return IDENTIFIER;
    }

    @Override
    public Set<PermissionNode<?>> getRegisteredNodes()
    {
        return immutableRegisteredNodes;
    }

    @Override
    public <T> T getPermission(ServerPlayerEntity player, PermissionNode<T> node, PermissionDynamicContext<?>... context)
    {
        return node.getDefaultResolver().resolve(player, player.getUUID(), context);
    }

    @Override
    public <T> T getOfflinePermission(UUID player, PermissionNode<T> node, PermissionDynamicContext<?>... context)
    {
        return node.getDefaultResolver().resolve(null, player, context);
    }
}
