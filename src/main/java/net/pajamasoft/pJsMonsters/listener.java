package net.pajamasoft.pJsMonsters;

import net.pajamasoft.pjcomputers.PJPlayer;
import net.pajamasoft.pjcomputers.TitleType;
import net.pajamasoft.pjenchants.Enchant;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

import static net.pajamasoft.pJsMonsters.PJsMonsters.pjc;
import static net.pajamasoft.pJsMonsters.PJsMonsters.pje;
import static net.pajamasoft.pjLib.PJLib.*;
import static net.pajamasoft.pjenchants.PJEnchants.genEnchantingScroll;

public class listener implements Listener {

    private static PJsMonsters pjm;

    listener(PJsMonsters pjm) {
        this.pjm = pjm;
    }


    @EventHandler
    public void onMobKill(EntityDeathEvent e){
        LivingEntity ent = e.getEntity();
        EntityType type = ent.getType();

        Player p = ent.getKiller();

        int looting = 1;
        if(p != null && p.getInventory().getItemInMainHand().hasItemMeta()){
            ItemStack sword = p.getInventory().getItemInMainHand();
            if(sword.getEnchantments().containsKey(Enchantment.LOOTING))
                looting += sword.getEnchantments().get(Enchantment.LOOTING);
        }

        if(ent instanceof Zombie)
            if(ent.isInsideVehicle())
                if(ent.getVehicle() instanceof ZombieHorse)
                    ent.getVehicle().remove();

        if(ent.getCustomName() != null)
            e.getDrops().removeIf(i -> i.getType().toString().contains("NETHERITE"));

        NamespacedKey key = new NamespacedKey(pjm,"PJsMonster");
        if(ent.getPersistentDataContainer().has(key)) { // Drops for custom mobs
            MonsterType monType;
            String str = ent.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if (str.startsWith("SIZE_")) {
                double scale = Double.parseDouble(str.substring(5));
                e.setDroppedExp((int) (e.getDroppedExp() * scale) + 1);
            }
            try {
                monType = MonsterType.valueOf(str);
            } catch (Exception ex) {
                return;
            }
            int tier = monType.getTier();
            e.setDroppedExp(e.getDroppedExp() + 3 * tier);
            List<ItemStack> drops = e.getDrops();

            if(pjc != null && p != null) {
                PJPlayer pjp = pjc.findPlayer(p.getUniqueId());
                drops.add(new ItemStack(Material.EMERALD,1 + (int)(Math.random()*(5*tier+looting))));
                switch (monType) {
                    case ZOMBIE_COMMANDER -> {
                        for (ItemStack item : drops) {
                            if (item != null)
                                if (item.getType() == Material.DIAMOND_SWORD && pjp != null)
                                    pjp.unlockTitle(TitleType.COMMANDER);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent e){

        if(!(e.getEntity() instanceof Monster mon)) // Needs to be expanded on if making big wolves
            return;

        EntityType type = e.getEntityType();
        final List<EntityType> excluded = Arrays.asList(EntityType.ENDER_DRAGON,EntityType.WITHER,EntityType.SLIME,EntityType.MAGMA_CUBE);
        final List<EntityType> cantBeSmaller = Arrays.asList(EntityType.PHANTOM,EntityType.CAVE_SPIDER,EntityType.SILVERFISH,EntityType.ENDERMITE);

        if(excluded.contains(type))
            return;

        boolean isBaby = false;
        if(mon instanceof Ageable ageable)
            isBaby = !ageable.isAdult();

        if(!isBaby && percentChance(20)){ // Random scale variants
            NamespacedKey key = new NamespacedKey(pjm,"PJsMonster");
            double variance = 0.8;
            double rscale = Math.random()*variance + 1-variance/2.0;

            if(cantBeSmaller.contains(mon.getType()))
                rscale += variance/2.0;

            if(mon.getAttribute(Attribute.SCALE) != null)
                mon.getAttribute(Attribute.SCALE).setBaseValue(rscale);
            if(mon.getAttribute(Attribute.ATTACK_DAMAGE) != null)
                mon.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(mon.getAttribute(Attribute.ATTACK_DAMAGE).getValue() * rscale);
            if(mon.getAttribute(Attribute.MOVEMENT_SPEED) != null)
                mon.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(mon.getAttribute(Attribute.MOVEMENT_SPEED).getValue() * (2 - rscale));
            if(mon.getAttribute(Attribute.MAX_HEALTH) != null) {
                mon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(mon.getAttribute(Attribute.MAX_HEALTH).getValue() * rscale);
                mon.setHealth(mon.getAttribute(Attribute.MAX_HEALTH).getValue());
            }

            if(rscale != 1){
                double percent = rscale - (1 - variance/2.0);
                percent /= variance;
                int size = (int)(10*percent);
                mon.setCustomNameVisible(false);
                mon.setCustomName("§7(Size "+size+"§7)");
                String size_clamped = String.valueOf(rscale);
                size_clamped = size_clamped.substring(0, Math.min(size_clamped.length(), 4));
                mon.getPersistentDataContainer().set(key, PersistentDataType.STRING, "SIZE_"+size_clamped);
            }

            if(mon instanceof Creeper creep)
                creep.setExplosionRadius(3 * (int)(rscale * 2) - 1);
            return;
        }

        if(e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER)
            return;

        for(MonsterType monType:MonsterType.values()){
            if(percentChance(monType.getSpawnChance())) {
                pjm.makeSpecialMob(mon,monType);
            }
        }
    }

}
