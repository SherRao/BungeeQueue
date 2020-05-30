package tk.sherrao.bukkit.bungeequeue.listeners;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import tk.sherrao.bukkit.bungeequeue.BungeeQueue;
import tk.sherrao.bukkit.bungeequeue.commands.QueueCommand;
import tk.sherrao.bukkit.bungeequeue.tasks.PlayerQueue;
import tk.sherrao.bukkit.bungeequeue.tasks.SignUpdater;
import tk.sherrao.bukkit.utils.plugin.SherEventListener;

public class SignInteractListener extends SherEventListener {
    
    protected QueueCommand cmd;
    protected PlayerQueue queue;
    protected SignUpdater signs;
    
    protected String queueMsgSuccess, queueMsgFailure;
    protected Sound queueSoundSuccess;
    
    protected boolean leftClick, rightClick;
    
    public SignInteractListener( BungeeQueue pl ) {
        super(pl);
        
        this.cmd = (QueueCommand) pl.asCommand( "queue" );
        this.queue = pl.getPlayerQueue();
        this.signs = pl.getSignUpdater();
        
        this.queueMsgSuccess = config.getString( "messages.queue.queue-join" );
        this.queueMsgFailure = config.getString( "messages.command.no-perms" );
        this.queueSoundSuccess = config.getSound( "sounds.queue-join" );
     
        this.leftClick = config.getBoolean( "sign.queue-on-left-click" );
        this.rightClick = config.getBoolean( "sign.queue-on-right-click" );
        
    }
    
    @EventHandler( priority = EventPriority.HIGHEST )
    public void onSignInteract( PlayerInteractEvent event ) {
        Action action = event.getAction();
        if( action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR )
            return;
        
        if( action == Action.LEFT_CLICK_BLOCK && !leftClick ) 
            return;
        
        if( action == Action.RIGHT_CLICK_BLOCK && !rightClick )
            return;
        
        if( event.getClickedBlock() == null )
            return;
        
        if( !( event.getClickedBlock().getState() instanceof Sign ) )
            return;
        
        String[] lines = ((Sign) event.getClickedBlock().getState()).getLines();
        List<String> valid = signs.getLineBuffer();
        if( lines[0].equals( valid.get(0) ) 
                && lines[1].equals( valid.get(1) ) 
                && lines[2].equals( valid.get(2) ) 
                && lines[3].equals( valid.get(3) ) ) 
            event.getPlayer().performCommand( "queue" );
        
        else 
            return;
        
    }
    
}