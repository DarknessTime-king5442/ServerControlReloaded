package me.DevTec.ServerControlReloaded.Utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.DevTec.ServerControlReloaded.SCR.API;
import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.SCR.Loader.Placeholder;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.placeholderapi.PlaceholderAPI;
import me.devtec.theapi.scheduler.Scheduler;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.listener.EventHandler;
import me.devtec.theapi.utils.listener.Listener;
import me.devtec.theapi.utils.listener.events.ServerListPingEvent;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.nms.NMSAPI.ChatType;
import me.devtec.theapi.utils.reflections.Ref;
import me.devtec.theapi.utils.serverlist.PlayerProfile;

public class Tasks {
	
	public static ArrayList<String> players = new ArrayList<>();
	static ArrayList<Integer> tasks = new ArrayList<>();
	static Map<String, String> sss = new HashMap<>();
	static Loader a;
	static int tests;
	static Listener l = new Listener() {
		@EventHandler
		public void onTag(ServerListPingEvent e) {
			if (setting.motd) {
				e.setMotd(TheAPI.colorize(PlaceholderAPI.setPlaceholders(null,Loader.config.getString((!setting.lock_server || setting.lock_server && !setting.motd_maintenance)?"Options.ServerList.MOTD.Text.Normal":"Options.ServerList.MOTD.Text.Maintenance")
						.replace("%next%", "\n").replace("%line%", "\n"))));
			}
			Iterator<PlayerProfile> p = e.getPlayersText().iterator();
			int vanis=0;
			while(p.hasNext()) {
				if(Bukkit.getPlayer(p.next().getUUID())!=null && API.hasVanish(Bukkit.getPlayer(p.next().getUUID()))) {
					p.remove();
					++vanis;
				}
			}
			e.setOnlinePlayers(e.getOnlinePlayers()-vanis);
			e.setMaxPlayers(TheAPI.getMaxPlayers());
		}
	};

	public static void unload() {
		for (int t : tasks)
			Scheduler.cancelTask(t);
		tests = 0;
		l.unregister();
		tasks.clear();
	}

	public static void load() {
		a = Loader.getInstance;
		sss.clear();
		players.clear();
		l.register();
		if (setting.am)
			automessage();
		if (setting.vip)
			vipslot();

		if (setting.tab) {
			for(Player p : TheAPI.getOnlinePlayers())regPlayer(p);
			tab();
		}

		if (setting.save)
			savetask();
		
		other();
		tempfly();
		tempGamemode();
	}

	public static void reload() {
		unload();
		load();
	}

	private static void tempfly() {
		tasks.add(new Tasker() {
			@Override
			public void run() {
				for(Player s : TheAPI.getOnlinePlayers()) {
					if(!TheAPI.getUser(s).getBoolean("TempFly.Use"))continue;
						long start = TheAPI.getUser(s).getLong("TempFly.Start");
						int end = TheAPI.getUser(s).getInt("TempFly.Time");
						long timeout = start / 1000 - System.currentTimeMillis() / 1000 + end;
						if (timeout <= 0) {
							if (s != null) {
								Loader.sendMessages(s, "Fly.Temp.End");
								API.getSPlayer(s).disableFly();
								TheAPI.getUser(s).remove("TempFly");
								TheAPI.getUser(s).save();
							}
							return;
						}
						if (s != null)
						if (timeout <= 5 || timeout == 10 || timeout == 15 || timeout == 30 || timeout == 45 || timeout == 60)
								Loader.sendMessages(s, "Fly.Temp.EndIn", Placeholder.c().add("%time%", StringUtils.setTimeToString(timeout)));
					}
				}
		}.runRepeating(0, 20));
	}

	private static void tempGamemode(){
		tasks.add(new Tasker() {
			@Override
			public void run() {
				for(Player s:TheAPI.getOnlinePlayers()) {
					if (!TheAPI.getUser(s).getBoolean("TempGamemode.Use")) continue;
					long start = TheAPI.getUser(s).getLong("TempGamemode.Start");
					int end = TheAPI.getUser(s).getInt("TempGamemode.Time");
					long timeout = start / 1000 - System.currentTimeMillis() / 1000 + end;
					if (timeout <= 0) {
						if (s != null) {
							try{
							Loader.sendMessages(s, "GameMode.Temp.End");
							s.setGameMode(GameMode.valueOf(TheAPI.getUser(s).getString("TempGamemode.Prev")));
							TheAPI.getUser(s).remove("TempGamemode");
							TheAPI.getUser(s).save();}catch (Exception e){e.printStackTrace();}
						}
						return;
					}
					if (s != null)
						if (timeout <= 3 || timeout == 10 || timeout == 15 || timeout == 30 || timeout == 45 || timeout == 60)
							Loader.sendMessages(s, "GameMode.Temp.EndIn", Placeholder.c().add("%time%", StringUtils.setTimeToString(timeout)));
				}
			}
		}.runRepeatingSync(0,20));
	}

	private static void savetask() {
		if (Loader.mw.getInt("SavingTask.Delay") < 600) {
			Loader.mw.set("SavingTask.Delay", 600);
			Loader.mw.save();
		}
		tasks.add(new Tasker() {
			int now = 0;

			@Override
			public void run() {
				List<World> w = Bukkit.getWorlds();
				if (w.size() <= now)
					now = 0;
				try {
					if (!Loader.mw.getBoolean("WorldsSettings." + w.get(now).getName() + ".AutoSave"))
						w.get(now).save();
				} catch (Exception err) {
				}
				++now;
			}
		}.runRepeatingSync(0, 20 * Loader.mw.getInt("SavingTask.Delay")));
	}

	public static void regPlayer(Player p) {
		if (!sss.containsKey(p.getName())) {
			String uuid = p.getUniqueId().toString();
			uuid = uuid.substring(0, 5);
			String pname = p.getName();
			if (pname.length() > 5) {
				pname = pname.substring(0, 5);
			}
			sss.put(p.getName(), uuid + pname);
		}
	}

	private static void other() {
		tasks.add(new Tasker() {
			@Override
			public void run() {
				for (Player p : TheAPI.getOnlinePlayers())
					ChatFormatter.setupName(p);
			}
		}.runRepeating(0, 20));
	}

	private static void tab() {
		int r = Loader.tab.getInt("Options.RefleshTick.NameTag");
		if (r <= 0)
			r = 1;
		tasks.add(new Tasker() {
			@Override
			public void run() {
				for (Player p : TheAPI.getOnlinePlayers())
					TabList.setFooterHeader(p);
				TabList.update();
			}
		}.runRepeating(0, Loader.tab.getInt("Options.RefleshTick.Tablist")));
		tasks.add(new Tasker() {
			@Override
			public void run() {
				for (Player p : TheAPI.getOnlinePlayers())
					TabList.setName(p);
				TabList.update();
			}
		}.runRepeating(0, r));
	}

	private static void vipslot() {
		if (setting.vip_add)
			TheAPI.setMaxPlayers(Bukkit.getMaxPlayers() + Loader.config.getInt("Options.VIPSlots.SlotsToAdd"));
		if (setting.vip_kick)
		tasks.add(new Tasker() {
			@Override
			public void run() {
				for (Player online : TheAPI.getOnlinePlayers()) {
					if (!players.contains(online.getName())) {
						if (!online.hasPermission("SCR.Other.JoinFullServer"))
							players.add(online.getName());
					} else if (online.hasPermission("SCR.Other.JoinFullServer")) {
						players.remove(online.getName());
					}
				}
			}
		}.runRepeating(0, 200));
	}

	protected static AnimationManager aa = new AnimationManager();
	
	private static void automessage() {
		tasks.add(new Tasker() {
			public void run() {
				if (TheAPI.getOnlinePlayers().size() < Loader.config.getInt("Options.AutoMessage.MinimalPlayers"))
					return;
				List<String> l = Loader.config.getStringList("Options.AutoMessage.Messages");
				if (setting.am_random) {
					for(Player p : TheAPI.getOnlinePlayers()) {
						if(Loader.config.getBoolean("Options.AutoMessage.UseJson")) {
							String json = StringUtils.colorizeJson(aa.replaceWithoutColors(p,TheAPI.getRandomFromList(l)));
							Ref.sendPacket(p, NMSAPI.getPacketPlayOutChat(ChatType.SYSTEM, NMSAPI.getIChatBaseComponentFromCraftBukkit(json)));
						}else {
							TheAPI.msg(aa.replace(p,TheAPI.getRandomFromList(l)), p);
						}
					}
				} else {
					if (l.size() <= tests)
						tests = 0;
					for(Player p : TheAPI.getOnlinePlayers()) {
						if(Loader.config.getBoolean("Options.AutoMessage.UseJson")) {
							Ref.sendPacket(p, NMSAPI.getPacketPlayOutChat(ChatType.SYSTEM, NMSAPI.getIChatBaseComponentFromCraftBukkit(StringUtils.colorizeJson(aa.replaceWithoutColors(p,l.get(tests))))));
						}else {
							TheAPI.msg(aa.replace(p,l.get(tests)), p);
						}
					}
					++tests;
				}
				aa.update();
			}

		}.runRepeating(0, 20* StringUtils.getTimeFromString(Loader.config.getString("Options.AutoMessage.Interval"))));
	}
}
