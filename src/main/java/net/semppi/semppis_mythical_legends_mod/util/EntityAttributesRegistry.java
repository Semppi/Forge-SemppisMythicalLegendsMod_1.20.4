package net.semppi.semppis_mythical_legends_mod.util;

import net.minecraft.world.entity.EntityType;
import net.semppi.semppis_mythical_legends_mod.entity.ModEntities;

import java.util.HashMap;
import java.util.Map;

public class EntityAttributesRegistry {

    public static class Attributes {
        public final double width;
        public final double height;
        public final double movementSpeed;

        public Attributes(double width, double height, double movementSpeed) {
            this.width = width;
            this.height = height;
            this.movementSpeed = movementSpeed;
        }
    }

    private static final Map<EntityType<?>, Attributes> ENTITY_ATTRIBUTES = new HashMap<>();

    static {
        // Register hitboxes and speeds for various entities
        register(EntityType.CAT, new Attributes(0.6, 0.7, 0.3)); // Example vanilla entity
        register(ModEntities.SATYR.get(), new Attributes(0.5, 1.7, 0.4)); // Mod entity
        register(ModEntities.WENDIGO.get(), new Attributes(0.6, 2.9, 0.4)); // Mod entity
        register(ModEntities.PROTO_WENDIGO.get(), new Attributes(0.5, 1.9, 0.27)); // Mod entity
        // Add more vanilla and custom entities as needed
    }


    private static void register(EntityType<?> type, Attributes attributes) {
        ENTITY_ATTRIBUTES.put(type, attributes);
    }

    public static Attributes getAttributes(EntityType<?> type) {
        return ENTITY_ATTRIBUTES.getOrDefault(type, new Attributes(0.6, 1.8, 0.1)); // Default player attributes
    }
}