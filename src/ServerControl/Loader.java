package ServerControl;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import Commands.Tpa.RequestMap;
import Events.AFKPlus;
import Events.AFkPlayerEvents;
import Events.ChatFormat;
import Events.CreatePortal;
import Events.DeathEvent;
import Events.DisableItems;
import Events.EntitySpawn;
import Events.FarmingSystem;
import Events.LoginEvent;
import Events.OnPlayerJoin;
import Events.RewardsListenerChat;
import Events.SecurityListenerAntiAD;
import Events.SecurityListenerCooldowns;
import Events.SecurityListenerV3;
import Events.Signs;
import Events.WorldChange;
import Utils.AFK;
import Utils.Colors;
import Utils.Configs;
import Utils.Converter;
import Utils.Metrics;
import Utils.MultiWorldsUtils;
import Utils.ScoreboardStats;
import Utils.TabList;
import Utils.Tasks;
import Utils.VaultHook;
import Utils.setting;
import me.DevTec.ConfigAPI;
import me.DevTec.EconomyAPI;
import me.DevTec.PluginManagerAPI;
import me.DevTec.SoundAPI;
import me.DevTec.TheAPI;
import me.DevTec.NMS.NMSAPI;
import me.DevTec.Other.LoaderClass;
import me.DevTec.Other.StringUtils;
import me.DevTec.Placeholders.PlaceholderAPI;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Loader extends JavaPlugin implements Listener {
	public static List<Plugin> addons = new ArrayList<Plugin>();
	public static ConfigAPI trans = TheAPI.getConfig("ServerControlReloaded", "Translations");
	public static ConfigAPI config = TheAPI.getConfig("ServerControlReloaded", "Config");
	public static ConfigAPI sb = TheAPI.getConfig("ServerControlReloaded", "Scoreboard");
	public static ConfigAPI tab = TheAPI.getConfig("ServerControlReloaded", "TabList");
	public static ConfigAPI mw = TheAPI.getConfig("ServerControlReloaded", "MultiWorlds");
	public static ConfigAPI kit = TheAPI.getConfig("ServerControlReloaded", "Kits");
	
	public static Economy econ;
	public static Loader getInstance;

	public String getPrefix(Player p) {
		if (API.existVaultPlugin() && vault != null) {
			if (getGroup(p) != null && vault.getGroupPrefix(p.getWorld().getName(), getGroup(p)) != null)
				return vault.getGroupPrefix(p.getWorld().getName(), getGroup(p));
			return "";
		}
		return "";
	}

	public String getSuffix(Player p) {
		if (API.existVaultPlugin() && vault != null) {
			if (getGroup(p) != null && vault.getGroupSuffix(p.getWorld().getName(), getGroup(p)) != null)
				return vault.getGroupSuffix(p.getWorld().getName(), getGroup(p));
			return "";
		}
		return "";
	}

	public static String FormatgetGroup(Player p) {
		try {
			if (API.existVaultPlugin())
				if (vault != null) {
					if (Loader.config.getString("Chat-Groups." + Loader.vault.getPrimaryGroup(p)) != null)
						return vault.getPrimaryGroup(p);
				}
			return "DefaultFormat";
		} catch (Exception e) {
			return "DefaultFormat";
		}
	}

	public static void setupChatFormat(Player p) {
		if (config.exist("Chat-Groups." + FormatgetGroup(p) + ".Name")) {
			String g = TheAPI.colorize(PlaceholderAPI.setPlaceholders(p,
					config.getString("Chat-Groups." + FormatgetGroup(p) + ".Name"))).replace("%", "%%");
			g = ChatFormat.r(p, g, null, false);
			g = g.replace("%%", "%");
			API.setDisplayName(p, Colors.colorize(g, false, p));
		} else
			API.setDisplayName(p, getInstance.getPrefix(p) + p.getName() + getInstance.getSuffix(p));
	}

	public static String PlayerNotEx(String s) {
		return Loader.s("Prefix") + Loader.s("PlayerNotExists").replace("%player%", s).replace("%playername%", s);
	}

	public static String PlayerNotOnline(String s) {
		return Loader.s("Prefix") + Loader.s("PlayerNotOnline").replace("%player%", s).replace("%playername%", s);
	}

	public String getGroup(Player p) {
		try {
			if (API.existVaultPlugin()) {
				if (Loader.vault != null && Loader.vault.getPrimaryGroup(p) != null)
					return Loader.vault.getPrimaryGroup(p);
				return "";
			}
		} catch (Exception e) {
			return "";
		}
		return "";
	}

	public static void Help(CommandSender s, String cmd, String help) {
		TheAPI.msg(config.getString("HelpFormat").replace("%prefix%", Loader.s("Prefix")).replace("%command%", cmd)
				.replace("%space%", " - ").replace("%help%", Loader.s("Help." + help)), s);
	}

	public String isAfk(Player p) {
		return API.getSPlayer(p).isAFK() ? tab.getString("AFK.IsAFK") : tab.getString("AFK.IsNotAFK");
	}

	private String getColoredPing(Player p) {
		int s = TheAPI.getPlayerPing(p);
		if (s >= 500)
			return TheAPI.colorize("&c" + s);
		if (s >= 200 && s < 500)
			return TheAPI.colorize("&e" + s);
		if (s >= 0 && s < 200)
			return TheAPI.colorize("&a" + s);
		return TheAPI.colorize("&4" + s);
	}

	public String pingPlayer(Player who) {
		if (tab.getBoolean("Colored-Ping") == true)
			return getColoredPing(who);
		return String.valueOf(TheAPI.getPlayerPing(who));
	}
	private boolean disabling;
	@Override
	public void onLoad() {
		int count = 0;
		for(String s : LoaderClass.plugin.getDescription().getVersion().split("\\."))
			count+=Integer.valueOf(s);
		if(count < 9) {
			disabling = true;
			return;
		}
		getInstance = this;
		Configs.load();
		regClasses();
		if (API.existVaultPlugin()) {
			setupEco();
		} else {
			TheAPI.getConsole().sendMessage(
					TheAPI.colorize(Loader.s("Prefix") + "&8*********************************************"));
			TheAPI.getConsole()
					.sendMessage(TheAPI.colorize(Loader.s("Prefix") + "&6INFO: Missing Vault plugin for Economy."));
			TheAPI.getConsole().sendMessage(
					TheAPI.colorize(Loader.s("Prefix") + "&8*********************************************"));
		}
	}

	private void regClasses() {
		try {
			new API();
			new MultiWorldsUtils();
			new RequestMap();
			new ScoreboardStats();
			new Tasks();
			setting.load();
		} catch (NoClassDefFoundError | Exception e) {
			Bukkit.getLogger().severe(TheAPI.colorize(Loader.s("Prefix") + "&cError when loading classes"));
		}
	}

	long loading;

	@Override
	public void onEnable() {
		if(disabling) {
			TheAPI.msg("&0[&cServerControlReloaded&0] &7You are using old version of TheAPI", TheAPI.getConsole());
			TheAPI.msg("&0[&cServerControlReloaded&0] &7Please update TheAPI to newest version!", TheAPI.getConsole());
			TheAPI.msg("&0[&cServerControlReloaded&0] &7Links:", TheAPI.getConsole());
			TheAPI.msg("&0[&cServerControlReloaded&0] &7 Discord: &ehttps://discord.io/spigotdevtec", TheAPI.getConsole());
			TheAPI.msg("&0[&cServerControlReloaded&0] &7 Github: &ehttps://github.com/TheDevTec/TheAPI", TheAPI.getConsole());
			TheAPI.msg("&0[&cServerControlReloaded&0] &7 Spigot: &ehttps://www.spigotmc.org/resources/theapi-1-7-10-up-to-1-16-2.72679/", TheAPI.getConsole());
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		if (ver() == null) {
			TheAPI.getConsole().sendMessage(
					TheAPI.colorize(Loader.s("Prefix") + "&8*********************************************"));
			TheAPI.getConsole().sendMessage(TheAPI.colorize(Loader.s("Prefix")
					+ "&4ERROR: You are using an unsupported version of the plugin for this server."));
			TheAPI.getConsole().sendMessage(TheAPI.colorize(Loader.s("Prefix") + "&4ERROR: Disabling plugin..."));
			TheAPI.getConsole().sendMessage(
					TheAPI.colorize(Loader.s("Prefix") + "&8*********************************************"));
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		if (API.existVaultPlugin())
			setupEco();
		loading = System.currentTimeMillis() / 100;
		MultiWorldsUtils.LoadWorlds();
		if (API.existVaultPlugin()) {
			setupVault();
			setupPermisions();
		}
		try {
			if (setting.timezone) {
				TimeZone.setDefault(TimeZone.getTimeZone(config.getString("Options.TimeZone.Zone")));
			}
		} catch (Exception e) {
			TheAPI.getConsole().sendMessage(
					TheAPI.colorize("&6Invalid time zone: &c" + config.getString("Options.TimeZone.Zone")));
			TheAPI.getConsole().sendMessage(TheAPI.colorize("&6List of available time zones:"));
			TheAPI.getConsole().sendMessage(TheAPI.colorize(" &6https://greenwichmeantime.com/time-zone/"));
		}
		if (vault == null && API.existVaultPlugin()) {
			TheAPI.getConsole().sendMessage(
					TheAPI.colorize(Loader.s("Prefix") + "&8*********************************************"));
			TheAPI.getConsole().sendMessage(TheAPI.colorize(
					Loader.s("Prefix") + "&6INFO: Missing Permissions plugin for Groups (TabList and ChatFormat)."));
			TheAPI.getConsole().sendMessage(
					TheAPI.colorize(Loader.s("Prefix") + "&8*********************************************"));
		}
		if (PluginManagerAPI.isEnabledPlugin("PlaceholderAPI")) {
			TheAPI.getConsole().sendMessage(
					TheAPI.colorize(Loader.s("Prefix") + "&8*********************************************"));
			TheAPI.getConsole()
					.sendMessage(TheAPI.colorize(Loader.s("Prefix") + "&aINFO: Hooked PlaceholderAPI plugin."));
			TheAPI.getConsole().sendMessage(
					TheAPI.colorize(Loader.s("Prefix") + "&8*********************************************"));
		}
		APIChecker();
		TheAPI.getConsole()
				.sendMessage(TheAPI.colorize(Loader.s("Prefix") + "&8*********************************************"));
		TheAPI.getConsole().sendMessage(
				TheAPI.colorize(Loader.s("Prefix") + "&6INFO: Newest versions of TheAPI can be found on Spigot:"));
		TheAPI.getConsole().sendMessage(
				TheAPI.colorize(Loader.s("Prefix") + "&6       https://www.spigotmc.org/resources/theapi.72679/"));
		TheAPI.getConsole()
				.sendMessage(TheAPI.colorize(Loader.s("Prefix") + "&8*********************************************"));
		Tasks();
		Converter.convert();
	}

	public void Tasks() {
		EventsRegister();
		CommmandsRegister();
		for (World wa : Bukkit.getWorlds()) {
			MultiWorldsUtils.DefaultSet(wa, Loader.mw.getString("WorldsSettings." + wa.getName() + ".Generator"));
		}
		AFK.start();
		for (Player p : TheAPI.getOnlinePlayers()) {
			SPlayer s = API.getSPlayer(p);
			if (s.hasTempFlyEnabled())
				s.enableTempFly();
			else if (s.hasPermission("servercontrol.fly") && s.hasFlyEnabled())
				s.enableFly();
			setupChatFormat(p);
			if (EconomyAPI.getEconomy() != null)
				EconomyAPI.createAccount(p);
		}
		Tasks.load();
		MultiWorldsUtils.EnableWorldCheck();
		if(TheAPI.isNewerThan(7))
		try {
			new Metrics(this);
		} catch (Exception er) {
		}
		TheAPI.msg(Loader.s("Prefix") + "&aPlugin loaded in " + (System.currentTimeMillis() / 100 - loading) + "ms",
				Bukkit.getConsoleSender());
		loading = 0;
	}

	@Override
	public void onDisable() {
		if(disabling)return;
		NMSAPI.postToMainThread(new Runnable() {
			public void run() {
				for (Player p : TheAPI.getOnlinePlayers()) {
					p.setFlying(false);
					p.setAllowFlight(false);
				}
				TabList.removeTab();
				ScoreboardStats.removeScoreboard();
				if (mw.getString("Worlds") != null)
					for (String w : mw.getStringList("Worlds")) {
						if (Bukkit.getWorld(w) != null && !mw.getBoolean("WorldsSettings." + w + ".AutoSave")) {
							Loader.info("Saving world '" + w + "'");
							Bukkit.getWorld(w).save();
							Loader.info("World '" + w + "' saved");
						}
					}
			}
		});
	}

	public static void warn(String s) {
		Bukkit.getLogger().warning(TheAPI.colorize(s));
	}

	public static void info(String s) {
		Bukkit.getLogger().info(TheAPI.colorize(s));
	}

	public static Chat vault = null;

	private boolean setupVault() {
		RegisteredServiceProvider<Chat> economyProvider = Bukkit.getServicesManager()
				.getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (economyProvider != null) {
			vault = economyProvider.getProvider();
		}

		return (vault != null);
	}

	private boolean setupCustomEco() {
		new VaultHook().hook();
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			econ = economyProvider.getProvider();

		}
		return econ != null;

	}

	public static void EconomyLog(String s) {
		if (config.getBoolean("Options.Economy.Log"))
			info("[EconomyLog] " + s);
	}

	private boolean setupEco() {
		try {
			RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager()
					.getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				if (config.getBoolean("Options.Economy.CanUseOtherEconomy")) {
					EconomyLog("Found economy '" + economyProvider.getProvider().getName()
							+ "', using setting and loading plugin economy.");
					setupCustomEco();
				} else {
					EconomyLog("Found economy '" + economyProvider.getProvider().getName()
							+ "', skipping plugin economy.");
					econ = economyProvider.getProvider();
				}
			}
			if (econ == null) {
				EconomyLog("Plugin not found any economy, loading plugin economy.");
				if (!config.getBoolean("Options.Economy.DisablePluginEconomy"))
					setupCustomEco();
			}
		} catch (Exception e) {
			EconomyLog("Error when hooking economy.");
		}
		return (econ != null);
	}

	public static Permission perms = null;

	private boolean setupPermisions() {
		RegisteredServiceProvider<Permission> economyProvider = Bukkit.getServicesManager()
				.getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (economyProvider != null) {
			perms = economyProvider.getProvider();
		}
		return (perms != null);
	}

	public static String s(String s) {
		if (trans.exist(s))
			return trans.getString(s);
		else {
			warn("String '" + s
					+ "' isn't exist in Tranlations.yml, please report this bug on https://github.com/Straiker123/ServerControlReloaded");
			return "&4ERROR, Missing path, See console for more informations";
		}
	}

	public String ver() {
		String v = null;
		String r = TheAPI.getServerVersion();
		if (r.equals("v1_7_R4")) { // required testing!
			v = "1.7.10";
		}
		if (r.contains("1_8")) { // required testing! (1.8 to 1.8.8)
			v = "1.8+";
		}
		if (r.contains("1_9")) {
			v = "1.9+";
		}
		if (r.contains("1_10")) {
			v = "1.10+";
		}
		if (r.contains("1_11")) {
			v = "1.11+";
		}
		if (r.contains("1_12")) {
			v = "1.12+";
		}
		if (r.equalsIgnoreCase("glowstone")) {
			v = "Glowstone";
		}
		if (TheAPI.isNewVersion()) {// v1_15_1 ... 1_15
			v = r.substring(1, 5).replace("_", ".") + "+";
		}
		return v;
	}

	public void APIChecker() {
		TheAPI.getConsole()
				.sendMessage(TheAPI.colorize(Loader.s("Prefix") + "&8*********************************************"));
		TheAPI.getConsole().sendMessage(
				TheAPI.colorize(Loader.s("Prefix") + "&2INFO: You are using version for servers version " + ver()));
		if (Loader.kit.getString("Kits") != null) {
			TheAPI.getConsole().sendMessage(TheAPI.colorize(Loader.s("Prefix") + "&2INFO: Loading kits.."));
			for (String s : Loader.kit.getConfigurationSection("Kits").getKeys(false)) {
				TheAPI.getConsole().sendMessage(TheAPI.colorize(
						Loader.s("Prefix") + "&2Kits: Name: " + s + ", Cooldown: "
								+ StringUtils
										.setTimeToString(StringUtils
												.getTimeFromString(Loader.kit.getString("Kits." + s + ".Cooldown")))
								+ ", Price: $" + API.setMoneyFormat(kit.getDouble("Kits." + s + ".Price"), false)));
			}
		}
		TheAPI.getConsole()
				.sendMessage(TheAPI.colorize(Loader.s("Prefix") + "&8*********************************************"));
		SoundsChecker();
	}

	private void CmdC(String s, CommandExecutor p) {
		getCommand(s).setExecutor(p);
	}

	private void CommmandsRegister() {
		CmdC("stop", new Commands.Main.Stop());
		CmdC("reload", new Commands.Main.Reload());
		CmdC("restart", new Commands.Main.Restart());
		CmdC("addons", new Commands.Addons());
		CmdC("give", new Commands.Give());
		CmdC("Kill", new Commands.Kill());
		CmdC("KillAll", new Commands.KillAll());
		CmdC("Butcher", new Commands.Butcher());
		CmdC("jail", new Commands.BanSystem.Jail());
		CmdC("TempBanIP", new Commands.BanSystem.TempBanIP());
		CmdC("unjail", new Commands.BanSystem.UnJail());
		CmdC("setjail", new Commands.BanSystem.SetJail());
		CmdC("deljail", new Commands.BanSystem.DelJail());
		CmdC("Mem", new Commands.RAM());
		CmdC("Chunks", new Commands.Chunks());
		CmdC("ClearChat", new Commands.ClearChat());
		CmdC("SCR", new Commands.ServerControl());
		CmdC("Chat", new Commands.Chat());
		CmdC("Maintenance", new Commands.Maintenance());
		CmdC("Clear", new Commands.ClearInv());
		CmdC("God", new Commands.God());
		CmdC("Heal", new Commands.Heal());
		CmdC("Fly", new Commands.Fly());
		CmdC("FlySpeed", new Commands.FlySpeed());
		CmdC("WalkSpeed", new Commands.WalkSpeed());
		CmdC("TPS", new Commands.TPS());
		CmdC("AFK", new Commands.AFK());
		CmdC("MultiWorlds", new Commands.Worlds());
		CmdC("TabList", new Commands.Tab());
		CmdC("suicide", new Commands.Suicide());
		CmdC("SetSpawn", new Commands.SetSpawn());
		CmdC("Spawn", new Commands.Spawn());
		CmdC("SetWarp", new Commands.SetWarp());
		CmdC("DelWarp", new Commands.DelWarp());
		CmdC("Warps", new Commands.Warp());
		CmdC("Skull", new Commands.Skull());
		CmdC("BalanceTop", new Commands.Economy.EcoTop());
		CmdC("Money", new Commands.Economy.Eco());
		CmdC("Pay", new Commands.Economy.Pay());
		CmdC("Home", new Commands.Home());
		CmdC("SetHome", new Commands.SetHome());
		CmdC("DelHome", new Commands.DelHome());
		CmdC("Homes", new Commands.Homes());
		CmdC("Back", new Commands.Back());
		CmdC("Return", new Commands.Back());
		CmdC("Vanish", new Commands.Vanish());
		CmdC("Sun", new Commands.Sun());
		CmdC("Thunder", new Commands.Thunder());
		CmdC("Strorm", new Commands.Thunder());
		CmdC("Rain", new Commands.Rain());
		CmdC("Day", new Commands.Day());
		CmdC("Night", new Commands.Night());
		CmdC("Kit", new Commands.Kit());
		CmdC("ChatLock", new Commands.ChatLock());
		CmdC("GameMode", new Commands.Gamemode());
		CmdC("GMS", new Commands.GamemodeS());
		CmdC("GMC", new Commands.GamemodeC());
		CmdC("GMSP", new Commands.GamemodeSP());
		CmdC("hat", new Commands.Hat());
		CmdC("GMA", new Commands.GamemodeA());
		CmdC("pm", new Commands.PrivateMessage());
		CmdC("helpop", new Commands.Helpop());
		CmdC("reply", new Commands.ReplyPrivateMes());
		CmdC("ClearConfirmToggle", new Commands.ClearConfirmToggle());
		CmdC("Kick", new Commands.BanSystem.Kick());
		CmdC("Ban", new Commands.BanSystem.Ban());
		CmdC("Immune", new Commands.BanSystem.Immune());
		CmdC("TempBan", new Commands.BanSystem.TempBan());
		CmdC("ChatFormat", new Commands.ChatFormat());
		CmdC("BanIP", new Commands.BanSystem.BanIP());
		CmdC("UnBan-IP", new Commands.BanSystem.UnBanIP());
		CmdC("UnBan", new Commands.BanSystem.UnBan());
		CmdC("tempmute", new Commands.BanSystem.TempMute());
		CmdC("mute", new Commands.BanSystem.Mute());
		CmdC("unmute", new Commands.BanSystem.UnMute());
		CmdC("warn", new Commands.BanSystem.Warn());
		CmdC("craft", new Commands.Craft());
		CmdC("enderchest", new Commands.EnderChest());
		CmdC("endersee", new Commands.EnderSee());
		CmdC("Seen", new Commands.Seen());
		CmdC("List", new Commands.ListCmd());
		CmdC("Staff", new Commands.Staff());
		CmdC("Trash", new Commands.Trash());
		CmdC("Invsee", new Commands.Invsee());
		CmdC("Enchant", new Commands.EnchantTable());
		CmdC("EnchantRemove", new Commands.EnchantTableRemove());
		CmdC("EnchantRemoveAll", new Commands.EnchantTableRemoveAll());
		CmdC("broadcast", new Commands.Broadcast());
		CmdC("multieconomy", new Commands.Economy.MultiEconomy());
		CmdC("tp", new Commands.Tpa.Tp());
		CmdC("tphere", new Commands.Tpa.Tphere());
		CmdC("tpa", new Commands.Tpa.Tpa());
		CmdC("tpahere", new Commands.Tpa.Tpahere());
		CmdC("tpblock", new Commands.Tpa.TpaBlock());
		CmdC("tpaall", new Commands.Tpa.Tpaall());
		CmdC("tpall", new Commands.Tpa.Tpall());
		CmdC("tpaccept", new Commands.Tpa.Tpaccept());
		CmdC("tpadeny", new Commands.Tpa.Tpadeny());
		CmdC("Repair", new Commands.Repair());
		CmdC("Feed", new Commands.Feed());
		CmdC("item", new Commands.Item());
		CmdC("board", new Commands.ScoreboardStats());
		CmdC("thor", new Commands.Thor());
		CmdC("spawner", new Commands.Spawner());
		CmdC("sudo", new Commands.Sudo());
		CmdC("exp", new Commands.Exp());
		CmdC("nick", new Commands.Nick());
		CmdC("nickreset", new Commands.NickReset());
		CmdC("WhoIs", new Commands.WhoIs());
		CmdC("closeinv", new Commands.CloseInventory());
		CmdC("homeother", new Commands.HomeOther());
		CmdC("tempfly", new Commands.TempFly());
		CmdC("tpcancel", new Commands.Tpa.Tpcancel());
		CmdC("mail", new Commands.Mail());

		CmdC("balance", new Commands.Economy.Balance());
		CmdC("tempjail", new Commands.BanSystem.TempJail());

		CmdC("psun", new Commands.PSun());
		CmdC("prain", new Commands.PRain());
		CmdC("pday", new Commands.PDay());
		CmdC("pnight", new Commands.PNight());
	}

	private void EventC(Listener l) {
		getServer().getPluginManager().registerEvents(l, this);
	}

	private void EventsRegister() {
		EventC(new DisableItems());
		EventC(new SecurityListenerAntiAD());
		EventC(new OnPlayerJoin());
		EventC(new SecurityListenerCooldowns());
		EventC(new ChatFormat());
		EventC(new RewardsListenerChat());
		EventC(new LoginEvent());
		EventC(new SecurityListenerV3());
		EventC(new DeathEvent());
		EventC(new AFkPlayerEvents());
		EventC(new WorldChange());
		EventC(new CreatePortal());
		EventC(new Signs());
		EventC(new FarmingSystem());
		EventC(new EntitySpawn());
		try {
			if (PluginManagerAPI.isEnabledPlugin("AFKPlus"))
				EventC(new AFKPlus());
		} catch (Exception e) {
		}
	}

	public static boolean SoundsChecker() {
		if (setting.sound && !new SoundAPI().existSound(config.getString("Options.Sounds.Sound"))) {
			TheAPI.msg("", TheAPI.getConsole());
			TheAPI.msg("", TheAPI.getConsole());
			TheAPI.msg(Loader.s("Prefix") + Loader.s("SoundErrorMessage"), TheAPI.getConsole());
			TheAPI.msg("", TheAPI.getConsole());
			TheAPI.msg("", TheAPI.getConsole());
			return false;
		}
		return true;
	}
}