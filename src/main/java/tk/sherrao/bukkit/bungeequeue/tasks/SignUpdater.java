package tk.sherrao.bukkit.bungeequeue.tasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.block.Sign;

import tk.sherrao.bukkit.bungeequeue.BungeeQueue;
import tk.sherrao.bukkit.utils.plugin.SherTask;

public class SignUpdater extends SherTask {
    
    private final class Lock {}
    private final Object lock = new Lock();
    
    protected PlayerQueue queue;
    
    protected List<Sign> signs;
    protected List<String> lines, buffer;
    protected Map<String, Integer> priorityTally;
    
    public SignUpdater( BungeeQueue pl ) {
        super(pl);
        
        this.queue = pl.getPlayerQueue();   
        
        this.signs = Collections.synchronizedList( new ArrayList<>() );
        this.lines = Collections.synchronizedList( config.getStringList( "sign.change-to" ) );
        this.buffer = Collections.synchronizedList( new ArrayList<>(4) );
        this.priorityTally = queue.getPriorityTally();
        
    }
    
    private String format( String str ) {
        priorityTally = queue.getPriorityTally();
        for( Entry<String, Integer> entry : priorityTally.entrySet() ) {
            str = str.replace( "[" + entry.getKey().split( "\\." )[2].substring( 0, 3 ) + "]", 
                    String.valueOf( entry.getValue() ) );
        }
    
        return str;
    }
    
    @Override
    public void fire() {
        synchronized( lock ) {
            buffer.clear();
            buffer.add( 0, format( lines.get(0) ) );
            buffer.add( 1, format( lines.get(1) ) );
            buffer.add( 2, format( lines.get(2) ) );
            buffer.add( 3, format( lines.get(3) ) );
            for( Sign sign : signs ) {
                sign.setLine( 0, buffer.get(0) );
                sign.setLine( 1, buffer.get(1) );
                sign.setLine( 2, buffer.get(2) );
                sign.setLine( 3, buffer.get(3) );
                sign.update();
         
            }
        }
    }

    public final void addSign( Sign sign ) {
        synchronized( lock ) { signs.add( sign ); }
        
    }
    
    public final void removeSign( Sign sign ) {
        synchronized( lock ) { signs.remove( sign ); }

    }
    
    public List<String> getLineBuffer() { return buffer; }
    
}