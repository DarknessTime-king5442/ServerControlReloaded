package Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import ServerControl.Loader;
import me.Straiker123.TheAPI;
		
public class NameTagChanger {
	public static void setNameTag(Player p, String prefix, String suffix) {
		for(Player player:Bukkit.getOnlinePlayers()) {
        	if(Loader.tab.getBoolean("SortTabList")) {
	            	if(Loader.tab.getBoolean("ModifyNameTags"))
	            		TheAPI.getNameTagAPI(p, TabList.replace(prefix,p), TabList.replace(suffix,p))
	            		.setNameTag(TabList.getGroup(p)+Tasks.ss.get(p), player.getScoreboard());
	            	else {
	     				String pname = p.getName();
					 pname = pname.substring(0, 11);
	            		TheAPI.getNameTagAPI(p, null, null).setNameTag(pname, player.getScoreboard());
		}}else {
        	if(Loader.tab.getBoolean("ModifyNameTags")) {
	            	if(Loader.tab.getBoolean("ModifyNameTags"))
	            		TheAPI.getNameTagAPI(p, TabList.replace(prefix,p), TabList.replace(suffix,p))
	            		.setNameTag(Tasks.ss.get(p), player.getScoreboard());
	 
        	}}}}
	public static void remove(Player p) {
    	for(Team t : p.getScoreboard().getTeams()) {
    		t.unregister();
    	}
	}
}