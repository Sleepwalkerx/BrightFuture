package net.minecraftforge.v2.server.permission.nodes;

import java.util.function.Function;

/**
 * Represents a key that can be used to build a {@link PermissionDynamicContext}.
 *
 * <p>Keys, along with their associated values, can be used to provide additional context for a permission handler
 * in determining whether to grant permission for an actor and a specific node.</p>
 *
 * <p>As an example usage, a dimension context key could be used inside a building permission
 * check to ensure that the actor can build given those constraints.</p>
 */
public class PermissionDynamicContextKey<T> {

    private final Class<T> typeToken;
    private final String name;
    private final Function<T, String> serializer;

    public PermissionDynamicContextKey(Class<T> typeToken, String name, Function<T, String> serializer){
        this.typeToken = typeToken;
        this.name = name;
        this.serializer = serializer;
    }

    public Class<T> getTypeToken() {
        return typeToken;
    }

    public String getName() {
        return name;
    }

    public Function<T, String> getSerializer() {
        return serializer;
    }

    public PermissionDynamicContext<T> createContext(T value)
    {
        return new PermissionDynamicContext<>(this, value);
    }
}
