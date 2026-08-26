package net.pajamasoft.pJsMonsters;

import net.pajamasoft.pjcomputers.PJPlayer;
import net.pajamasoft.pjcomputers.TitleType;
import net.pajamasoft.pjenchants.Enchant;
import net.pajamasoft.pjenchants.PJEnchants;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

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

        if(ent instanceof Zombie)
            if(ent.isInsideVehicle())
                if(ent.getVehicle() instanceof ZombieHorse)
                    ent.getVehicle().remove();

        if(ent.getCustomName() != null)
            e.getDrops().removeIf(i -> i.getType().toString().contains("NETHERITE"));

        Player p = e.getEntity().getKiller();
        if(p == null)
            return;

        if(pje == null)
            return;

        PJPlayer pjp = null;
        if(pjc != null)
            pjp = pjc.findPlayer(p.getUniqueId());

        int looting = 1;
        if(p.getInventory().getItemInMainHand().hasItemMeta()){
            ItemStack sword = p.getInventory().getItemInMainHand();
            if(sword.getEnchantments().containsKey(Enchantment.LOOTING))
                looting += sword.getEnchantments().get(Enchantment.LOOTING);
        }

        if(type == EntityType.BLAZE) {
            if (percentChance(0.5 * looting)) {
                e.getDrops().add(genEnchantingScroll(Enchant.BLAZE, 0));
            }
            else if(pjp != null){
                if(percentChance(0.5 * looting) && pjc.isDisplayCompleted(pjp, pjc.display_nether))
                    e.getDrops().add(genEnchantingScroll(Enchant.FORGING,1));
            }
        }

        if(type == EntityType.BREEZE)
            if(percentChance(0.5 * looting))
                e.getDrops().add(genEnchantingScroll(Enchant.BREEZE,0));

        if(type == EntityType.WITHER_SKELETON)
            if(percentChance(0.5 * looting))
                e.getDrops().add(genEnchantingScroll(Enchant.WILTING,0));

        if(type == EntityType.WITHER)
            if(percentChance(5 * looting))
                e.getDrops().add(genEnchantingScroll(Enchant.SKULLS,0));

        if(type == EntityType.WARDEN && pjp != null) {
            pjp.unlockTitle(TitleType.WARDEN);
        }
        if(type == EntityType.CREEPER)
            if(percentChance(0.5*looting))
                e.getDrops().add(genEnchantingScroll(Enchant.NITRO));
        if(type == EntityType.ENDERMAN)
            if(percentChance(0.5*looting))
                e.getDrops().add(genEnchantingScroll(Enchant.ENDEREYES));
        if(type == EntityType.SHULKER)
            if(percentChance(looting))
                e.getDrops().add(genEnchantingScroll(Enchant.ANTIGRAVITY));
        if(type == EntityType.DROWNED)
            if(percentChance(0.5*looting))
                e.getDrops().add(genEnchantingScroll(Enchant.SEALEGS));
        if(type == EntityType.SKELETON)
            if(percentChance(0.3*looting))
                e.getDrops().add(genEnchantingScroll(Enchant.FRACTURE));
        if(type == EntityType.MAGMA_CUBE)
            if (percentChance(0.5 * looting))
                e.getDrops().add(genEnchantingScroll(Enchant.MOLTEN));
        if(type == EntityType.HUSK)
            if (percentChance(0.5 * looting))
                e.getDrops().add(genEnchantingScroll(Enchant.DEVOUR));
        if(type == EntityType.STRAY)
            if (percentChance(0.5 * looting))
                e.getDrops().add(genEnchantingScroll(Enchant.PERMAFROST));
        if(type == EntityType.GHAST)
            if (percentChance(0.5 * looting))
                e.getDrops().add(genEnchantingScroll(Enchant.METEOR));
        if(ent instanceof Creeper creep){
            if(creep.isPowered()) {
                if (percentChance(looting))
                    e.getDrops().add(genEnchantingScroll(Enchant.DISCHARGE));
                else if(percentChance(looting))
                    e.getDrops().add(genEnchantingScroll(Enchant.THUNDER));
            }
        }
        if(type == EntityType.PHANTOM)
            if (percentChance(looting))
                e.getDrops().add(genEnchantingScroll(Enchant.GLIDE));
        if(type == EntityType.GUARDIAN)
            if (percentChance(0.5*looting))
                e.getDrops().add(genEnchantingScroll(Enchant.SPIKES));
        if(type == EntityType.ELDER_GUARDIAN)
            if (percentChance(2*looting))
                e.getDrops().add(genEnchantingScroll(Enchant.SPIKES));
        if(type == EntityType.CAVE_SPIDER)
            if (percentChance(0.5*looting))
                e.getDrops().add(genEnchantingScroll(Enchant.VENOM));

        NamespacedKey key = new NamespacedKey(pjm,"PJsMonster");
        if(ent.getPersistentDataContainer().has(key)){ // Drops for custom mobs
            MonsterType monType = null;
            String str = ent.getPersistentDataContainer().get(key,PersistentDataType.STRING);
            if(str.startsWith("SIZE_")){
                double scale = Double.parseDouble(str.substring(5));
                e.setDroppedExp((int)(e.getDroppedExp() * scale)+1);
            }
            try{
                monType = MonsterType.valueOf(str);
            }
            catch(Exception ex){
                return;
            }

            int tier = monType.getTier();
            e.setDroppedExp(e.getDroppedExp()+3*tier);
            List<ItemStack> drops = e.getDrops();

            if(pjc != null){
                drops.add(new ItemStack(Material.EMERALD,1 + (int)(Math.random()*(5*tier+looting))));
                switch(monType){
                    case FAST -> {
                        if(percentChance(looting))
                            drops.add(genEnchantingScroll(Enchant.DASH));
                    }
                    case GHOST_SOLDIER -> {
                        if(percentChance(looting+6))
                            drops.add(genEnchantingScroll(Enchant.PHANTOM));
                    }
                    case JUMPING_SPIDER -> {
                        if(percentChance(looting+2))
                            drops.add(genEnchantingScroll(Enchant.LEAPING));
                    }
                    case ZOMBIE_COMMANDER -> {
                        drops.add(genEnchantingScroll());
                        for(ItemStack item:drops){
                            if(item != null)
                                if(item.getType() == Material.DIAMOND_SWORD && pjp != null)
                                    pjp.unlockTitle(TitleType.COMMANDER);
                        }
                    }
                    case EVIL_SNOWMAN -> {
                        if(percentChance(looting*6))
                            drops.add(genEnchantingScroll(Enchant.BLIZZARD));
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
