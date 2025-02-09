package me.DevTec.ServerControlReloaded.Events;

import me.DevTec.ServerControlReloaded.Commands.CommandsManager;
import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.devtec.theapi.punishmentapi.PlayerBanList;
import me.devtec.theapi.punishmentapi.PunishmentAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collection;

public class AFkPlayerEvents implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreakBlock(PlayerInteractEvent e) {
		Loader.getInstance.save(e.getPlayer());
		PlayerBanList d= PunishmentAPI.getBanList(e.getPlayer().getName());
		if (d.isJailed() || d.isTempJailed() || d.isTempIPJailed() || d.isIPJailed())
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreakBlock(PlayerCommandPreprocessEvent e) {
		if(CommandsManager.isLoaded("Other", "AFK")) {
			String c = e.getMessage().substring(1);
			if(c.toLowerCase().startsWith(Loader.cmds.getString("Other.AFK.Name").toLowerCase()))return;
			Object d = Loader.cmds.get("Other.AFK.Aliases");
			if(d instanceof Collection) {
			for(Object cmd : (Collection<?>)d)
				if(c.toLowerCase().startsWith((cmd+"").toLowerCase()))return;
			}else
			if(c.toLowerCase().startsWith((d+"").toLowerCase()))return;
		}
		Loader.getInstance.save(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreakBlock(AsyncPlayerChatEvent e) {
		Loader.getInstance.save(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreakBlock(InventoryClickEvent e) {
		Loader.getInstance.save((Player)e.getWhoClicked());
	}
}
