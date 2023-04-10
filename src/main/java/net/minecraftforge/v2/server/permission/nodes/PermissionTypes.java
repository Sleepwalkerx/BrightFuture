/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.v2.server.permission.nodes;

import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

/**
 * Default PermissionTypes, if you need additional ones, please PR it.
 */
public final class PermissionTypes
{
    public static final PermissionType<Boolean> BOOLEAN = new PermissionType<>(Boolean.class, "boolean");
    public static final PermissionType<Integer> INTEGER = new PermissionType<>(Integer.class, "integer");
    public static final PermissionType<String> STRING = new PermissionType<>(String.class, "string");
    public static final PermissionType<ITextComponent> COMPONENT = new PermissionType<>(ITextComponent.class, "component");

    private PermissionTypes()
    {
    }

    @Nullable
    public static PermissionType<?> getTypeByName(String name)
    {
        switch (name){
            case "boolean":
                return BOOLEAN;
            case "integer":
                return INTEGER;
            case "string":
                return STRING;
            case "component":
                return COMPONENT;
            default:
                return null;
        }
    }
}
