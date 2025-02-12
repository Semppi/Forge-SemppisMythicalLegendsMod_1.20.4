package net.semppi.semppis_mythical_legends_mod.entity;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface PlayerLinkedEntity {
    void setLinkedPlayer(Player player);
    @Nullable Player getLinkedPlayer();
    boolean isLinkedToPlayer(Player player);
}