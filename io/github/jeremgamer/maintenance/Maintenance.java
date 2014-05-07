package io.github.jeremgamer.maintenance;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.CachedServerIcon;

public final class Maintenance extends JavaPlugin implements Listener {
	
	PluginManager pm = Bukkit.getPluginManager();
	
    boolean maintenanceTime = false;
    int schedule;
    long scheduleM;
    String scheduleArgument;
    long durationM;
    String durationArgument;
    private Thread t;
    private Thread t2;
    String pluginName;
    Plugin plugin;
    int maxPlayer;
    String scheduleMessageBegin;
    String scheduleMessageEnd;
    
    
    @Override
    public void onEnable() {
    	getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();
    	maintenanceTime = getConfig().getBoolean("maintenanceModeOnStart");
    }
 
    @Override
    public void onDisable() {
    	this.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

    	if (cmd.getName().equalsIgnoreCase( "maintenance")) { 
    		if (args[0].equalsIgnoreCase( "on" ) && sender.hasPermission( "maintenance.maintenance")) {
    			if ( maintenanceTime == false ) {
        		if (args.length == 2) {
        			scheduleArgument = args[1];
        			t = new Thread(new MaintenanceSchedule(this, sender));
        			t.start();
        			this.getConfig().set("maintenanceModeOnStart", true);
        	        return true;
        	        } 
        		if (args.length == 3) {
        			scheduleArgument = args[1];
        			t = new Thread(new MaintenanceSchedule(this, sender));
        			t.start();
        			durationArgument = args[2];
        			t2 = new Thread(new MaintenanceDuration(this, sender));
        			t2.start();
        			this.getConfig().set("maintenanceModeOnStart", false);
        			return true;
        		}
    			maintenanceTime = true;
    			this.getConfig().set("maintenanceModeOnStart", true);
    			Bukkit.getServer().broadcastMessage( getConfig().getString("maintenanceStart") );
    			for (Player player: Bukkit.getServer().getOnlinePlayers()) {
    				if ( !player.hasPermission( "maintenance.access" ) || !player.isOp() ) {
    					player.kickPlayer( getConfig().getString("kickMessage") );
    				}
    			}
    		} else {
    			sender.sendMessage( getConfig().getString("maintenanceAlreadyLaunched") );
    		}
    			return true;
    		} else if (args[0].equalsIgnoreCase( "off" ) && sender.hasPermission( "maintenance.maintenance")) {
    			if ( maintenanceTime == true ) {
    			maintenanceTime = false;
    			this.getConfig().set("maintenanceModeOnStart", false);
    			Bukkit.getServer().broadcastMessage( getConfig().getString("maintenanceEnd") );
    			} else {
        			sender.sendMessage( getConfig().getString("noMaintenanceLaunched") );
    			}
    			return true;
    		} else if (args[0].equalsIgnoreCase( "reload" ) && sender.hasPermission( "maintenance.reload")) {
    			this.reloadConfig();
    			sender.sendMessage( "MaintenanceManager config reloaded!" );
    			return true;
    		} else if (args[0].equalsIgnoreCase( "disable" )) {
        		if (args.length == 2) {
        			pluginName = args[1];
        			plugin = pm.getPlugin(pluginName);
        			pm.disablePlugin(plugin);
        			pluginName = pluginName + " ";
        			sender.sendMessage( ChatColor.GOLD + "§l" + pluginName + ChatColor.RESET + getConfig().getString("pluginDisabled") );
        		} else {
        			sender.sendMessage( getConfig().getString("pluginManagementArgumentErrorDisable") );
        		}
    			return true;
    		} else if (args[0].equalsIgnoreCase( "enable" )) {
        		if (args.length == 2) {
        			pluginName = args[1];
        			plugin = pm.getPlugin(pluginName);
        			pm.enablePlugin(plugin);
        			pluginName = pluginName + " ";
        			sender.sendMessage( ChatColor.GOLD +  "§l" + pluginName + ChatColor.RESET + getConfig().getString("pluginEnabled") );
        		} else {
        			sender.sendMessage( getConfig().getString("pluginManagementArgumentErrorDisable") );
        		}
    			return true;
    		}
    	}
    	
		return false;
    }
   
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin (final PlayerLoginEvent event) {
    	Player player = event.getPlayer();
    	if (maintenanceTime == true && !player.isOp()) {    		
    			event.disallow( org.bukkit.event.player.PlayerLoginEvent.Result.KICK_OTHER, getConfig().getString("maintenanceMessage") );
    			return;
    	} else if (maintenanceTime == true && player.hasPermission( "maintenance.acess" )) {
    			event.allow();
    	}
    }
    
    public class MaintenanceSchedule extends BukkitRunnable {
    	
        @SuppressWarnings("unused")
		private final JavaPlugin plugin;
        private final CommandSender sender;
        
        public MaintenanceSchedule(JavaPlugin plugin, CommandSender sender) {
            this.plugin = plugin;
            this.sender = sender;
        }
     
        @SuppressWarnings("static-access")
		@Override
        public void run() {
        	scheduleMessageBegin = getConfig().getString("scheduleMessageBegin") + " ";
        	scheduleMessageEnd =  " " + getConfig().getString("scheduleMessageEnd");
        	try{
        		schedule = Integer.parseInt(scheduleArgument);
        		Bukkit.getServer().broadcastMessage( scheduleMessageBegin + schedule + scheduleMessageEnd);
        		scheduleM = Integer.parseInt(scheduleArgument) * 60 * 1000;
    		try {
				t.sleep(scheduleM / 2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		if (schedule / 2 == 0) {
    			Bukkit.getServer().broadcastMessage("scheduleLessThanOneMinute");
    		} else {
    			Bukkit.getServer().broadcastMessage( scheduleMessageBegin + schedule / 2 + scheduleMessageEnd);
    		}
    		try {
				t.sleep(scheduleM / 2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		Bukkit.getServer().broadcastMessage( getConfig().getString("maintenanceStart") );
    		for (Player player: Bukkit.getServer().getOnlinePlayers()) {
    			if ( !player.hasPermission( "maintenance.access" ) || !player.isOp() ) {
    				player.kickPlayer( getConfig().getString("kickMessage") );;
    			}
    		}
    		} catch (NumberFormatException e){
    			sender.sendMessage( getConfig().getString("inputErrorSchedule") );
    		}
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void MaintenanceListPing (final ServerListPingEvent event) {
    	if ( maintenanceTime == true ) {
    		event.setMotd( getConfig().getString("maintenanceMOTD") );
			try {
				BufferedImage maintenanceImage = ImageIO.read(new URL( getConfig().getString("maintenanceIcon") ));
				CachedServerIcon maintenanceIcon = Bukkit.getServer().loadServerIcon(maintenanceImage);
	    		event.setServerIcon(maintenanceIcon); 
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			getConfig().getString("maxPlayerDuringMaintenance");
			try{
				maxPlayer = Integer.parseInt( getConfig().getString("maxPlayersOnMaintenance") );
				event.setMaxPlayers( maxPlayer );
			} catch (NumberFormatException e){
				e.printStackTrace();
			}
    	}   	
    }
    
    public class MaintenanceDuration extends BukkitRunnable {
    	
        @SuppressWarnings("unused")
		private final JavaPlugin plugin;
        private final CommandSender sender;
        
        public MaintenanceDuration(JavaPlugin plugin, CommandSender sender) {
            this.plugin = plugin;
            this.sender = sender;
        }
     
        @SuppressWarnings("static-access")
		@Override
        public void run() {
        	try{
        		durationM = Integer.parseInt(durationArgument) * 60 * 1000;
        	try {
				t2.sleep(durationM);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			maintenanceTime = false;
			Bukkit.getServer().broadcastMessage( getConfig().getString("maintenanceEnd") );
        	} catch (NumberFormatException e){
    			sender.sendMessage( getConfig().getString("inputErrorDuration") );
        	}
        }
    }
}
