package tk.sherrao.bukkit.bungeequeue.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import tk.sherrao.bukkit.bungeequeue.BungeeQueue;
import tk.sherrao.bukkit.bungeequeue.tasks.PlayerQueue;
import tk.sherrao.bukkit.utils.plugin.SherEventListener;

public class PlayerDisconnectListener extends SherEventListener {

	protected PlayerQueue queue;
	
	public PlayerDisconnectListener( BungeeQueue pl ) {
		super(pl);
		
		this.queue = pl.getPlayerQueue();
		
	}
	
	@EventHandler( priority = EventPriority.HIGH )
	public void onPlayerDisconnect( PlayerQuitEvent event ) {
		queue.remove( event.getPlayer() );
		
	}
	
}
