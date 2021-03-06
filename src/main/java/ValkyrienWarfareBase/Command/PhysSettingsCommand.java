package ValkyrienWarfareBase.Command;

import ValkyrienWarfareBase.API.Vector;
import ValkyrienWarfareBase.PhysicsSettings;
import ValkyrienWarfareBase.ValkyrienWarfareMod;
import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PhysSettingsCommand extends CommandBase {

    public static final ArrayList<String> completionOptions = new ArrayList<String>();

    static {
        completionOptions.add("gravityVector");
        completionOptions.add("doSplitting");
        completionOptions.add("maxShipSize");
        completionOptions.add("physicsIterations");
        completionOptions.add("physicsSpeed");
        completionOptions.add("doGravity");
        completionOptions.add("doPhysicsBlocks");
        completionOptions.add("doBalloons");
        completionOptions.add("doAirshipRotation");
        completionOptions.add("doAirshipMovement");
        completionOptions.add("save");
        completionOptions.add("doEtheriumLifting");
    }

    @Override
    public String getName() {
        return "physSettings";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/physSettings <setting name> [value]";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString("Avaliable Physics Commands:"));
            for (String command : completionOptions) {
                sender.sendMessage(new TextComponentString(command));
            }
        }
        String key = args[0];
        if (key.equals("doSplitting")) {
            if (args.length == 1) {
                sender.sendMessage(new TextComponentString("doSplitting=" + ValkyrienWarfareMod.doSplitting + " (Default: false)"));
                return;
            } else if (args.length == 2) {
                boolean value = Boolean.parseBoolean(args[1]);
                ValkyrienWarfareMod.doSplitting = value;
                sender.sendMessage(new TextComponentString("Set physics splitting to " + value));
                return;
            }
        } else if (key.equals("maxShipSize")) {
            if (args.length == 1) {
                sender.sendMessage(new TextComponentString("maxShipSize=" + ValkyrienWarfareMod.maxShipSize + " (Default: 15000)"));
                return;
            } else if (args.length == 2) {
                int value = Integer.parseInt(args[1]);
                ValkyrienWarfareMod.maxShipSize = value;
                sender.sendMessage(new TextComponentString("Set maximum ship size to " + value));
                return;
            }
        } else if (key.equals("gravityVector")) {
            if (args.length == 1) {
                sender.sendMessage(new TextComponentString("gravityVector=" + ValkyrienWarfareMod.gravity.toRoundedString() + " (Default: <0,-9.8,0>)"));
                return;
            } else if (args.length == 4) {
                Vector newVector = new Vector(0, -9.8, 0);
                try {
                    if (args[1] != null && args[2] != null && args[3] != null) {
                        newVector.X = Double.parseDouble(args[1]);
                        newVector.Y = Double.parseDouble(args[2]);
                        newVector.Z = Double.parseDouble(args[3]);
                    } else {
                        sender.sendMessage(new TextComponentString("Usage: /physSettings gravityVector <x> <y> <z>"));
                        return;
                    }
                } catch (Exception e) {
                }
                ValkyrienWarfareMod.gravity = newVector;
                sender.sendMessage(new TextComponentString("Physics gravity set to " + newVector.toRoundedString() + " (Default: <0,-9.8,0>)"));
                return;
            } else {
                sender.sendMessage(new TextComponentString("Usage: /physSettings gravityVector <x> <y> <z>"));
            }
        } else if (key.equals("physicsIterations")) {
            if (args.length == 1) {
                sender.sendMessage(new TextComponentString("physicsIterations=" + ValkyrienWarfareMod.physIter + " (Default: 10)"));
                return;
            } else if (args.length == 2) {
                int value = Integer.parseInt(args[1]);
                if (value < 0 || value > 1000) {
                    sender.sendMessage(new TextComponentString("Please enter a value between 0 and 1000"));
                    return;
                }
                ValkyrienWarfareMod.physIter = value;
                sender.sendMessage(new TextComponentString("Set physicsIterations to " + value));
                return;
            }
        } else if (key.equals("physicsSpeed")) {
            if (args.length == 1) {
                sender.sendMessage(new TextComponentString("physicsSpeed=" + ValkyrienWarfareMod.physSpeed + " (Default: 100%)"));
                return;
            } else if (args.length == 2) {
                double value = Double.parseDouble(args[1].replace('%', ' '));
                if (value < 0 || value > 1000) {
                    sender.sendMessage(new TextComponentString("Please enter a value between 0 and 1000"));
                    return;
                }
                ValkyrienWarfareMod.physSpeed = value * 0.05D / 100D;
                sender.sendMessage(new TextComponentString("Set physicsSpeed to " + value + " percent"));
                return;
            }
        } else if (key.equals("doGravity")) {
            if (args.length == 1) {
                sender.sendMessage(new TextComponentString("doGravity=" + PhysicsSettings.doGravity + " (Default: true)"));
                return;
            } else if (args.length == 2) {
                boolean value = Boolean.parseBoolean(args[1]);
                PhysicsSettings.doGravity = value;
                sender.sendMessage(new TextComponentString("Set doGravity to " + (PhysicsSettings.doGravity ? "enabled" : "disabled")));
                return;
            }
        } else if (key.equals("doPhysicsBlocks")) {
            if (args.length == 1) {
                sender.sendMessage(new TextComponentString("doPhysicsBlocks=" + PhysicsSettings.doPhysicsBlocks + " (Default: true)"));
                return;
            } else if (args.length == 2) {
                boolean value = Boolean.parseBoolean(args[1]);
                PhysicsSettings.doPhysicsBlocks = value;
                sender.sendMessage(new TextComponentString("Set doPhysicsBlocks to " + (PhysicsSettings.doPhysicsBlocks ? "enabled" : "disabled")));
                return;
            }
        } else if (key.equals("doBalloons")) {
            if (args.length == 1) {
                sender.sendMessage(new TextComponentString("doBalloons=" + PhysicsSettings.doBalloons + " (Default: true)"));
                return;
            } else if (args.length == 2) {
                boolean value = Boolean.parseBoolean(args[1]);
                PhysicsSettings.doBalloons = value;
                sender.sendMessage(new TextComponentString("Set doBalloons to " + (PhysicsSettings.doBalloons ? "enabled" : "disabled")));
                return;
            }
        } else if (key.equals("doAirshipRotation")) {
            if (args.length == 1) {
                sender.sendMessage(new TextComponentString("doAirshipRotation=" + PhysicsSettings.doAirshipRotation + " (Default: true)"));
                return;
            } else if (args.length == 2) {
                boolean value = Boolean.parseBoolean(args[1]);
                PhysicsSettings.doAirshipRotation = value;
                sender.sendMessage(new TextComponentString("Set doAirshipRotation to " + (PhysicsSettings.doAirshipRotation ? "enabled" : "disabled")));
                return;
            }
        } else if (key.equals("doAirshipMovement")) {
            if (args.length == 1) {
                sender.sendMessage(new TextComponentString("doAirshipMovement=" + PhysicsSettings.doAirshipMovement + " (Default: true)"));
                return;
            } else if (args.length == 2) {
                boolean value = Boolean.parseBoolean(args[1]);
                PhysicsSettings.doAirshipMovement = value;
                sender.sendMessage(new TextComponentString("Set doAirshipMovement to " + (PhysicsSettings.doAirshipMovement ? "enabled" : "disabled")));
                return;
            }
        } else if (key.equals("doEtheriumLifting")) {
            if (args.length == 1) {
                sender.sendMessage(new TextComponentString("doEtheriumLifting=" + PhysicsSettings.doEtheriumLifting + " (Default: true)"));
                return;
            } else if (args.length == 2) {
                boolean value = Boolean.parseBoolean(args[1]);
                PhysicsSettings.doEtheriumLifting = value;
                sender.sendMessage(new TextComponentString("Set doEtheriumLifting to " + (PhysicsSettings.doEtheriumLifting ? "enabled" : "disabled")));
                return;
            }
        } else if (key.equals("save")) {
            ValkyrienWarfareMod.instance.saveConfig();
            sender.sendMessage(new TextComponentString("Saved phyisics settings"));
            return;
        } else if (true || key.equals("help")) {
            sender.sendMessage(new TextComponentString("Avaliable Physics Commands:"));
            for (String command : completionOptions) {
                sender.sendMessage(new TextComponentString(command));
            }
        }

        sender.sendMessage(new TextComponentString(this.getUsage(sender)));
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args.length == 1) {
            ArrayList<String> possibleArgs = (ArrayList<String>) completionOptions.clone();

            for (Iterator<String> iterator = possibleArgs.iterator(); iterator.hasNext(); ) { //Don't like this, but I have to because concurrentmodificationexception
                if (!iterator.next().startsWith(args[0])) {
                    iterator.remove();
                }
            }

            return possibleArgs;
        } else if (args.length == 2) {
            if (args[0].startsWith("do")) {
                if (args[1].startsWith("t")) {
                    return Lists.newArrayList("true");
                } else if (args[1].startsWith("f")) {
                    return Lists.newArrayList("false");
                } else {
                    return Lists.newArrayList("true", "false");
                }
            }
        }

        return null;
    }
}
