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
import org.bukkit.inventory.meta.LeatherArmorMeta;
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
                makeSpecialMob(mon,monType);
            }
        }
    }

    public void makeSpecialMob(Monster mon, MonsterType monType){
        EntityType type = mon.getType();
        if (monType.getBaseTypes().contains(type)) {
            String c = monType.getTierColor();
            mon.setCustomName(c+format(type.name())+" "+format(monType.name()));
            mon.setCustomNameVisible(false);

            NamespacedKey key = new NamespacedKey(pjm,"PJsMonster");
            mon.getPersistentDataContainer().set(key, PersistentDataType.STRING, monType.name());

            switch(monType){
                case BABY -> {
                    mon.setCustomName(c+"Baby "+format(type.name()));
                    mon.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.3);
                    mon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(10);
                    mon.getAttribute(Attribute.SCALE).setBaseValue(0.5);
                    if(mon instanceof Creeper creep)
                        creep.setExplosionRadius(1);
                    return;
                }
                case BRUTE -> {
                    mon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(50);
                    mon.setHealth(50);
                    mon.getAttribute(Attribute.SCALE).setBaseValue(1.3);
                    mon.getAttribute(Attribute.ARMOR).setBaseValue(6);
                    ItemStack axe = newItem(Material.STONE_AXE,c+ format(type.name()) +" Brute Axe");
                    if(pje != null)
                        pje.enchant(axe,Enchant.FRACTURE,3);
                    mon.getEquipment().setItemInMainHand(axe);
                    return;
                }
                case TITAN -> {
                    mon.getAttribute(Attribute.SCALE).setBaseValue(1.8);
                    mon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(80);
                    mon.setHealth(80);
                    mon.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.19);
                    mon.getAttribute(Attribute.ARMOR).setBaseValue(4);
                    mon.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1);
                    mon.getEquipment().setItemInMainHand(pjc.getItem("titan_greatsword"));
                    return;
                }
                case UNSTABLE -> {
                    mon.setCustomName(c+"Unstable "+format(type.name()));
                    mon.getEquipment().setHelmet(new ItemStack(Material.TNT));
                    ItemStack weapon = new ItemStack(Material.FLINT_AND_STEEL);
                    weapon.addUnsafeEnchantment(Enchantment.FIRE_ASPECT,1);
                    mon.getEquipment().setItemInMainHand(weapon);
                    return;
                }
                case ROTTING_CORPSE -> {
                    mon.setCustomName(c+"Rotting Corpse");
                    if(type == EntityType.ZOMBIE){
                        mon.getEquipment().setHelmet(new ItemStack(Material.SKELETON_SKULL));
                    }
                    else if(type == EntityType.SKELETON){
                        mon.getEquipment().setHelmet(new ItemStack(Material.ZOMBIE_HEAD));
                    }
                    mon.getEquipment().setItemInMainHand(new ItemStack(Material.WOODEN_AXE));
                    ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
                    LeatherArmorMeta chestmeta = (LeatherArmorMeta) chest.getItemMeta();
                    chestmeta.setColor(Color.fromRGB(22,186,188));
                    chest.setItemMeta(chestmeta);
                    mon.getEquipment().setChestplate(chest);
                    mon.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
                    ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
                    LeatherArmorMeta bootsmeta = (LeatherArmorMeta) boots.getItemMeta();
                    bootsmeta.setColor(Color.fromRGB(110,112,114));
                    boots.setItemMeta(bootsmeta);
                    mon.getEquipment().setBoots(boots);
                    return;
                }
                case ZOMBIE_COMMANDER -> {
                    mon.setCustomName(c+"Zombie Commander");
                    mon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(80);
                    mon.setHealth(80);
                    mon.getAttribute(Attribute.SCALE).setBaseValue(1.2);
                    mon.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
                    mon.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
                    mon.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
                    mon.getEquipment().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
                    ItemStack sword = pjc.newItem(Material.DIAMOND_SWORD,"§cZombie Commander Sword");
                    sword.addEnchantment(Enchantment.SHARPNESS,1);
                    if(pje != null)
                        pje.enchant(sword,Enchant.UNHOLY,1);
                    mon.getEquipment().setItemInMainHand(sword);
                    ZombieHorse zh = mon.getWorld().spawn(mon.getLocation(),ZombieHorse.class);
                    zh.setAdult();
                    zh.addPassenger(mon);
                    zh.setPersistent(false);
                    zh.getAttribute(Attribute.MAX_HEALTH).setBaseValue(120);
                    zh.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(zh.getAttribute(Attribute.MOVEMENT_SPEED).getValue()*1.4);
                    return;
                }
                case FAST -> {
                    mon.setCustomName(c+"§oFast "+format(type.name()));
                    mon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(mon.getAttribute(Attribute.MAX_HEALTH).getValue()*0.75);
                    mon.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(mon.getAttribute(Attribute.MOVEMENT_SPEED).getValue()*1.6);
                    return;
                }
                case KNIGHT -> {
                    ItemStack sword = newItem(Material.IRON_SWORD,"§cKnight Sword");
                    sword.addEnchantment(Enchantment.SHARPNESS,1);
                    mon.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
                    mon.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                    mon.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                    mon.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
                    mon.getEquipment().setItemInMainHand(sword);
                    return;
                }
                case GHOST_SOLDIER -> {
                    ItemStack sword = newItem(Material.DIAMOND_SWORD,"§cGhost Blade");
                    sword.addEnchantment(Enchantment.SHARPNESS,1);
                    if(pje != null)
                        pje.enchant(sword,Enchant.PHANTOM,1);
                    mon.getEquipment().setItemInMainHand(sword);
                    mon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(10);
                    mon.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(mon.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue()*1.3);
                    mon.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,PotionEffect.INFINITE_DURATION,0,false,false));
                    return;
                }
                case JUMPING_SPIDER -> {
                    mon.setCustomName(c+"Jumping Spider");
                    mon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(15);
                    mon.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST,PotionEffect.INFINITE_DURATION,3));
                    return;
                }
                case TARANTULA -> {
                    mon.setCustomName(c+"Tarantula");
                    mon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(40);
                    mon.setHealth(40);
                    mon.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(1);
                    mon.getAttribute(Attribute.SCALE).setBaseValue(1.5);
                    return;
                }
                case SEA_COMMANDER -> {
                    mon.setCustomName(c+"Sea Commander");
                    mon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(80);
                    mon.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
                    mon.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
                    mon.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
                    mon.getEquipment().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
                    mon.getEquipment().setItemInMainHand(new ItemStack(Material.TRIDENT));
                    mon.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(mon.getAttribute(Attribute.MOVEMENT_SPEED).getValue() * 1.3);
                    return;
                }
                case CREEPER_JOCKEY -> {
                    mon.setCustomName(null);
                    mon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(15);
                    mon.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(mon.getAttribute(Attribute.MOVEMENT_SPEED).getValue()*1.4);
                    Creeper cr = mon.getWorld().spawn(mon.getLocation(), Creeper.class);
                    mon.addPassenger(cr);
                }
                case COMBO -> {
                    mon.setCustomName("");
                    Monster m2 = (Monster)mon.getWorld().spawnEntity(mon.getLocation(), mon.getType());
                    m2.getAttribute(Attribute.MAX_HEALTH).setBaseValue(10);
                    m2.getAttribute(Attribute.SCALE).setBaseValue(0.5);
                    mon.addPassenger(m2);
                }
                case COLOSSUS -> {
                    mon.getAttribute(Attribute.SCALE).setBaseValue(10);
                    mon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(300);
                    mon.setHealth(300);
                    mon.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.1);
                    mon.getAttribute(Attribute.ARMOR).setBaseValue(2);
                    mon.getAttribute(Attribute.KNOCKBACK_RESISTANCE).setBaseValue(1);
                    mon.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,300,0,false,false));
                    mon.teleport(mon.getWorld().getHighestBlockAt(mon.getLocation()).getLocation().add(0,1,0));
                    return;
                }
                case FROZEN_KNIGHT -> {
                    mon.getAttribute(Attribute.SCALE).setBaseValue(1.3);
                    mon.getAttribute(Attribute.MAX_HEALTH).setBaseValue(25);
                    mon.setHealth(25);
                }
                case EVIL_SNOWMAN -> {
                    Snowman snowman = (Snowman)mon.getWorld().spawnEntity(mon.getLocation(), EntityType.SNOW_GOLEM);
                    mon.remove();
                    snowman.setCustomName(c+"Evil Snow Golem");
                    snowman.getPersistentDataContainer().set(key, PersistentDataType.STRING, monType.name());
                    snowman.getAttribute(Attribute.SCALE).setBaseValue(1.9);
                    snowman.getAttribute(Attribute.MAX_HEALTH).setBaseValue(40);
                    snowman.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.5);
                    snowman.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,PotionEffect.INFINITE_DURATION,0,false,false));
                    snowman.setHealth(40);
                    snowman.setDerp(true);
                    snowman.getEquipment().setHelmet(new ItemStack(Material.SKELETON_SKULL));
                    new BukkitRunnable() {
                        public void run(){
                            if(snowman.isDead())
                                cancel();
                            if(snowman.getTarget() == null){
                                for(Entity ent: snowman.getNearbyEntities(10,10,10)){
                                    if(ent instanceof Player p)
                                        snowman.setTarget(p);
                                }
                            }
                        }
                    }.runTaskTimer(pjm,0,30L);
                }
            }
        }
    }
}
