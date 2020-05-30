package tk.sherrao.bukkit.bungeequeue.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;

import tk.sherrao.bukkit.bungeequeue.BungeeQueue;
import tk.sherrao.bukkit.utils.plugin.SherTask;

public class QueueController extends SherTask implements PluginMessageListener {

	protected BukkitTask task;
	
	public QueueController( BungeeQueue pl, BukkitTask task ) {
		super(pl);
		
		this.task = task;
		
	}

	@Override
	public void fire() {
		if( Bukkit.getOnlinePlayers().size() == 0 )
			return;
		
		Player player = Bukkit.getOnlinePlayers().iterator().next();
		player.sendPluginMessage( pl, "BungeeCord", null  );
		
	}

	@Override
	public void onPluginMessageReceived( String channel, Player player, byte[] message ) {
		return;
		
	}
	
}