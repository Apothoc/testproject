package grouplightning.artifactlightning;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.apache.commons.lang.Validate;

import static java.lang.Math.abs;

public class CommandHandler implements CommandExecutor {
    private Artifactlightning plug;

    public CommandHandler(Artifactlightning plugin) {
        this.plug = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ligline")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (!((NumberUtils.isNumber(args[0])) && (NumberUtils.isDigits(args[1])) && (NumberUtils.isDigits(args[2])))){
                    player.sendMessage("One of your parameters is not the correct number! Distance between strikes must be a double, while the strike count and the delay between strikes must be integers.");
                    return true;
                }

                final Double diststrike = abs(Double.parseDouble(args[0]));
                final int numstrike =  abs(Integer.parseInt(args[1]));
                final int strikedelay = abs(Integer.parseInt(args[2]));

                if ((diststrike > 10) || (numstrike > 40) || (strikedelay > 300)) {
                    player.sendMessage("One of your parameters is too high! 10 maximum distance between strikes, 40 maximum strikes, 300 maximum delay between strikes.");
                    return true;
                }

                new BukkitRunnable() {
                    double time = 0;
                    World world = player.getWorld();
                    Location loc = player.getLocation();
                    Vector direction = loc.getDirection().normalize();
                    int strikecount = numstrike;
                    int timestrike = strikedelay;

                    @Override
                    public void run() {
                        time += 1;
                        if (time == 1) {
                            loc.add(direction.multiply(2));
                        }
                        if (time == timestrike) {
                            world.strikeLightning(world.getHighestBlockAt(loc).getLocation());
                            strikecount -= 1;
                            loc.add(direction.multiply(diststrike));
                            timestrike = timestrike + strikedelay;
                        }
                        if (strikecount == 0) {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plug, 0, 1);
            }
        }
        return true;
    }
}


