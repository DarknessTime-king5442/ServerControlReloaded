package me.DevTec.ServerControlReloaded.Commands.Weather;

import java.util.Arrays;
import java.util.List;

import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.DevTec.ServerControlReloaded.SCR.API;
import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.SCR.Loader.Placeholder;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.StringUtils;

public class PSun implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (args.length == 0) {
			if (s instanceof Player) {
				if (Loader.has(s, "PSun", "Weather")) {
					((Player) s).setPlayerWeather(WeatherType.CLEAR);
					Loader.sendMessages(s, "Weather.PSun", Placeholder.c()
							.add("%player%", s.getName()).add("%player%", ((Player) s).getDisplayName()));
					return true;
				}
				Loader.noPerms(s, "PSun", "Weather");
				return true;
			}
			Loader.Help(s, "PSun", "Weather");
			return true;
		}
		if (Loader.has(s, "PSun", "Weather","Other")) {
			if (TheAPI.getPlayer(args[0]) != null) {
				TheAPI.getPlayer(args[0]).setPlayerWeather(WeatherType.CLEAR);
				Loader.sendMessages(s, "Weather.PSun", Placeholder.c()
						.add("%player%", TheAPI.getPlayer(args[0]).getName())
						.add("%playername%", TheAPI.getPlayer(args[0]).getDisplayName()));
				return true;
			}
			Loader.notOnline(s, args[0]);
			return true;
		}
		Loader.noPerms(s, "PSun", "Weather","Other");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1,
			String arg2, String[] args) {
		if (args.length == 1)
			if (Loader.has(s, "PlayerSun", "Weather", "Other"))
				return StringUtils.copyPartialMatches(args[0], API.getPlayerNames(s));
		return Arrays.asList();
	}
}
