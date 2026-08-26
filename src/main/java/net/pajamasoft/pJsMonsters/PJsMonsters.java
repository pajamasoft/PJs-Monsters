package net.pajamasoft.pJsMonsters;

import net.pajamasoft.pjcomputers.PJComputers;
import net.pajamasoft.pjenchants.PJEnchants;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public final class PJsMonsters extends JavaPlugin {

    public static PJEnchants pje = null;
    public static PJComputers pjc = null;
    @Override
    public void onEnable() {


        try{
            pjc = (PJComputers) Bukkit.getPluginManager().getPlugin("PJComputers");
        }catch(Exception ex){
            //
        }
        try{
            pje = (PJEnchants) Bukkit.getPluginManager().getPlugin("PJEnchants");
        }catch(Exception ex){
            //
        }

        getLogger().info("[PJsMonsters] Plugin is active");
        getServer().getPluginManager().registerEvents(new listener(this), this);
        this.getCommand("monsters").setExecutor(new commands());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static boolean isMonster(Entity ent, MonsterType type){
        return false;
    }
}
