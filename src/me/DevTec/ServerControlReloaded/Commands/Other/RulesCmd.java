package me.DevTec.ServerControlReloaded.Commands.Other;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.Utils.TabList;
import me.devtec.theapi.TheAPI;

public class RulesCmd implements CommandExecutor, TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1,
			String arg2, String[] args) {
		return Arrays.asList();
	}

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		for(String read : Loader.rulesText)
			TheAPI.msg(TabList.replace(read, s instanceof Player?(Player)s:null, false), s);
		return true;
	}
}
