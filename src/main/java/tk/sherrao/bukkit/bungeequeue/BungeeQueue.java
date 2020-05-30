package tk.sherrao.bukkit.bungeequeue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

import tk.sherrao.bukkit.bungeequeue.commands.QueueCommand;
import tk.sherrao.bukkit.bungeequeue.listeners.SignBreakListener;
import tk.sherrao.bukkit.bungeequeue.listeners.SignInteractListener;
import tk.sherrao.bukkit.bungeequeue.listeners.SignPlaceListener;
import tk.sherrao.bukkit.bungeequeue.tasks.PlayerQueue;
import tk.sherrao.bukkit.bungeequeue.tasks.QueueController;
import tk.sherrao.bukkit.bungeequeue.tasks.SignUpdater;
import tk.sherrao.bukkit.utils.plugin.SherPlugin;

public class BungeeQueue extends SherPlugin {
    
    protected PlayerQueue playerQueue;
    protected QueueController queueController;
    protected SignUpdater signUpdater;
    protected QueueCommand queueCmd;
    protected int delay;
    
    public BungeeQueue() {
        super();
        
    }
    
    @Override
    public void onLoad() {
        super.onLoad();
        
    }
    
    @Override
    public void onEnable() { 
        super.onEnable();
        
        Bukkit.getPluginManager().addPermission( 
                new Permission( "bungeequeue.sign.create", "Allows the creation of BungeeQueue sings" ) );
        Bukkit.getPluginManager().addPermission( 
                new Permission( "bungeequeue.sign.use", "Allows the usage of BungeeQueue sings" ) );
        Bukkit.getPluginManager().addPermission( 
                new Permission( "bungeequeue.sign.destroy", "Allows the destruction of BungeeQueue sings" ) );
        
        playerQueue = new PlayerQueue( this );
        signUpdater = new SignUpdater( this );
        queueCmd = new QueueCommand( this );
        delay = config.getInt( "delay-betweem-queue-ping" );
        
        super.getServer().getMessenger().registerOutgoingPluginChannel( this, "BungeeCord" );
        super.getServer().getMessenger().registerIncomingPluginChannel( this, "BungeeCord", playerQueue );
        
        super.registerAsyncRepeatingTask( playerQueue, delay, delay );
        super.registerAsyncRepeatingTask( queueController, delay, delay );
        super.registerAsyncRepeatingTask( signUpdater, 1, 1 );
        super.registerCommand( "queue", queueCmd );
        
        super.registerEventListener( "Sign-Place Listener", new SignPlaceListener( this ) );
        super.registerEventListener( "Sign-Interact Listener", new SignInteractListener( this ) );
        super.registerEventListener( "Sign-Destroy Listener", new SignBreakListener( this ) );
        initPlaceholderFile();
        
        super.complete();

    }
    
    @Override
    public void onDisable() { 
        super.onDisable();
        
    }
    
    private void initPlaceholderFile() {
        File file = null;
        try {
            file  = super.createFile( "placeholders.txt" );
            if( !file.exists() )
                file.createNewFile();
            
        } catch ( IOException e ) { e.printStackTrace(); }
        
        try( BufferedWriter out = new BufferedWriter( new FileWriter( file ) ) ) {
            out.append( "Placeholders for common messages in the config.yml file:" );
            out.append( "These placeholders work for the messages.queue.* section except for messages.queue.tp-to-server" );
            out.newLine();
            out.newLine();
            out.append( "'[chance]' -> The chance a player has to get teleported to the BungeeServer " );
            out.newLine();
            out.append( "'[pos]' -> The position of the player in a queue" );
            out.newLine();
            out.append( "'[max-pos]' -> The amount of players in the queue" );
            out.newLine();
            out.append( "'[bungee-server-name]' -> The name of the BungeeCord Server " );
            out.newLine();
            out.newLine();
            out.append( "Placeholders for BungeeQueue signs:" );
            out.append( "If you'd like to display the amount of people from a certain priority-list, " );
            out.append( "surround the first three letter of the name of the rank with '[' and ']', e.g: regular -> [reg] " );
            
        } catch ( IOException e ) { e.printStackTrace(); }
        
    }
    
    public PlayerQueue getPlayerQueue() { return playerQueue; }

    public SignUpdater getSignUpdater() { return signUpdater; }

}