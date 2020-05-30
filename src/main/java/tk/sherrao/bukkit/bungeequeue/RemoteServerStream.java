package tk.sherrao.bukkit.bungeequeue;

import java.io.DataInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import tk.sherrao.bukkit.utils.plugin.SherPlugin;
import tk.sherrao.bukkit.utils.plugin.SherPluginFeature;

public class RemoteServerStream extends SherPluginFeature {

	protected Socket socket;
	protected InetSocketAddress address;
		
	protected DataInputStream in;
	
	public RemoteServerStream( SherPlugin pl ) {
		super(pl);
		
		this.socket = new Socket();
		this.address = new InetSocketAddress( config.getString( "bungee.ip" ), config.getInt( "bungee.port" ) );
	
	}
	
	
	
}
