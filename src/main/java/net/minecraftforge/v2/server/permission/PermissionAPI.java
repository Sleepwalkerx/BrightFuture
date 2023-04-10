/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.v2.server.permission;

import com.sleepwalker.brightfuture.permission.PermissionAdapter;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.v2.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.v2.server.permission.exceptions.UnregisteredPermissionException;
import net.minecraftforge.v2.server.permission.handler.DefaultPermissionHandler;
import net.minecraftforge.v2.server.permission.handler.IPermissionHandler;
import net.minecraftforge.v2.server.permission.handler.IPermissionHandlerFactory;
import net.minecraftforge.v2.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.v2.server.permission.nodes.PermissionNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public final class PermissionAPI
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static IPermissionHandler activeHandler = null;

    public static Collection<PermissionNode<?>> getRegisteredNodes() {
        return activeHandler == null ? Collections.emptySet() : activeHandler.getRegisteredNodes();
    }

    private PermissionAPI() { }

    @Nullable
    public static ResourceLocation getActivePermissionHandler()
    {
        return activeHandler == null ? null : activeHandler.getIdentifier();
    }

    /**
     * <p>Queries a player's permission for a given node and contexts</p>
     * <p><strong>Warning:</strong> PermissionNodes <strong>must</strong> be registered using the
     * {@link PermissionGatherEvent.Nodes} event before querying.</p>
     *
     * @param player  player for which you want to check permissions
     * @param node    the PermissionNode for which you want to query
     * @param context optional array of PermissionDynamicContext, single entries will be ignored if they weren't
     *                registered to the node
     * @param <T>     type of the queried PermissionNode
     * @return a value of type {@code <T>}, that the combination of Player and PermissionNode map to, defaults to the
     * PermissionNodes default handler.
     * @throws UnregisteredPermissionException when the PermissionNode wasn't registered properly
     */
    public static <T> T getPermission(ServerPlayerEntity player, PermissionNode<T> node, PermissionDynamicContext<?>... context)
    {
        if (!activeHandler.getRegisteredNodes().contains(node)) throw new UnregisteredPermissionException(node);
        return activeHandler.getPermission(player, node, context);
    }

    /**
     * See {@link PermissionAPI#getPermission(ServerPlayerEntity, PermissionNode, PermissionDynamicContext[])}
     *
     * @param player  offline player for which you want to check permissions
     * @param node    the PermissionNode for which you want to query
     * @param context optional array of PermissionDynamicContext, single entries will be ignored if they weren't
     *                registered to the node
     * @param <T>     type of the queried PermissionNode
     * @return a value of type {@code <T>}, that the combination of Player and PermissionNode map to, defaults to the
     * PermissionNodes default handler.
     * @throws UnregisteredPermissionException when the PermissionNode wasn't registered properly
     */
    public static <T> T getOfflinePermission(UUID player, PermissionNode<T> node, PermissionDynamicContext<?>... context)
    {
        if (!activeHandler.getRegisteredNodes().contains(node)) throw new UnregisteredPermissionException(node);
        return activeHandler.getOfflinePermission(player, node, context);
    }


    /**
     * <p>Helper method for internal use only!</p>
     * <p>Initializes the active permission handler based on the users config.</p>
     */
    public static void initializePermissionAPI()
    {
        //TODO: Нету обходимости
        /*Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        if (callerClass != ServerLifecycleHooks.class)
        {
            LOGGER.warn("{} tried to initialize the PermissionAPI, this call will be ignored.", callerClass.getName());
            return;
        }*/

        PermissionAPI.activeHandler = null;

        PermissionGatherEvent.Handler handlerEvent = new PermissionGatherEvent.Handler();
        MinecraftForge.EVENT_BUS.post(handlerEvent);
        Map<ResourceLocation, IPermissionHandlerFactory> availableHandlers = handlerEvent.getAvailablePermissionHandlerFactories();

        try
        {

            LOGGER.info("Find PermHandlers {}", availableHandlers.size());
            availableHandlers.keySet().forEach(location -> LOGGER.info("Find PermHandler: {}", location));

            PermissionGatherEvent.SelectHandler selectHandlerEvent = new PermissionGatherEvent.SelectHandler(Collections.unmodifiableMap(availableHandlers));
            MinecraftForge.EVENT_BUS.post(selectHandlerEvent);

            ResourceLocation selectedPermissionHandler = selectHandlerEvent.getSelect(); //new ResourceLocation(ForgeConfig.SERVER.permissionHandler.get());
            if (!availableHandlers.containsKey(selectedPermissionHandler))
            {
                LOGGER.error("Unable to find configured permission handler {}, will use {}", selectedPermissionHandler, DefaultPermissionHandler.IDENTIFIER);
                selectedPermissionHandler = DefaultPermissionHandler.IDENTIFIER;
            }

            IPermissionHandlerFactory factory = availableHandlers.get(selectedPermissionHandler);

            PermissionGatherEvent.Nodes nodesEvent = new PermissionGatherEvent.Nodes();
            MinecraftForge.EVENT_BUS.post(nodesEvent);

            //TODO: Adapter Init
            nodesEvent.addNodes(PermissionAdapter.INSTANCE.getAllNodes());

            PermissionAPI.activeHandler = factory.create(nodesEvent.getNodes());

            if(!selectedPermissionHandler.equals(activeHandler.getIdentifier()))
                LOGGER.warn("Identifier for permission handler {} does not match registered one {}", activeHandler.getIdentifier(), selectedPermissionHandler);

            LOGGER.info("Successfully initialized permission handler {}", PermissionAPI.activeHandler.getIdentifier());
        }
        catch (ResourceLocationException e)
        {
            LOGGER.error("Error parsing config value 'permissionHandler'", e);
        }
    }
}
