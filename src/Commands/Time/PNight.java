package Commands.Time;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ServerControl.Loader;
import ServerControl.Loader.Placeholder;
import me.DevTec.TheAPI.TheAPI;

public class PNight implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (args.length == 0) {
			if (s instanceof Player) {
				if (Loader.has(s, "PlayerNight", "Time")) {
					((Player) s).setPlayerTime(13000, true);
					Loader.sendMessages(s, "Time.PNight", Placeholder.c().add("%world%", ((Player) s).getLocation().getWorld().getName())
							.add("%player%", s.getName()));
					return true;
				}
				Loader.noPerms(s, "PlayerNight", "Time");
				return true;
			}
			Loader.Help(s, "/Night <world>", "Time");
			return true;
		}
		if (args.length == 1) {
			if (Loader.has(s, "PlayerNight", "Time", "Other")) {
				if (TheAPI.getPlayer(args[0]) != null) {
					TheAPI.getPlayer(args[0]).setPlayerTime(13000, true);
					Loader.sendMessages(s, "Time.PNight", Placeholder.c().add("%world%", ((Player) s).getLocation().getWorld().getName())
							.add("%player%", args[0]));
					return true;
				}
				Loader.notOnline(s, args[0]);
				return true;
			}
			Loader.noPerms(s, "PlayerNight", "Time", "Other");
			return true;
		}
		return false;
	}

}
