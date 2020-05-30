package tk.sherrao.bukkit.bungeequeue.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import tk.sherrao.bukkit.bungeequeue.BungeeQueue;
import tk.sherrao.bukkit.bungeequeue.tasks.PlayerQueue;
import tk.sherrao.bukkit.utils.command.CommandBundle;
import tk.sherrao.bukkit.utils.command.SherCommand;
import tk.sherrao.bukkit.utils.plugin.SherPlugin;

public class QueueCommand extends SherCommand {
    
    protected Map<String, Double> priorityList;
    protected PlayerQueue task;
    
    public QueueCommand( SherPlugin pl ) {
        super( "queue", pl );
        
        this.priorityList = new HashMap<String, Double>();
        config.getStringList( "priority.permissions" ).forEach( (str) -> { 
            String perm = str.split( ":" )[0];
            Bukkit.getPluginManager().addPermission( new Permission( perm, "BungeeQueue Priority List" ) );

            double priority = Double.valueOf( str.split( ":" )[1] );
            priorityList.put( perm, priority ); 
            
        } ); 
        
        this.task = ((BungeeQueue) pl).getPlayerQueue();
        
    }
    
    @Override
    public void onExecute( CommandBundle bundle ) {
        if( bundle.isPlayer() ) {
            Player player = (Player) bundle.sender;
            boolean allowed = false;
            for( Entry<String, Double> entry : priorityList.entrySet() ) {
                if( player.hasPermission( entry.getKey() ) ) { 
                    task.add( player, entry.getKey(), entry.getValue() );
                    allowed = true;
                    
                }                
            }
            
            if( !allowed ) 
                bundle.message( config.getString( "messages.command.no-perms" ) );
            
        } else
            bundle.message( config.getString( "messages.command.sender-is-console" ) );
        
    }
    
    public Map<String, Double> getPriorityList() { return priorityList; }
    
}