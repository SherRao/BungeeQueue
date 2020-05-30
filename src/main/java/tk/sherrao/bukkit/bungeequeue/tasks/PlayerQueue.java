package tk.sherrao.bukkit.bungeequeue.tasks;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tk.sherrao.bukkit.utils.plugin.SherEventListener;
import tk.sherrao.bukkit.utils.plugin.SherPlugin;
import tk.sherrao.bukkit.utils.plugin.SherTask;

public class PlayerQueue extends SherTask implements PluginMessageListener {
    
    private final class Lock { }
    private final Object lock = new Lock();
    
    protected Map<Player, Double> queuedPlayers;
    protected Map<String, Integer> priorityTally;
    protected List<Player> indexes;
    
    protected int tpPlayersPerCheck, maxPlayerAllowed;
    protected String server;
    protected String joinMsg, pingMsg, alreadyQueuedMsg, tpMsg;
    protected ByteArrayDataOutput outTP, outQuery;
    protected ByteArrayDataInput inQuery;

    protected double random, probability;
    protected int playersTped, serverPlayerCount;
    
    public PlayerQueue( SherPlugin pl ) {
        super(pl);
    
        this.queuedPlayers = Collections.synchronizedMap( new LinkedHashMap<>() );    
        this.priorityTally = Collections.synchronizedMap( new HashMap<>() );    
        config.getStringList( "priority.permissions" ).forEach( (str) -> { 
            String perm = str.split( ":" )[0];
            priorityTally.put( perm, 0 );
            
        } ); 
        
        this.indexes = Collections.synchronizedList( new ArrayList<>() );
        
        this.tpPlayersPerCheck = config.getInt( "tp-players-per-check" );
        this.server = config.getString( "bungee.server-name" );
        this.joinMsg = config.getString( "messages.queue.queue-join" );
        this.pingMsg = config.getString( "messages.queue.queue-ping" );
        this.alreadyQueuedMsg = config.getString( "messages.queue.already-in-queue" );
        this.tpMsg = config.getString( "messages.queue.tp-to-server" );
      
        this.outTP = ByteStreams.newDataOutput();
        outTP.writeUTF( "Connect" );
        outTP.writeUTF( server ); 
        
        this.outQuery = ByteStreams.newDataOutput();
        outQuery.writeUTF( "PlayerCount" );
        outQuery.writeUTF( server );
        
        pl.registerEventListener( new SherEventListener( pl ) {
            
            @EventHandler( priority = EventPriority.NORMAL )
            public void onPlayerLeave( PlayerQuitEvent event ) {
                remove( event.getPlayer() );

            }
            
        } );
        
    }
    
    private String formatMsg( String str, Player player ) {
        return str.replace( "[chance]", Double.toString( queuedPlayers.get( player ) ).replaceAll( "0.", "" ) )
                .replace( "[pos]", Integer.toString( indexes.indexOf( player ) ) )
                .replace( "[max-pos]", Integer.toString( queuedPlayers.size() )) 
                .replace( "[bungee-server-name]", server );
     
    }

    @Override
	public void onPluginMessageReceived( String channel, Player player, byte[] message ) {
    	if( !channel.equals( "BungeeCord" ) ) 
    		return;
    	
    	inQuery = ByteStreams.newDataInput( message );
        String subchannel = inQuery.readUTF();
        if ( subchannel.equals( "PlayerCount" ) ) 
        	serverPlayerCount = inQuery.readInt(); 
        
        else
        	return;
        
    }
    
    @Override
    public void fire() {
    	updatePlayerCount();
    	if( serverPlayerCount >= maxPlayerAllowed )
    		return;
    	
        for( Entry<Player, Double> entry : queuedPlayers.entrySet() ) {
            if( playersTped > tpPlayersPerCheck ) 
                break;
            
            Player player = entry.getKey();
            probability += entry.getValue();
            if( random <= probability ) {
            	remove( player );
                player.sendMessage( tpMsg );
                player.playSound( player.getLocation(), config.getSound( "sounds.tp-to-server" ), 1F, 1F );
                player.sendPluginMessage( pl, "BungeeCord", outTP.toByteArray() );
                playersTped++;

            } else {
                player.sendMessage( formatMsg( pingMsg, player ) );
                player.playSound( player.getLocation(), config.getSound( "sounds.queue-ping" ), 1F, 1F );
        
            }
        }
        
        random = Math.random();
        probability = 0;
        playersTped = 0;
        
    }
    
    public void updatePlayerCount() {
    	if( pl.getServer().getOnlinePlayers().size() == 0 ) {
    		serverPlayerCount = 0;
    		return;
    
    	}
    	
    	Player to = pl.getServer().getOnlinePlayers().iterator().next();
    	to.sendPluginMessage( pl, "BungeeCord", outQuery.toByteArray() );
    	
    }
    
    public void add( Player player, String priority, double level ) {
        synchronized( lock ) { 
            if( !contains( player ) ) {
                queuedPlayers.put( player, level );
                if( !priorityTally.containsKey( priority ) )
                    priorityTally.put( priority, 1 );
                
                else
                    priorityTally.put( priority, priorityTally.get( priority ) + 1 );
                
                indexes.add( player );
                player.sendMessage( formatMsg( joinMsg, player ) );
                player.playSound( player.getLocation(), config.getSound( "sounds.queue-join" ), 1F, 1F );
                
            } else {
                player.sendMessage( formatMsg( alreadyQueuedMsg.replace( "[server-name]", server ), player ) );
                player.playSound( player.getLocation(), config.getSound( "sounds.already-in-queue" ), 1F, 1F );
                
            }    
            
        }
    }
   
    public void remove( Player player ) {
        synchronized( lock ) { 
            if( contains( player ) ) {
                queuedPlayers.remove( player );
                indexes.remove( player );
                config.getStringList( "priority.permissions" ).forEach( (str) -> { 
                    String perm = str.split( ":" )[0];
                    if( player.hasPermission( perm ) ) 
                        if( priorityTally.get( perm ) != 0 )
                            priorityTally.put( perm, priorityTally.get( perm ) - 1 );
                    
                } );                
            }   
        }
    }
    
    public boolean contains( Player player ) {
        synchronized( lock ) { 
            return queuedPlayers.containsKey( player )
                    && indexes.contains( player );
            
        }
    }
    
    public Map<Player, Double> getQueuedPlayers() { return queuedPlayers; }

    public Map<String, Integer> getPriorityTally() { return priorityTally; }

}