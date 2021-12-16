package bubbles.sabotage.plugin;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import bubbles.sabotage.plugin.fileIO.ReadWrite;
import bubbles.sabotage.plugin.game.Game;
import bubbles.sabotage.plugin.groups.SabKits;
import bubbles.sabotage.plugin.items.GrapplingHook;
import bubbles.sabotage.plugin.items.HealingWand;
import bubbles.sabotage.plugin.items.KitSelect;
import bubbles.sabotage.plugin.items.Sniper;
import bubbles.sabotage.plugin.items.Steak;
import bubbles.sabotage.plugin.items.TeamSelect;
import bubbles.sabotage.plugin.items.Trash;
import bubbles.sabotage.plugin.items.customitem.CustomItem;

public class Main extends JavaPlugin {
	
	private Commands cmds;
	private Scoreboard board;
	private Game game;
	private HashMap<String, CustomItem> items = new HashMap<String, CustomItem>();
	
	@Override
	public void onEnable() {
		ConfigurationSerialization.registerClass(SabKits.class, "SabKits");
		ConfigurationSerialization.registerClass(Kit.class, "Kit");
		ConfigurationSerialization.registerClass(Bomb.class, "Bomb");
		ConfigurationSerialization.registerClass(Game.class, "Game");
		board = getServer().getScoreboardManager().getNewScoreboard();
		
		load("Sab");
		loadCustomItems();
		game.setup();

		cmds = new Commands(game);
		getCommand("sab").setExecutor(cmds);
		getCommand("team").setExecutor(cmds);
		getCommand("kit").setExecutor(cmds);
		logOut("Commands Loaded!");
		
	}
	
	@Override
	public void onDisable() {
		game.getTeams().unregisterAll();
	}
	
	private void loadCustomItems() {
		items.put("healing", new HealingWand());
		items.put("grappler", new GrapplingHook());
		items.put("trash", new Trash());
		items.put("sniper", new Sniper());
		items.put("steak", new Steak());
		items.put("kit", new KitSelect());
		items.put("team", new TeamSelect());
		for (String name : items.keySet()) {
			logOut("Item Loaded: " + name + " : " + items.get(name).getItem().getType());
		}
	}
	
	public HashMap<String, CustomItem> getItems() {
		return items;
	}

	public void load(String gameName) {
		
		if (game!=null) {
			game.getTeams().unregisterAll();
		}
		
		if (ReadWrite.getSabGame(gameName)!=null) {
			logOut("Loading " + gameName + "!");
			game = ReadWrite.getSabGame(gameName);
			for (int i = 0; i < game.getTeamNames().size(); i++) {
				game.addTeam(game.getTeamNames().get(i), game.getTeamSpawns().get(i));
				logOut("Team " + game.getTeamNames().get(i) + " has been loaded! (Spawn: " + game.getTeamSpawns().get(i) + ")");
			}
		} else {
			logOut("Creating " + gameName + "...");
			game = new Game(gameName, "world");
		}
		
		if (ReadWrite.getSabBombs(gameName)!=null) {
			logOut("Loading Bombs!");
			ArrayList<Bomb> bombs = ReadWrite.getSabBombs(gameName);
			for (Bomb bomb : bombs) {
				game.addBomb(bomb);
				if (bomb.getOwner()!=null) {
					logOut("Bomb for Team " + bomb.getOwner().getName() + " loaded!");
				} else {
					logOut("Bomb for No Team loaded!");
				}
			}
		} else {
			logOut("Creating Bombs!");
			game.addBomb("red", 0, 3, 0, 0, 1, 0, 120);
		}
		
		if (ReadWrite.getSabKits(gameName)!=null) {
			logOut("Loading Kits!");
			SabKits kits = ReadWrite.getSabKits(gameName);
			game.setKits(kits);
			logOut("Kits Loaded: " + kits);
		} else {
			logOut("Creating Kits!");
		}
		
		//game.setup();
	}
	
	public void logOut(String log) {
		System.out.println("[Sabotage] " + log);
	}

	public Scoreboard getScoreboard() {
		return board;
	}
	
	public Commands getCmds() {
		return cmds;
	}

	public void updateScoreboard() {
		for (Player p : getServer().getOnlinePlayers()) {
			p.setScoreboard(board);
		}
	}

	public Game getGame() {
		return game;
	}
}
