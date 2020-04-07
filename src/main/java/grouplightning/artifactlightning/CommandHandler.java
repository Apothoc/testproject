package grouplightning.artifactlightning;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

import static java.lang.Math.*;

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

                if (!player.hasPermission("lightning.ligline")) {
                    player.sendMessage("No permission.");
                    return true;
                }

                if (args.length != 3) {
                    player.sendMessage("Please include a distance between strikes, a number of strikes, and a delay between strikes.");
                    return true;
                }

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

        if (cmd.getName().equalsIgnoreCase("ligstorm")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                //strike radius, strike frequency, storm duration

                if (!player.hasPermission("lightning.ligstorm")) {
                    player.sendMessage("No permission.");
                    return true;
                }

                if (args.length != 3) {
                    player.sendMessage("Please include a storm radius, a number of ticks for an average strike, and a storm duration.");
                    return true;
                }

                if (!((NumberUtils.isNumber(args[0])) && (NumberUtils.isDigits(args[1])) && (NumberUtils.isDigits(args[2])))){
                    player.sendMessage("One of your parameters is not the correct number! Storm radius must be a double, while the number of ticks for an average strike, and storm duration must be integers.");
                    return true;
                }

                final Double stormradius = abs(Double.parseDouble(args[0]));
                final int striketick =  abs(Integer.parseInt(args[1]));
                final int stormduration = abs(Integer.parseInt(args[2]));

                if ((stormradius > 50) || (striketick > 4000) || (stormduration > 4000)) {
                    player.sendMessage("One of your parameters is too high! 50 maximum radius for the storm, 4000 maximum ticks between average strike, and 4000 ticks maximum storm duration.");
                    return true;
                }

                new BukkitRunnable() {
                    double time = 0;
                    World world = player.getWorld();
                    Location loc = player.getLocation();
                    int striketickinner = striketick;
                    int stormdurationinner = stormduration;
                    double newstormradius;
                    double rando;
                    double angle;
                    Vector direction;

                    @Override
                    public void run() {
                        time += 1;
                        if (1 == (int) (Math.random()*striketickinner + 1)) {
                            angle = Math.random()*2*Math.PI;
                            rando = Math.random() + Math.random();
                            if (rando > 1) {
                                newstormradius = stormradius*(2-rando);
                            } else {
                                newstormradius = stormradius*(rando);
                            }
                            direction = new Vector(Math.sin(angle)*newstormradius, 0, Math.cos(angle)*newstormradius);
                            loc = world.getHighestBlockAt(player.getLocation().add(direction)).getLocation();
                            world.strikeLightning(loc);
                        }

                        if (time >= stormdurationinner) {
                            this.cancel();
                        }

                    }
                }.runTaskTimer(plug, 0, 1);
            }
        }

        if (cmd.getName().equalsIgnoreCase("gaelwave")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                final int fireballcount =  abs(Integer.parseInt(args[0]));

                new BukkitRunnable() {
                    double time = 0;
                    World world = player.getWorld();
                    Location loc = player.getLocation().add(0,2,0);;
                    Entity[] entityArray = new Entity[fireballcount];
                    Vector[] vectorArray = new Vector[fireballcount];
                    double targetdistance = 100000;
                    Entity target = null;
                    Vector addvector;


                    @Override
                    public void run() {
                        time += 1;
                        if (time == 1) {
                            player.sendMessage("time = 1");
                            for (int i = 0; i < fireballcount; i++) {
                                vectorArray[i] = new Vector((1-(2*Math.random())), 0.7, (1-(2*Math.random()))).normalize();
                                entityArray[i] = world.spawnEntity(loc, EntityType.SMALL_FIREBALL);
                            }
                        }

                        if (time > 19) {
                            for (int i = 0; i < fireballcount; i++) {
                                if (entityArray[i] != null) {
                                    List<Entity> nearEnts = entityArray[i].getNearbyEntities(30, 30, 30);
                                    if (nearEnts != null) {
                                        for (Entity forEnt : nearEnts) {
                                            if ((forEnt != player) && ((forEnt instanceof Mob) || (forEnt instanceof Player))) {
                                                if (targetdistance > forEnt.getLocation().distance(entityArray[i].getLocation())) {
                                                    target = forEnt;
                                                    targetdistance = forEnt.getLocation().distance(entityArray[i].getLocation());
                                                }
                                            }
                                        }
                                    }
                                    if (target != null) {
                                        addvector = (target.getLocation().toVector().subtract(entityArray[i].getLocation().toVector())).normalize().multiply(0.2);
                                        vectorArray[i] = ((vectorArray[i].normalize()).add(addvector)).normalize();

                                    }
                                    target = null;
                                    targetdistance = 100000;
                                }
                            }

                        }

                        for (int i = 0; i < fireballcount; i++) {
                            if (entityArray[i] != null) {
                                entityArray[i].setVelocity(vectorArray[i].multiply(0.2));
                            }
                        }


                        if (time > 100) {
                            this.cancel();
                        }


                    }
                }.runTaskTimer(plug, 0, 1);
            }
        }

        if (cmd.getName().equals("lightningstrike")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                World world = player.getWorld();
                world.strikeLightning(player.rayTraceBlocks(200, FluidCollisionMode.NEVER).getHitBlock().getLocation());
            }
        }

        return true;
    }
}


