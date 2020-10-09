package Commands.Weather;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import ServerControl.Loader;
import ServerControl.Loader.Placeholder;

public class Sun implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (args.length == 0) {
			if (s instanceof Player) {
				if (Loader.has(s, "Sun", "Weather")) {
					((Player) s).getLocation().getWorld().setStorm(false);
					((Player) s).getLocation().getWorld().setWeatherDuration(100000000);
					Loader.sendMessages(s, "Weather.Sun", Placeholder.c()
							.add("%world%", ((Player) s).getLocation().getWorld().getName()));
					return true;
				}
				Loader.noPerms(s, "Sun", "Weather");
				return true;
			}
			Loader.Help(s, "Sun", "Weather");
			return true;
		}
		if (Loader.has(s, "Sun", "Weather")) {
			if (Bukkit.getWorld(args[0]) != null) {
				Bukkit.getWorld(args[0]).setStorm(false);
				Loader.sendMessages(s, "Weather.Sun", Placeholder.c()
						.add("%world%", args[0]));
				return true;
			}
			Loader.sendMessages(s, "Missing.World", Placeholder.c()
					.add("%world%", args[0]));
			return true;
		}
		Loader.noPerms(s, "Sun", "Weather");
		return true;
	}

	public List<String> worlds() {
		List<String> list = new ArrayList<String>();
		for (World p2 : Bukkit.getWorlds()) {
			list.add(p2.getName());
		}
		return list;
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String alias, String[] args) {
		List<String> c = new ArrayList<>();
		if (cmd.getName().equalsIgnoreCase("sun") && args.length == 1) {
			if (s.hasPermission("ServerControl.Weather")) {
				c.addAll(StringUtil.copyPartialMatches(args[0], worlds(), new ArrayList<>()));
			}
		}
		return c;
	}
}