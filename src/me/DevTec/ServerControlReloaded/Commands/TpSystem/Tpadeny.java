package me.DevTec.ServerControlReloaded.Commands.TpSystem;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.DevTec.ServerControlReloaded.SCR.Loader;

public class Tpadeny implements CommandExecutor, TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1,
			String arg2, String[] arg3) {
		return Arrays.asList();
	}

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (Loader.has(s, "TpaDeny", "TpSystem")) {
			if (s instanceof Player) {
				RequestMap.deny((Player)s);
				return true;
			}
		}
		Loader.noPerms(s, "TpaDeny", "TpSystem");
		return true;
	}
}