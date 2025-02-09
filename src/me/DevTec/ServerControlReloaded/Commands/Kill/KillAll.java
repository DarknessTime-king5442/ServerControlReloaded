package me.DevTec.ServerControlReloaded.Commands.Kill;


import me.DevTec.ServerControlReloaded.SCR.API;
import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.SCR.Loader.Placeholder;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KillAll implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (Loader.has(s, "KillAll", "Kill")) {
			List<String> pl = new ArrayList<String>();
			for (Player p : TheAPI.getOnlinePlayers()) {
				boolean i = p.isDead() || p.getHealth()==0;
				p.setHealth(0);
				if ((p.isDead() || p.getHealth()==0) && !i) {
					pl.add(p.getName());
				}
			}
			Loader.sendMessages(s, "Kill.KilledMore", Placeholder.c()
					.add("%list%", pl.isEmpty()?"none":StringUtils.join(pl, ","))
					.replace("%amount%", pl.size()+""));
			return true;
		}
		Loader.noPerms(s, "KillAll", "Kill");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1,
			String arg2, String[] args) {
		if(args.length==1 && Loader.has(s, "KillAll", "Kill"))
			return StringUtils.copyPartialMatches(args[0], API.getPlayerNames(s));
		return Arrays.asList();
	}
}
