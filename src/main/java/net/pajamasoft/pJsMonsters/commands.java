package net.pajamasoft.pJsMonsters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public class commands implements CommandExecutor {

    String prefix = "§7[§9PJ§bs§3Monsters§7] ";
    private PJsMonsters pjm;

    commands(PJsMonsters pjm){
        this.pjm = pjm;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            Player p = (Player) sender;
            if(p.isOp()){
                if(args[0].equalsIgnoreCase("summon")){
                    MonsterType monType = MonsterType.valueOf(args[2].toUpperCase());
                    EntityType type = (EntityType)monType.getBaseTypes().toArray()[0];
                    if(args.length > 3)
                        type = EntityType.valueOf(args[3].toUpperCase());
                    Monster mon = (Monster)p.getWorld().spawnEntity(p.getLocation().add(p.getLocation().getDirection()),type);
                    pjm.makeSpecialMob(mon,monType);
                }
            }
        } catch (Exception ex) {
            //
        }
        return true;
    }
}
