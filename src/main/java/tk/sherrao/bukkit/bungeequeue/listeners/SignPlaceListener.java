package tk.sherrao.bukkit.bungeequeue.listeners;

import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;

import tk.sherrao.bukkit.bungeequeue.BungeeQueue;
import tk.sherrao.bukkit.bungeequeue.tasks.SignUpdater;
import tk.sherrao.bukkit.utils.plugin.SherEventListener;

public class SignPlaceListener extends SherEventListener {
    
    protected String prefix;
    protected SignUpdater signs;
    
    protected String placeMsgSucess, placeMsgFailure;
    protected Sound placeSoundSuccess, placeSoundFailure;
    
    public SignPlaceListener( BungeeQueue pl ) {
        super(pl);
        
        this.prefix = config.getString( "sign.prefix" );
        this.signs = pl.getSignUpdater();
        
        this.placeMsgSucess = config.getString( "messages.queue.sign-place" );
        this.placeMsgFailure = config.getString( "messages.command.no-perms" );
        this.placeSoundSuccess = config.getSound( "sounds.sign-place-success" );
        this.placeSoundFailure = config.getSound( "sounds.sign-place-failure" );

    }
    
    @EventHandler( priority = EventPriority.HIGHEST )
    public void onSignPlace( SignChangeEvent event ) {
        if( !event.getLine(0).equals( prefix ) ) 
            return;
        
        Player player = event.getPlayer();
        if( player.hasPermission( "bungeequeue.sign.create" ) ) {
            signs.addSign( (Sign) event.getBlock().getState() ); 
            player.sendMessage( placeMsgSucess );
            player.playSound( player.getLocation(), placeSoundSuccess, 1F, 1F );
            
        
        } else {
            player.sendMessage( placeMsgFailure );
            player.playSound( player.getLocation(), placeSoundFailure, 1F, 1F );
            event.setCancelled( true );
            
        }
        
    }
    
}
