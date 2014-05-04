package io.github.jeremgamer.maintenance;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Maintenance extends JavaPlugin implements Listener {
	
    boolean maintenanceTime = false;
    int schedule;
    long scheduleM;
    String scheduleArgument;
    long durationM;
    String durationArgument;
    private Thread t;
    private Thread t2;

	
    
    @Override
    public void onEnable() {
    	getServer().getPluginManager().registerEvents(this, this);
    }
 
    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

    	if (cmd.getName().equalsIgnoreCase( "maintenance" )) { 
    		if (args[0].equalsIgnoreCase( "on" )) {
        		if (args.length == 2) {
        			scheduleArgument = args[1];
        			t = new Thread(new MaintenanceSchedule(this));
        			t.start();
        			durationArgument = "0";
        			t2 = new Thread(new MaintenanceDuration(this));
        			t2.start();
        	        return true;
        	        } 
        		if (args.length == 3) {
        			scheduleArgument = args[1];
        			t = new Thread(new MaintenanceSchedule(this));
        			t.start();
        			durationArgument = args[2];
        			t2 = new Thread(new MaintenanceDuration(this));
        			t2.start();
        			return true;
        		}
    			maintenanceTime = true;
    			Bukkit.getServer().broadcastMessage( "Maintenance time!" );
    			for (Player player: Bukkit.getServer().getOnlinePlayers()) {
    				if ( !player.hasPermission( "maintenance.access" ) || !player.isOp() ) {
    					player.kickPlayer( "The server is currently under maintenance. Come back later." );
    				}
    			}
    			return true;
    		} else if (args[0].equalsIgnoreCase( "off" )) {
    			maintenanceTime = false;
    			Bukkit.getServer().broadcastMessage( "Maintenance finished!" );
    			return true;
    		}
    	}
		return false;
    }
   
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin (final PlayerLoginEvent event) {
    	Player player = event.getPlayer();
    	if (maintenanceTime == true && !player.isOp()) {    		
    			event.disallow( org.bukkit.event.player.PlayerLoginEvent.Result.KICK_OTHER, "The server is currently under maintenance. Come back later." );
    			return;
    	} else if (maintenanceTime == true && player.hasPermission( "maintenance.acess" )) {
    			event.allow();
    	}
    }
    
    public class MaintenanceSchedule extends BukkitRunnable {
    	
        @SuppressWarnings("unused")
		private final JavaPlugin plugin;
        
        public MaintenanceSchedule(JavaPlugin plugin) {
            this.plugin = plugin;
        }
     
        @SuppressWarnings("static-access")
		@Override
        public void run() {
    		schedule = Integer.parseInt(scheduleArgument);
    		Bukkit.getServer().broadcastMessage( "Maintenance time in " + schedule + " minutes!");
    		scheduleM = Integer.parseInt(scheduleArgument) * 60 * 1000;
    		try {
				t.sleep(scheduleM / 2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		Bukkit.getServer().broadcastMessage( "Maintenance time in " + schedule / 2 + " minutes!");
    		try {
				t.sleep(scheduleM / 2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		Bukkit.getServer().broadcastMessage( "Maintenance time!" );
    		for (Player player: Bukkit.getServer().getOnlinePlayers()) {
    			if ( !player.hasPermission( "maintenance.access" ) || !player.isOp() ) {
    				player.kickPlayer( "The server is currently under maintenance. Come back later." );;
    			}
    		}
     
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void changeMOTD (final ServerListPingEvent event){
    	if ( maintenanceTime == true ) {
    		event.setMotd( "Maintenance mode..." );
    	}
    }
    
    public class MaintenanceDuration extends BukkitRunnable {
    	
        @SuppressWarnings("unused")
		private final JavaPlugin plugin;
        
        public MaintenanceDuration(JavaPlugin plugin) {
            this.plugin = plugin;
        }
     
        @SuppressWarnings("static-access")
		@Override
        public void run() {
        	durationM = Integer.parseInt(durationArgument) * 60 * 1000;
        	try {
				t2.sleep(durationM);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			maintenanceTime = false;
			Bukkit.getServer().broadcastMessage( "Maintenance finished!" );
        }
    }
}
