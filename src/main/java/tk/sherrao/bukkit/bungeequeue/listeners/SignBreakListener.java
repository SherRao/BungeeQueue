package tk.sherrao.bukkit.bungeequeue.listeners;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

import tk.sherrao.bukkit.bungeequeue.BungeeQueue;
import tk.sherrao.bukkit.bungeequeue.tasks.SignUpdater;
import tk.sherrao.bukkit.utils.plugin.SherEventListener;

public class SignBreakListener extends SherEventListener {
    
    protected SignUpdater signs;
    protected String prefix;
    
    protected String destroyMsgSuccess, destroyMsgFailure;
    protected Sound destroySoundSuccess, destroySoundFailure;
    
    public SignBreakListener( BungeeQueue pl ) {
        super(pl);
        
        this.signs = pl.getSignUpdater();
        this.prefix = config.getString( "sign.prefix" );

        this.destroyMsgSuccess = config.getString( "messages.queue.sign-destroy" );
        this.destroyMsgFailure = config.getString( "messages.command.no-perms" );
        this.destroySoundSuccess = config.getSound( "sounds.sign-destroy-success" );
        this.destroySoundFailure = config.getSound( "sounds.sign-destroy-failure" );

    }
    
    @EventHandler( priority = EventPriority.HIGH )
    public void onSignBreak( BlockBreakEvent event ) {
        if( !( event.getBlock().getState() instanceof Sign ) )
            return;
        
        Sign sign = (Sign) event.getBlock().getState();
        String[] lines = sign.getLines();
        List<String> valid = signs.getLineBuffer();
        if( !( lines[0].equals( valid.get(0) ) 
                && lines[1].equals( valid.get(1) ) 
                && lines[2].equals( valid.get(2) ) 
                && lines[3].equals( valid.get(3) ) )) 
            return;
            
        Player player = event.getPlayer();
        if( player.hasPermission( "bungeequeue.sign.destroy" ) ) {
            player.sendMessage( destroyMsgSuccess );
            player.playSound( player.getLocation(), destroySoundSuccess, 1F, 1F );
            signs.removeSign( sign ); 
            
        } else {
            player.sendMessage( destroyMsgFailure );
            player.playSound( player.getLocation(), destroySoundFailure, 1F, 1F );

        }
        
    }
    
}