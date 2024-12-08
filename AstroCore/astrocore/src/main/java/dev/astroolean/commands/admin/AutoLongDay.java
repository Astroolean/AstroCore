package dev.astroolean.commands.admin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.GameRule;

public final class AutoLongDay {
    private HashMap<World, Integer> taskIDs;

    public void onEnable(JavaPlugin plugin) {
        this.taskIDs = new HashMap<>();
        // Schedule the task to run after enabling the plugin
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Iterator<World> worldIterator = Bukkit.getWorlds().iterator();

            while (worldIterator.hasNext()) {
                final World world = worldIterator.next();
                // Check if the world is not Nether or The End and has daylight cycle enabled
                if (world.getEnvironment() != Environment.NETHER 
                    && world.getEnvironment() != Environment.THE_END 
                    && Boolean.TRUE.equals(world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE))) {
                    
                    int taskID = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
                        private int tickCounter = 0;

                        @Override
                        public void run() {
                            long time = world.getTime();
                            // Control day-night cycle
                            if (time > 0L && time < 12000L && tickCounter % 2 == 0) {
                                world.setTime(time - 1L); // Set time back to maintain daylight
                            } else if (tickCounter % 2 == 0) {
                                world.setTime(time + 1L); // Set time forward
                            }
                            
                            tickCounter++;
                            if (tickCounter > 2) {
                                tickCounter = 0; // Reset tick counter
                            }
                        }
                    }, 1L, 1L).getTaskId();
                    
                    this.taskIDs.put(world, taskID);
                }
            }
        }, 1L);
    }

    public void onDisable() {
        // Cancel all running tasks when the plugin is disabled
        Collection<Integer> taskIds = this.taskIDs.values();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        Objects.requireNonNull(scheduler);
        taskIds.forEach(scheduler::cancelTask);
    }
}
