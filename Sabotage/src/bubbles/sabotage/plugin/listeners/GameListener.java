package bubbles.sabotage.plugin.listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import bubbles.sabotage.plugin.game.Game;

public class GameListener implements Listener {
	
	private Game game;
	
	public GameListener(Game game) {
		this.game = game;
		game.getPlugin().getServer().getPluginManager().registerEvents(this, game.getPlugin());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		game.showPlayer(player);
		player.setGameMode(GameMode.SURVIVAL);
		game.spectator(player);
		game.getPlugin().updateScoreboard();
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		game.spectator(player);
		if (game.isActive()) {
			game.deathCounter(player, 8);
		}
	}
	
	@EventHandler
	public void onVoid(EntityDamageEvent e) {
		if (e.getCause()==DamageCause.VOID && e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			p.setHealth(0);
			p.teleport(game.getWorld().getSpawnLocation());
		}
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBucket(PlayerBucketEmptyEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBucket(PlayerBucketFillEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onWorldInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (!game.getPlayerStatus(player)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onHunger(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}
	
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onAttack(EntityDamageByEntityEvent e) {
		if (e.getDamager().getType()==EntityType.PLAYER) {
			Player player = (Player) e.getDamager();
			if (!game.getPlayerStatus(player)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		if (e.getEntity() instanceof Arrow) {
			Arrow arrow = (Arrow) e.getEntity();
			arrow.remove();
		}
	}
		
		
}
