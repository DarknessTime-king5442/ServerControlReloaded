package me.DevTec.ServerControlReloaded.Commands.Other;

import me.DevTec.ServerControlReloaded.SCR.API;
import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.SCR.Loader.Placeholder;
import me.DevTec.ServerControlReloaded.Utils.Repeat;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Feed implements CommandExecutor, TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1,
			String arg2, String[] args) {
		if(Loader.has(s, "Feed", "Other") && args.length==1)
			return StringUtils.copyPartialMatches(args[0], API.getPlayerNames(s));
		return Arrays.asList();
	}

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
			if (args.length == 0) {
				if (Loader.has(s, "Feed", "Other")) {
				if (s instanceof Player) {
					Player p = (Player) s;
					p.setFoodLevel(20);
					Loader.sendMessages(s, "Feed.You");
					return true;
				}
				Loader.Help(s, "Feed", "Other");
				return true;
				}
				Loader.noPerms(s, "Feed", "Other");
				return true;
			}
			if (args.length == 1) {
				if (args[0].equals("*")) {
					Repeat.a(s, "feed *");
					return true;
				}
				Player p = TheAPI.getPlayer(args[0]);
				if(p==null) {
					Loader.notOnline(s, args[0]);
					return true;
				}
				if (p == s) {
					if (Loader.has(s, "Feed", "Other")) {
					p.setFoodLevel(20);
					Loader.sendMessages(s, "Feed.You");
					return true;
					}
					Loader.noPerms(s, "Feed", "Other");
					return true;
				}
				if (Loader.has(s, "Feed", "Other","Other")) {
					p.setFoodLevel(20);
					Loader.sendMessages(s, "Feed.Other.Sender", Placeholder.c().replace("%player%", p.getName())
							.replace("%playername%", p.getDisplayName()));
					Loader.sendMessages(p, "Feed.Other.Receiver", Placeholder.c().replace("%player%", s.getName())
							.replace("%playername%", s.getName()));
					return true;
				}
				Loader.noPerms(s, "Feed", "Other", "Other");
				return true;
			}
			return true;
	}

}