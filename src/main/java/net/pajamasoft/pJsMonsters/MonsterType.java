package net.pajamasoft.pJsMonsters;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Snowman;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public enum MonsterType {

    BABY(2,Set.of(EntityType.CREEPER,EntityType.SKELETON,EntityType.SPIDER, EntityType.ENDERMAN)),
    BRUTE(2,Set.of(EntityType.ZOMBIE,EntityType.SKELETON,EntityType.WITHER_SKELETON,EntityType.PIGLIN)),
    COLOSSUS(6,Set.of(EntityType.ZOMBIE,EntityType.SKELETON,EntityType.SPIDER)),
    COMBO(3,Set.of(EntityType.ZOMBIE,EntityType.SKELETON,EntityType.CREEPER,EntityType.SPIDER,EntityType.HUSK)),
    CREEPER_JOCKEY(2,EntityType.SPIDER),
    FAST(1,Set.of(EntityType.ZOMBIE,EntityType.PIGLIN,EntityType.PIGLIN_BRUTE,EntityType.SPIDER,EntityType.SKELETON,EntityType.ENDERMAN)),
    GHOST_SOLDIER(2,EntityType.SKELETON),
    JUMPING_SPIDER(1,Set.of(EntityType.SPIDER)),
    KNIGHT(2,Set.of(EntityType.ZOMBIE,EntityType.SKELETON,EntityType.PIGLIN)),
    FROZEN_KNIGHT(2,EntityType.STRAY),
    ROTTING_CORPSE(2,Set.of(EntityType.ZOMBIE,EntityType.SKELETON)),
    SEA_COMMANDER(4,EntityType.DROWNED),
    TARANTULA(3,EntityType.SPIDER),
    TITAN(4,Set.of(EntityType.ZOMBIE,EntityType.SKELETON)),
    UNSTABLE(2,Set.of(EntityType.ZOMBIE,EntityType.SKELETON)),
    ZOMBIE_COMMANDER(4,EntityType.ZOMBIE),
    EVIL_SNOWMAN(3,EntityType.STRAY),
    ;

    private final int tier;
    private Set<EntityType> base_types;

    MonsterType(int tier, Set<EntityType> base_types) {
        this.tier = tier;
        this.base_types = base_types;
    }

    MonsterType(int tier, EntityType type){
        this.tier = tier;
        this.base_types = Set.of(type);
    }

    public int getTier(){
        return tier;
    }

    public Set<EntityType> getBaseTypes(){
        return base_types;
    }

    public double getSpawnChance(){
        if(tier == 5)
            return 0.1;
        if(tier == 4)
            return 0.5;
        if(tier == 3)
            return 1;
        if(tier == 2)
            return 3;
        if(tier == 1)
            return 5;
        return 0;
    }

    public String getTierColor(){
        if(tier == 5)
            return "§4§l";
        if(tier == 4)
            return "§c";
        if(tier == 3)
            return "§6";
        if(tier == 2)
            return "§e";
        if(tier == 1)
            return "§a";
        return "";
    }
}
