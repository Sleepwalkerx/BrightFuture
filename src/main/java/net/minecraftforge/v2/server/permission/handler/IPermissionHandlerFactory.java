/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.v2.server.permission.handler;

import net.minecraftforge.v2.server.permission.nodes.PermissionNode;

import java.util.Collection;

@FunctionalInterface
public interface IPermissionHandlerFactory
{
    IPermissionHandler create(Collection<PermissionNode<?>> permissions);
}
