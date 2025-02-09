package me.DevTec.ServerControlReloaded.Commands.Server;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.Utils.setting;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.scheduler.Scheduler;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.reflections.Ref;

public class BigTask {
	public static int r = -1;

	public static enum TaskType {
		STOP, RESTART, RELOAD
	}

	private static TaskType s;

	public static boolean start(TaskType t, long h) {
		s = t;
		if ((t == TaskType.STOP ? setting.warn_stop
				: (t == TaskType.RELOAD ? setting.warn_reload : setting.warn_restart))) {
			if(h > 15 && h!=30 && h!=45 && h!=60)
			for (String s : Loader.config.getStringList("Options.WarningSystem."
					+ (t == TaskType.STOP ? "Stop" : (t == TaskType.RELOAD ? "Reload" : "Restart"))
					+ ".Messages"))
				TheAPI.broadcastMessage(
						s.replace("%time%", "" + StringUtils.setTimeToString(h)));
			if (r == -1) {
				r = new Tasker() {
					long f = h;

					@Override
					public void run() {
						if (f <= 0) {
							new Tasker() {
								public void run() {
									end();
								}
							}.runTaskSync();
							return;
						} else if (f == 60 || f == 45 || f == 30 || f == 15 || f <= 5) {
							for (String s : Loader.config.getStringList("Options.WarningSystem."
									+ (t == TaskType.STOP ? "Stop" : (t == TaskType.RELOAD ? "Reload" : "Restart"))
									+ ".Messages"))
								TheAPI.broadcastMessage(
										s.replace("%time%", "" + StringUtils.setTimeToString(f)));
						}
						--f;
					}
				}.runRepeating(0, 20);
				return true;
			}
			return false;
		} else {
			end();
			return true;
		}
	}

	public static void cancel() {
		if (r != -1) {
			Scheduler.cancelTask(r);
			r = -1;
			TheAPI.broadcastMessage("&eCancelled "
					+ (s == TaskType.STOP ? "stopping" : (s == TaskType.RELOAD ? "reloading" : "restarting"))
					+ " of server!");
		}
	}

	public static void end() {
		if (r != -1) {
			Scheduler.cancelTask(r);
			r = -1;
			String text = "&c" + (s == TaskType.STOP ? "Stopping" : (s == TaskType.RELOAD ? "Reloading" : "Restarting"))
					+ " of server..";
			TheAPI.broadcastMessage(text);
			switch (s) {
			case RELOAD:
				Bukkit.reload();
				break;
			case RESTART:
				for(Player s : TheAPI.getOnlinePlayers())
					s.kickPlayer(TheAPI.colorize(text));
				if (Ref.getClass("net.md_5.bungee.api.ChatColor")!=null)
					try {
					Bukkit.spigot().restart();
					}catch(Exception | NoSuchMethodError e) {
						Bukkit.shutdown();
					}
				else
					Bukkit.shutdown();
				break;
			case STOP:
				for(Player s : TheAPI.getOnlinePlayers())
					s.kickPlayer(TheAPI.colorize(text));
				Bukkit.shutdown();
				break;
			}
	}}
}
