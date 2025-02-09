package me.DevTec.ServerControlReloaded.Commands.Other.tablist;

import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.SCR.Loader.Placeholder;
import me.DevTec.ServerControlReloaded.Utils.TabList;
import me.devtec.theapi.utils.StringUtils;
import org.bukkit.command.CommandSender;

public class SuffixTab {

	public SuffixTab(CommandSender s, String[] args) {
		String type = args[1];
		if(!(type.equalsIgnoreCase("player")||type.equalsIgnoreCase("group"))) {
			Loader.advancedHelp(s, "TabList", "Other", "Suffix");
			return;
		}
		String name = args[2];
		String ttype = args[3];
		if(!(ttype.equalsIgnoreCase("nametag")||ttype.equalsIgnoreCase("tablist")||ttype.equalsIgnoreCase("tab"))) {
			Loader.advancedHelp(s, "TabList", "Other", "Suffix");
			return;
		}
		if(args[4].equalsIgnoreCase("set")) {
			if(args.length==5) {
				Loader.advancedHelp(s, "TabList", "Other", "Suffix");
				return;
			}
			if(type.equalsIgnoreCase("player")) {
				String val = StringUtils.buildString(5, args);
				if(val.startsWith("\"") && val.endsWith("\""))val=val.substring(1, val.length()-1);
				TabList.setSuffix(name, ttype.equalsIgnoreCase("nametag"), 0, val);
				Loader.sendMessages(s, "TabList.Set."+(ttype.equalsIgnoreCase("nametag")?"NameTag":"TabList")+".Suffix.Player", 
						Placeholder.c().add("%value%", val).replace("%name%", name));
				return;
			}
			String val = StringUtils.buildString(5, args);
			if(val.startsWith("\"") && val.endsWith("\""))val=val.substring(1, val.length()-1);
			TabList.setSuffix(name, ttype.equalsIgnoreCase("nametag"), 2, val);
			Loader.sendMessages(s, "TabList.Set."+(ttype.equalsIgnoreCase("nametag")?"NameTag":"TabList")+".Suffix.Group", 
					Placeholder.c().add("%value%", val).replace("%name%", name));
			return;
		}
		if(args[4].equalsIgnoreCase("get")) {
			if(type.equalsIgnoreCase("player")) {
				Loader.sendMessages(s, "TabList.Get."+(ttype.equalsIgnoreCase("nametag")?"NameTag":"TabList")+".Suffix.Player", 
						Placeholder.c().add("%value%", TabList.getSuffix(name, ttype.equalsIgnoreCase("nametag"), 0)).replace("%name%", name));
				return;
			}
			Loader.sendMessages(s, "TabList.Get."+(ttype.equalsIgnoreCase("nametag")?"NameTag":"TabList")+".Suffix.Group", 
					Placeholder.c().add("%value%", TabList.getSuffix(name, ttype.equalsIgnoreCase("nametag"), 2)).replace("%name%", name));
			return;
		}
		Loader.advancedHelp(s, "TabList", "Other", "Suffix");
	}
}
