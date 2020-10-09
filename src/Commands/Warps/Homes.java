package Commands.Warps;

import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ServerControl.Loader;
import ServerControl.Loader.Placeholder;
import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.DataKeeper.User;

public class Homes implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if (args.length == 0) {
			if (s instanceof Player) {
				Player p = (Player) s;
				if (Loader.has(s, "Homes", "Warps")) {
					User d = TheAPI.getUser(p);
					Set<String> ne = d.getKeys("Homes");
					if (!ne.isEmpty()) {
						Loader.sendMessages(s, "Home.List", Placeholder.c()
								.add("%homes%", StringUtils.join(ne, ", ")));
						return true;
					}
					Loader.sendMessages(s, "Home.EmptyList");
					return true;
				}
				Loader.noPerms(s, "Homes", "Warps");
				return true;
			}
			return true;
		}
		if (Loader.has(s, "Homes", "Warps", "Other")) {
			User d = TheAPI.getUser(args[0]);
			Set<String> ne = d.getKeys("Homes");
			if (!ne.isEmpty()) {
				Loader.sendMessages(s, "Home.Other.List", Placeholder.c()
						.add("%homes%", StringUtils.join(ne, ", "))
						.add("%player%", args[0])
						.add("%playername%", args[0]));
				return true;
			}
			Loader.sendMessages(s, "Home.Other.EmptyList", Placeholder.c()
					.add("%player%", args[0])
					.add("%playername%", args[0]));
			return true;
		}
		Loader.noPerms(s, "Homes", "Warps", "Other");
		return true;
	}
}
