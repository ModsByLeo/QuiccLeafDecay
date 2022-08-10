package adudecalledleo.quiccleafdecay;

import org.bukkit.plugin.java.JavaPlugin;

public final class QuiccLeafDecay extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new QuiccLeafDecayListener(getLogger(), this), this);
    }

    @Override
    public void onDisable() {}
}
