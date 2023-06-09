package net.minecraftforge.v2.server.permission.events;

import com.google.common.base.Preconditions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.v2.server.permission.handler.DefaultPermissionHandler;
import net.minecraftforge.v2.server.permission.handler.IPermissionHandler;
import net.minecraftforge.v2.server.permission.handler.IPermissionHandlerFactory;
import net.minecraftforge.v2.server.permission.nodes.PermissionNode;

import java.util.*;

/**
 * Fired to gather information for the permissions API, such as the {@link IPermissionHandler} and {@link PermissionNode}s.
 *
 * <p>
 * {@link Handler} allows to set a new PermissionHandler<br/>
 * {@link Nodes} allows you to register new PermissionNodes</p>
 *
 * <p><strong>Note:</strong> All PermissionNodes that you want to use, <strong>must</strong> be registered!</p>
 */
public class PermissionGatherEvent extends Event
{

    /**
     * Used to register a new PermissionHandler, a server config value exists to choose which one to use.
     * <p>Note: Create a new instance when registering a PermissionHandler.
     * If you cache it, make sure that your PermissionHandler is actually used after this event.</p>
     */
    public static class Handler extends PermissionGatherEvent
    {
        private Map<ResourceLocation, IPermissionHandlerFactory> availableHandlers = new HashMap<>();

        public Handler()
        {
            availableHandlers.put(DefaultPermissionHandler.IDENTIFIER, DefaultPermissionHandler::new);
        }

        public Map<ResourceLocation, IPermissionHandlerFactory> getAvailablePermissionHandlerFactories()
        {
            return Collections.unmodifiableMap(availableHandlers);
        }

        public void addPermissionHandler(ResourceLocation identifier, IPermissionHandlerFactory handlerFactory)
        {
            Preconditions.checkNotNull(identifier, "Permission handler identifier cannot be null!");
            Preconditions.checkNotNull(handlerFactory, "Permission handler cannot be null!");
            if(this.availableHandlers.containsKey(identifier))
                throw new IllegalArgumentException("Attempted to overwrite permission handler " + identifier + ", this is not allowed.");
            this.availableHandlers.put(identifier, handlerFactory);
        }
    }

    /**
     * Select PermissionHandler event
     * */
    public static class SelectHandler extends PermissionGatherEvent {

        private final Map<ResourceLocation, IPermissionHandlerFactory> availableHandlers;
        private ResourceLocation select;

        public SelectHandler(Map<ResourceLocation, IPermissionHandlerFactory> availableHandlers) {
            this.availableHandlers = availableHandlers;
        }

        public void setSelect(ResourceLocation select) {
            this.select = select;
        }

        public ResourceLocation getSelect() {
            return select;
        }

        public Map<ResourceLocation, IPermissionHandlerFactory> getAvailableHandlers() {
            return availableHandlers;
        }
    }

    /**
     * Used to register your PermissionNodes, <strong>every node that you want to use, must be registered!</strong>
     */
    public static class Nodes extends PermissionGatherEvent
    {
        private final Set<PermissionNode<?>> nodes = new HashSet<>();

        public Nodes()
        {
        }

        public Collection<PermissionNode<?>> getNodes()
        {
            return Collections.unmodifiableCollection(this.nodes);
        }

        public void addNodes(PermissionNode<?>... nodes)
        {
            for (PermissionNode<?> node : nodes)
            {
                if (!this.nodes.add(node))
                    throw new IllegalArgumentException("Tried to register duplicate PermissionNode '" + node.getNodeName() + "'");
            }
        }

        public void addNodes(Iterable<PermissionNode<?>> nodes)
        {
            for (PermissionNode<?> node : nodes)
            {
                if (!this.nodes.add(node))
                    throw new IllegalArgumentException("Tried to register duplicate PermissionNode '" + node.getNodeName() + "'");
            }
        }
    }
}
