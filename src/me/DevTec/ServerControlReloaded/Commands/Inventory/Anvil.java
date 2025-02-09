package me.DevTec.ServerControlReloaded.Commands.Inventory;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.DevTec.ServerControlReloaded.SCR.API;
import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.SCR.Loader.Placeholder;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.guiapi.AnvilGUI;
import me.devtec.theapi.utils.StringUtils;

public class Anvil implements CommandExecutor, TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1,
			String arg2, String[] args) {
		if(args.length==1 && Loader.has(s, "Anvil", "Inventory"))
			return StringUtils.copyPartialMatches(args[0], API.getPlayerNames(s));
		return Arrays.asList();
	}

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (Loader.has(s, "Anvil", "Inventory")) {
			if (s instanceof Player) {
				if (args.length == 0) {
					Loader.sendMessages(s, "Inventory.Anvil.You");
					new AnvilGUI("&7Anvil", (Player)s).setInsertable(true);
					return true;
				}
				if (args.length == 1) {
					Player t = TheAPI.getPlayer(args[0]);
					if (t == null) {
						Loader.notOnline(s, args[0]);
						return true;
					}
					Loader.sendMessages(s, "Inventory.Anvil.Other.Sender", Placeholder.c().add("%player%", t.getName()).add("%playername%", t.getDisplayName()));
					Loader.sendMessages(t, "Inventory.Anvil.Other.Target", Placeholder.c().add("%player%", s.getName()).add("%playername%", s.getName()));
					new AnvilGUI("&7Anvil", t).setInsertable(true);
					return true;
				}
			}
			return true;
		}
		Loader.noPerms(s, "Anvil", "Inventory");
		return true;
	}
}
