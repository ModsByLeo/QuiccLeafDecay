package adudecalledleo.quiccleafdecay;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;

public final class QuiccLeafDecayListener implements Listener {
    private final Logger logger;
    private final QuiccLeafDecay plugin;
    private final Set<Location> locationsBeingProcessed;

    public QuiccLeafDecayListener(Logger logger, QuiccLeafDecay plugin) {
        this.logger = logger;
        this.plugin = plugin;

        this.locationsBeingProcessed = Collections.synchronizedSet(new HashSet<>());
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        decayLeavesAroundBlock(e.getBlock());
    }

    private void decayLeavesAroundBlock(Block block) {
        var world = block.getWorld();
        var loc = block.getLocation();

        long delay = 1;
        for (int y = -1; y <= 1; y++) {
            for (int z = -4; z <= 4; z++) {
                for (int x = -4; x <= 4; x++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    var otherLoc = new Location(world, loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
                    var otherBlock = world.getBlockAt(otherLoc);
                    if (otherBlock.getBlockData() instanceof Leaves leaves) {
                        if (!leaves.isPersistent() && leaves.getDistance() >= 5) {
                            // don't repeatedly scan around the same location
                            if (this.locationsBeingProcessed.add(otherLoc)) {
                                // stagger subsequent scans, so we don't accidentally murder the server
                                this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                                    LeavesDecayEvent e = new LeavesDecayEvent(otherBlock);
                                    this.plugin.getServer().getPluginManager().callEvent(e);
                                    if (!e.isCancelled()) {
                                        block.breakNaturally(false);
                                    }
                                    this.locationsBeingProcessed.remove(otherLoc);
                                }, delay++);
                            }
                        }
                    }
                }
            }
        }
    }
}
