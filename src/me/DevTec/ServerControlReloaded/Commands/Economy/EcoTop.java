package me.DevTec.ServerControlReloaded.Commands.Economy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.DevTec.ServerControlReloaded.SCR.API;
import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.Utils.Eco;
import me.DevTec.ServerControlReloaded.Utils.Pagination;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.economyapi.EconomyAPI;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.sortedmap.RankingAPI;
import me.devtec.theapi.utils.StringUtils;

public class EcoTop implements CommandExecutor, TabCompleter {
	private HashMap<String, Pagination<Entry<String, Double>>> h = new HashMap<>();

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (EconomyAPI.getEconomy() == null) {
			return true;
		}
		if (Loader.has(s, "BalanceTop", "Economy")) {
			
			new Tasker() {
				public void run() {
				String world = Eco.getEconomyGroupByWorld(Bukkit.getWorlds().get(0).getName());
				if (s instanceof Player)
					world = Eco.getEconomyGroupByWorld(((Player) s).getWorld().getName());
				Pagination<Entry<String, Double>> m = h.containsKey(world) ? h.get(world) : null;
				if (TheAPI.getCooldownAPI("ServerControlReloaded").expired("scr") || m == null) {
					TheAPI.getCooldownAPI("ServerControlReloaded").createCooldown("scr", 300*20); 
					HashMap<String, Double> money = new HashMap<>();
					for (UUID sa : TheAPI.getUsers()) {
						if(Bukkit.getOfflinePlayer(sa).getName()==null||Bukkit.getOfflinePlayer(sa).getName().equals("ServerControlReloaded"))continue;
						money.put(Bukkit.getOfflinePlayer(sa).getName(),EconomyAPI.getBalance(Bukkit.getOfflinePlayer(sa).getName(), world));
					}
					if (m != null)
						h.remove(world); 
					m = new Pagination<>(10,new RankingAPI<>(money).entrySet());
					h.put(world, m);
				}
				int page =args.length!=0?StringUtils.getInt(args[0]):1;
				--page;
				if(m.totalPages()<=page)page=m.totalPages()-1;
				TheAPI.msg("&7=====» &cBalanceTop &e"+(page+1)+"/"+(m.totalPages())+" &7«=====", s);
				int i = 0;
				for(Entry<String, Double> sf : m.getPage(page)){
					String key = sf.getKey();
					++i;
					TheAPI.msg(Loader.config.getString("Options.Economy.BalanceTop").replace("%position%", (i+(10*(page+1))-10) + "")
							.replace("%player%", key).replace("%playername%", player(s,key))
							.replace("%money%", API.setMoneyFormat(sf.getValue(),true)), s);
				}
			}}.runTask();
			return true;
		}
		Loader.noPerms(s, "BalanceTop", "Economy");
		return true;
	}

	public String player(CommandSender d, String s) {
		if (TheAPI.getPlayerOrNull(s) != null)
			return API.getPlayers(d).contains(TheAPI.getPlayerOrNull(s)) ? TheAPI.getPlayerOrNull(s).getDisplayName() : s;
		return s;
	}
	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1,
			String arg2, String[] arg3) {
		return Arrays.asList();
	}
}
