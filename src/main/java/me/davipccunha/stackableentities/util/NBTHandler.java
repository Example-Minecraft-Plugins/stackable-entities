package me.davipccunha.stackableentities.util;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class NBTHandler {
    public static Entity addNBT(Entity entity, String key, String value) {

        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        NBTTagCompound compound = nmsEntity.getNBTTag() == null ? new NBTTagCompound() : nmsEntity.getNBTTag();

        compound.setString(key, value);
        nmsEntity.c(compound);
        nmsEntity.f(compound);

        if (nmsEntity.getNBTTag() != null) System.out.println(nmsEntity.getNBTTag().getString(key));
        return nmsEntity.getBukkitEntity();
    }

    public static String getNBT(Entity entity, String key) {
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        NBTTagCompound compound = nmsEntity.getNBTTag() == null ? new NBTTagCompound() : nmsEntity.getNBTTag();

        return compound.hasKey(key) ? compound.getString(key) : null;
    }
}