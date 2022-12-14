package me.swipez.vehicles.commands;

import it.unimi.dsi.fastutil.shorts.AbstractShort2ReferenceSortedMap;
import me.swipez.vehicles.ArmorStandCreation;
import me.swipez.vehicles.VehiclesPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ArmorStandMakeCommand implements CommandExecutor {

    public static Location firstCorner;
    public static Location secondCorner;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!VehiclesPlugin.creatorModeActive){
            return true;
        }
        if (sender instanceof Player player){
            // This command converts blocks to armor stands.
            double distance = 0.625;
            if (firstCorner != null){
                if (secondCorner != null){
                    HashMap<Location, BlockData> relativeAndMat = new HashMap<>();
                    for (int x = firstCorner.getBlockX(); x <= secondCorner.getBlockX(); x++){
                        for (int y = firstCorner.getBlockY(); y <= secondCorner.getBlockY(); y++){
                            for (int z = firstCorner.getBlockZ(); z <= secondCorner.getBlockZ(); z++){
                                Block block = firstCorner.getWorld().getBlockAt(x, y, z);
                                if (!block.getType().isAir() && !block.getType().equals(Material.BARRIER)){
                                    relativeAndMat.put(block.getLocation().clone().subtract(firstCorner), block.getBlockData());
                                }
                            }
                        }
                    }
                    Location origin = player.getLocation().clone();
                    List<UUID> armorStands = new ArrayList<>();
                    for (Location location : relativeAndMat.keySet()){
                        Location armorStandAddition = location.clone().multiply(distance).add(origin);
                        ArmorStand armorStand = (ArmorStand) armorStandAddition.getWorld().spawnEntity(armorStandAddition, EntityType.ARMOR_STAND);
                        armorStand.getEquipment().setHelmet(new ItemStack(relativeAndMat.get(location).getMaterial()));
                        armorStand.setGravity(false);
                        armorStand.setVisible(false);
                        armorStand.setInvulnerable(true);
                        armorStands.add(armorStand.getUniqueId());
                    }
                    ArmorStandCreation armorStandCreation = new ArmorStandCreation(origin);
                    armorStandCreation.addAll(armorStands);
                    CreationModeCommand.creationHashMap.put(player.getUniqueId(), armorStandCreation);
                }
            }
        }
        return true;
    }
}
