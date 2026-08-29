/*
 * ---------------------------------------------------
 *  PJ's Monsters
 *      Custom monsters for survival Minecraft
 * ---------------------------------------------------
 * by Nathan Cook @pajamasoft, nathan@pajamasoft.net
 * ---------------------------------------------------
 */
package net.pajamasoft.pJsMonsters;

import net.pajamasoft.pjcomputers.PJComputers;
import net.pajamasoft.pjenchants.Enchant;
import net.pajamasoft.pjenchants.PJEnchants;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static net.pajamasoft.pjLib.PJLib.format;
import static net.pajamasoft.pjLib.PJLib.newItem;

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
        this.getCommand("monsters").setExecutor(new commands(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("[PJsMonsters] Plugin has been disabled");
    }

    public void makeSpecialMob(Monster mon, MonsterType monType){
        EntityType type = mon.getType();
        if (monType.getBaseTypes().contains(type)) {
            String c = monType.getTierColor();
            mon.setCustomName(c+format(type.name())+" "+format(monType.name()));
            mon.setCustomNameVisible(false);

            NamespacedKey key = new NamespacedKey(this,"PJsMonster");
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
                        pje.enchant(axe, Enchant.FRACTURE,3);
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

                    ItemStack sword = newItem(Material.STONE_SWORD,"§7§lTitan Greatsword");
                    ItemMeta swordmeta = sword.getItemMeta();
                    swordmeta.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(new NamespacedKey(this,"attack"),8, AttributeModifier.Operation.ADD_NUMBER));
                    swordmeta.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(new NamespacedKey(this,"speed"),-3, AttributeModifier.Operation.ADD_NUMBER));
                    sword.setItemMeta(swordmeta);
                    if(pje != null)
                        pje.enchant(sword, Enchant.GRAVITY,1);
                    mon.getEquipment().setItemInMainHand(sword);
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
                    ItemStack sword = newItem(Material.DIAMOND_SWORD,"§cZombie Commander Sword");
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
                    mon.getEquipment().setItemInMainHand(null);
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
                    mon.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.2);
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
                    ItemStack chestplate = newItem(Material.IRON_CHESTPLATE,c+"Frozen Chestplate");
                    ArmorMeta meta = (ArmorMeta)chestplate.getItemMeta();
                    meta.setTrim(new ArmorTrim(TrimMaterial.DIAMOND, TrimPattern.SILENCE));
                    chestplate.setItemMeta(meta);
                    if(pje != null)
                        pje.enchant(chestplate,Enchant.PERMAFROST,1);
                    snowman.getEquipment().setChestplate(chestplate);
                    snowman.setDerp(true);
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
                    }.runTaskTimer(this,0,30L);
                }
            }
        }
    }
}
