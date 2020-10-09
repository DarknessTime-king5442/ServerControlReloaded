package Commands.BanSystem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ServerControl.Loader;
import ServerControl.Loader.Placeholder;
import me.DevTec.TheAPI.PunishmentAPI.PlayerBanList;
import me.DevTec.TheAPI.PunishmentAPI.PunishmentAPI;

public class UnBanIP implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (Loader.has(s, "UnBanIP", "BanSystem")) {
			if (args.length == 0) {
				Loader.Help(s, "UnBanIP", "BanSystem");
				return true;
			}
			PlayerBanList p = PunishmentAPI.getBanList(args[0]);
			if (p.isIPBanned() || p.isTempIPBanned()) {
				PunishmentAPI.unbanIP(args[0]);
				Loader.sendMessages(s, "BanSystem.UnBanIP.Sender", Placeholder.c().replace("%operator%", s.getName())
						.replace("%ip%", args[0]));
				Loader.sendBroadcasts(s, "BanSystem.UnBanIP.Admins", Placeholder.c().replace("%operator%", s.getName())
						.replace("%ip%", args[0]));
				return true;
			}
			Loader.sendMessages(s, "BanSystem.Not.Banned", Placeholder.c().replace("%playername%", args[0]).replace("%player%", args[0]));
			return true;
		}
		Loader.noPerms(s, "UnBanIP", "BanSystem");
		return true;
	}
}
