package me.DevTec.ServerControlReloaded.Utils;

import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.SCR.Loader.Placeholder;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.apis.PluginManagerAPI;
import me.devtec.theapi.economyapi.EconomyAPI;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.datakeeper.User;
import me.devtec.theapi.utils.reflections.Ref;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Method;

public class SPlayer {
	public boolean lock;
	private final String s;
	public int kick, afk;
	public boolean bc, manual;
	public String reply, type;
	public Location l;
	
	public SPlayer(Player p) {
		s = p.getName();
	}
	
	public SPlayer(String p) {
		s = p;
	}

	
	public void setHP() {
		if(getPlayer()==null)return;
		getPlayer().setHealth(((Damageable)getPlayer()).getMaxHealth());
	}

	public void heal() {
		if(getPlayer()==null)return;
		setHP();
		setFood();
		setAir();
		setFire();
		for (PotionEffect e : getPlayer().getActivePotionEffects())
			getPlayer().removePotionEffect(e.getType());
	}

	public void setFood() {
		if(getPlayer()==null)return;
		getPlayer().setFoodLevel(20);
	}

	public void setAir() {
		if(getPlayer()==null)return;
		getPlayer().setRemainingAir(getPlayer().getMaximumAir());
	}

	public void enableTempGameMode(long time,GameMode g,boolean t){
		User s = TheAPI.getUser(this.s);
		s.set("TempGamemode.Start",System.currentTimeMillis());
		s.set("TempGamemode.Time",time);
		s.set("TempGamemode.Prev",getPlayer().getGameMode());
		if(!hasGameMode()){
			s.setAndSave("TempGamemode.Use",true);
			getPlayer().setGameMode(g);
			if(t==false){
				TheAPI.msg(Loader.getTranslation("GameMode.Temp.You").toString().replace("%time%",StringUtils.timeToString(time)).replace("%gamemode%",g.toString().toLowerCase()),getPlayer());
			}else{
				TheAPI.msg(Loader.getTranslation("GameMode.Temp.Other.Reciever").toString().replace("%time%",StringUtils.timeToString(time)).replace("%gamemode%",g.toString().toLowerCase()),getPlayer());
			}
		}else s.save();
		if(getPlayer()==null)return;
	}

	public boolean hasGameMode(){
		return TheAPI.getUser(this.s).getBoolean("TempGamemode.Use");
	}

	public void enableTempFly(long stop) {
		User s = TheAPI.getUser(this.s);
		s.set("TempFly.Start", System.currentTimeMillis());
		s.set("TempFly.Time", stop);
		if (!hasTempFlyEnabled()) {
			s.setAndSave("TempFly.Use", true);
			enableTempFly();
		}else s.save();
		if(getPlayer()==null)return;
		Loader.sendMessages(getPlayer(), "Fly.Temp.Enabled.You", Placeholder.c().add("%time%", StringUtils.setTimeToString(stop)));
	}

	public void enableTempFly() {
		if(getPlayer()==null)return;
		if (hasTempFlyEnabled()) {
			getPlayer().setAllowFlight(true);
			getPlayer().setFlying(true);
		}
	}

	public void enableFly() {
		if (hasTempFlyEnabled()) {
			TheAPI.getUser(s).setAndSave("TempFly.Use", false);
		}
		TheAPI.getUser(s).setAndSave("Fly", true);
		if(getPlayer()==null)return;
		getPlayer().setAllowFlight(true);
		getPlayer().setFlying(true);
	}

	public void disableFly() {
		User d = TheAPI.getUser(s);
		d.remove("TempFly");
		d.remove("Fly");
		d.save();
		if(getPlayer()==null)return;
		getPlayer().setFlying(false);
		getPlayer().setAllowFlight(false);
	}

	private static final Class<?> ess = Ref.getClass("com.earth2me.essentials.Essentials");
	private static final Method getUser = Ref.method(ess, "getUser", Player.class);
	
	public boolean isAFK() {
		try {
			if(ess!=null) {
			Object user = Ref.invoke(Ref.cast(ess, PluginManagerAPI.getPlugin("Essentials")), getUser, s);
			if (PluginManagerAPI.isEnabledPlugin("Essentials") && user!=null&& (boolean)Ref.invoke(user, "isAfk"))
				return true;
			}
		} catch (Exception er) {
		}
		return (Loader.getInstance.isAFK(this) || Loader.getInstance.isManualAfk(this));
	}

	public void setAFK(boolean afk) {
		if(getPlayer()==null)return;
		if (!afk) {
			Loader.getInstance.save(getPlayer());
		} else {
			Loader.getInstance.setAFK(this);
		}
	}
	public void setAFK(boolean afk, String reason) {
		if(getPlayer()==null)return;
		if (!afk) {
			Loader.getInstance.save(getPlayer());
		} else {
			Loader.getInstance.setAFK(this, reason);
		}
	}

	public void setFire() {
		if(getPlayer()==null)return;
		getPlayer().setFireTicks(-20);
	}

	public String getName() {
		return s;
	}

	public String getDisplayName() {
		return getPlayer()!=null?getPlayer().getDisplayName():s;
	}

	public String getCustomName() {
		return getPlayer()!=null?getPlayer().getDisplayName():s;
	}

	public int getFoodLevel() {
		return getPlayer()!=null?getPlayer().getFoodLevel():-1;
	}

	public double getHealth() {
		return getPlayer()!=null?((Damageable)getPlayer()).getHealth():-1;
	}

	public Player getPlayer() {
		return TheAPI.getPlayerOrNull(s);
	}

	public void toggleGod(CommandSender toggler) {
		if(getPlayer()==null)return;
		if (hasGodEnabled()) {
			Loader.sendMessages(getPlayer(), "God.Disabled.Other.Receiver");
			if (toggler != null)
			Loader.sendMessages(toggler, "God.Disabled.Other.Sender", Placeholder.c().add("%player%", getName()).add("%playername%", getPlayer().getDisplayName()).add("%customname%", getPlayer().getCustomName()));
			disableGod();
		} else {
			Loader.sendMessages(getPlayer(), "God.Enabled.Other.Receiver");
			if (toggler != null)
			Loader.sendMessages(toggler, "God.Enabled.Other.Sender", Placeholder.c().add("%player%", getName()).add("%playername%", getPlayer().getDisplayName()).add("%customname%", getPlayer().getCustomName()));
			enableGod();
		}
	}

	public void toggleFly(CommandSender toggler) {
		if(getPlayer()==null)return;
		if (hasFlyEnabled()) {
			Loader.sendMessages(getPlayer(), "Fly.Disabled.Other.Receiver");
			if (toggler != null)
			Loader.sendMessages(toggler, "Fly.Disabled.Other.Sender", Placeholder.c().add("%player%", getName()).add("%playername%", getPlayer().getDisplayName()).add("%customname%", getPlayer().getCustomName()));
			disableFly();
		} else {
			Loader.sendMessages(getPlayer(), "Fly.Enabled.Other.Receiver");
			if (toggler != null)
			Loader.sendMessages(toggler, "Fly.Enabled.Other.Sender", Placeholder.c().add("%player%", getName()).add("%playername%", getPlayer().getDisplayName()).add("%customname%", getPlayer().getCustomName()));
			enableFly();
		}
	}

	public void setWalkSpeed() {
		if(getPlayer()==null)return;
		User d = TheAPI.getUser(s);
		if (d.exist("WalkSpeed")) {
			Player f = getPlayer();
			if (d.getDouble("WalkSpeed") < 0.0)
				f.setWalkSpeed(0);
			else if (d.getDouble("WalkSpeed") > 10.0)
				f.setWalkSpeed(10);
			else
				f.setWalkSpeed(d.getFloat("WalkSpeed"));
		}
	}

	public void setFlySpeed() {
		if(getPlayer()==null)return;
		User d = TheAPI.getUser(s);
		if (d.exist("FlySpeed")) {
			Player f = getPlayer();
			if (d.getDouble("FlySpeed") < 0.0)
				f.setFlySpeed(0);
			else if (d.getDouble("FlySpeed") > 10.0)
				f.setFlySpeed(10);
			else
				f.setFlySpeed(d.getFloat("FlySpeed"));
		}
	}

	public void enableGod() {
		TheAPI.getUser(s).setAndSave("God", true);
		setHP();
		setFood();
		setFire();
	}

	public boolean hasPermission(String perm) {
		return hasPerm(perm);
	}

	public boolean hasPerm(String perm) {
		if(getPlayer()==null)return false;
		return getPlayer().hasPermission(perm);
	}

	public void disableGod() {
		User d = TheAPI.getUser(s);
		d.remove("God");
		d.save();
	}

	public void createEconomyAccount() {
		if (setting.eco_multi && EconomyAPI.getEconomy() != null)
			EconomyAPI.createAccount(s);
	}

	public void setGamamode() {
		if(getPlayer()==null)return;
		if (!getPlayer().hasPermission("SCR.Other.GamemodeChangePrevent")) {
			if (Loader.mw.exists("WorldsSettings." + getPlayer().getWorld().getName() + ".GameMode"))
				try {
				getPlayer().setGameMode(GameMode.valueOf(Loader.mw.getString("WorldsSettings." + getPlayer().getWorld().getName() + ".GameMode").toUpperCase()));
				}catch(Exception | NoSuchFieldError err) {}
			}
	}

	public boolean hasFlyEnabled() {
		return TheAPI.getUser(s).getBoolean("Fly")||getPlayer()!=null&&getPlayer().isFlying();
	}

	public boolean hasGodEnabled() {
		return TheAPI.getUser(s).getBoolean("God");
	}

	public boolean hasTempFlyEnabled() {
		return TheAPI.getUser(s).getBoolean("TempFly.Use");
	}
}
