package com.sleepwalker.brightfuture.permission;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.StateHolder;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.v2.commands.Commands;
import net.minecraft.world.World;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.IPermissionHandler;
import net.minecraftforge.server.permission.context.ContextKeys;
import net.minecraftforge.server.permission.context.IContext;
import net.minecraftforge.v2.server.permission.PermissionAPI;
import net.minecraftforge.v2.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.v2.server.permission.nodes.PermissionDynamicContextKey;
import net.minecraftforge.v2.server.permission.nodes.PermissionNode;
import net.minecraftforge.v2.server.permission.nodes.PermissionTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class PermissionAdapter implements IPermissionHandler {

    @Nonnull
    public static final PermissionAdapter INSTANCE = new PermissionAdapter();

    @Nonnull
    private final Map<String, PermissionNode<?>> allNodes = new HashMap<>();

    @Nonnull
    private final PermissionDynamicContextKey<World> worldContext = new PermissionDynamicContextKey<>(
        World.class, "world", world -> world.dimension().location().toString()
    );

    @Nonnull
    private final PermissionDynamicContextKey<AxisAlignedBB> areaContext = new PermissionDynamicContextKey<>(
        AxisAlignedBB.class, "area", AxisAlignedBB::toString
    );

    @Nonnull
    private final PermissionDynamicContextKey<BlockPos> posContext = new PermissionDynamicContextKey<>(
        BlockPos.class, "pos", BlockPos::toString
    );

    @Nonnull
    private final PermissionDynamicContextKey<Entity> targetContext = new PermissionDynamicContextKey<>(
        Entity.class, "target", entity -> Objects.requireNonNull(entity.getType().getRegistryName()).toString()
    );

    @Nonnull
    private final PermissionDynamicContextKey<Direction> facingContext = new PermissionDynamicContextKey<>(
        Direction.class, "facing", Direction::getName
    );

    @Nonnull
    private final PermissionDynamicContextKey<BlockState> blockstateContext = new PermissionDynamicContextKey<>(
        BlockState.class, "blockstate", StateHolder::toString
    );

    @Override
    public void registerNode(@Nonnull String node, @Nonnull DefaultPermissionLevel level, @Nonnull String desc) {
        PermissionNode<Boolean> permissionNode = new PermissionNode<>(node, PermissionTypes.BOOLEAN, new DefaultResolver(PermLevel.resolve(level)));
        if(desc.length() != 0){
            permissionNode.setInformation(new StringTextComponent(node), new StringTextComponent(desc));
        }
        allNodes.put(node, permissionNode);
    }

    @Nonnull
    @Override
    public Collection<String> getRegisteredNodes() {
        return PermissionAPI.getRegisteredNodes().stream().map(PermissionNode::getNodeName).collect(Collectors.toList());
    }

    @Override
    public boolean hasPermission(@Nonnull GameProfile profile, @Nonnull String node, @Nullable IContext context) {
        PermissionNode<Boolean> createNode = new PermissionNode<>(node, PermissionTypes.BOOLEAN, new DefaultResolver(PermLevel.OP));
        Boolean state;
        if(context != null){
            state = PermissionAPI.getOfflinePermission(profile.getId(), createNode, convertContext(context).toArray(new PermissionDynamicContext[0]));
        }
        else {
            state = PermissionAPI.getOfflinePermission(profile.getId(), createNode);
        }
        return state != null && state;
    }

    @Nonnull
    @Override
    public String getNodeDescription(@Nonnull String node) {
        return PermissionAPI.getRegisteredNodes().stream()
            .filter(n -> n.getNodeName().equals(node))
            .findFirst()
            .flatMap(n -> Optional.ofNullable(n.getDescription()))
            .map(ITextComponent::getString)
            .orElse("");
    }

    @Nonnull
    public Collection<PermissionNode<?>> getAllNodes() {
        return allNodes.values();
    }

    @Nonnull
    private List<PermissionDynamicContext<?>> convertContext(@Nonnull IContext context){

        List<PermissionDynamicContext<?>> contexts = new ArrayList<>();
        if(context.has(ContextKeys.POS)){
            contexts.add(posContext.createContext(context.get(ContextKeys.POS)));
        }

        if(context.has(ContextKeys.BLOCK_STATE)){
            contexts.add(blockstateContext.createContext(context.get(ContextKeys.BLOCK_STATE)));
        }

        if(context.has(ContextKeys.FACING)){
            contexts.add(facingContext.createContext(context.get(ContextKeys.FACING)));
        }

        if (context.has(ContextKeys.TARGET)){
            contexts.add(targetContext.createContext(context.get(ContextKeys.TARGET)));
        }

        if (context.has(ContextKeys.AREA)) {
            contexts.add(areaContext.createContext(context.get(ContextKeys.AREA)));
        }

        if(context.getWorld() != null){
            contexts.add(worldContext.createContext(context.getWorld()));
        }

        return contexts;
    }

    public static class DefaultResolver implements PermissionNode.PermissionResolver<Boolean> {

        @Nonnull
        private final PermLevel level;

        public DefaultResolver(@Nonnull PermLevel level) {
            this.level = level;
        }

        @Override
        public Boolean resolve(@Nullable ServerPlayerEntity player, UUID playerUUID, PermissionDynamicContext<?>... context) {
            return level.hasPermission(player, playerUUID);
        }
    }

    public enum PermLevel {
        ALL{
            @Override
            public boolean hasPermission(@Nullable ServerPlayerEntity player, UUID playerUUID) {
                return true;
            }
        },
        OP {
            @Override
            public boolean hasPermission(@Nullable ServerPlayerEntity player, UUID playerUUID) {
                if(player != null){
                    return player.hasPermissions(Commands.LEVEL_OWNERS);
                }
                else {
                    return false;
                }
            }
        },
        NONE {
            @Override
            public boolean hasPermission(@Nullable ServerPlayerEntity player, UUID playerUUID) {
                return false;
            }
        };

        public abstract boolean hasPermission(@Nullable ServerPlayerEntity player, UUID playerUUID);

        @Nonnull
        public static PermLevel resolve(@Nonnull DefaultPermissionLevel level){
            if(level == DefaultPermissionLevel.ALL){
                return ALL;
            }
            else return level == DefaultPermissionLevel.NONE ? NONE : OP;
        }
    }
}
