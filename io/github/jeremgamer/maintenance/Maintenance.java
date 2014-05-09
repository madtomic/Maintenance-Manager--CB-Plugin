package io.github.jeremgamer.maintenance;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

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
    String durationArgument;
    private Thread t;
    private Thread t2;
    private Thread t3;
    String pluginName;
    Plugin plugin;
    int maxPlayer;
    String scheduleMessageBegin;
    String scheduleMessageEnd;
    boolean durationEnabled = false;
    int remainingMilliseconds;
    boolean scheduleEnabled = false;
    List<String> disabledPlugins;
    Plugin pluginToDisable;


    
    @Override
    public void onEnable() {
    	getServer().getPluginManager().registerEvents(this, this);
    	File configFile = new File(this.getDataFolder(), "config.yml");
    	if (!configFile.exists()) { 
        this.saveDefaultConfig();
    	}
    	getConfig();
    	maintenanceTime = getConfig().getBoolean("maintenanceModeOnStart");
    	remainingMilliseconds = getConfig().getInt("remainingMilliseconds");
    	if (remainingMilliseconds != 0) {
    		durationEnabled = true;
			t2 = new Thread(new MaintenanceDuration(this, null));
    		t2.start();
    	}
    	
    	disabledPlugins = getConfig().getStringList("disabledPlugins");
    	if ( !disabledPlugins.isEmpty() ) {
			t3 = new Thread(new disablingPluginsOnStart(this));
    		t3.start();
    	}
    }
 

	@Override
    public void onDisable() {
    	if (durationEnabled == true) {
			t2.interrupt();
    	}
    	getConfig();
    	this.saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

    	if (cmd.getName().equalsIgnoreCase( "maintenance")) { 
    		if (args[0].equalsIgnoreCase( "on" ) && sender.hasPermission( "maintenance.maintenance")) {
    			if ( maintenanceTime == false ) {
    				
    				if ( args.length == 1 ) {
            			maintenanceTime = true;
            			this.getConfig().set("maintenanceModeOnStart", true);
            			saveConfig();
            			Bukkit.getServer().broadcastMessage( getConfig().getString("maintenanceStart") );
    				}
    				
    				if (args.length == 2) {
    					scheduleArgument = args[1];
    					scheduleEnabled = true;
    					t = new Thread(new MaintenanceSchedule(this, sender));
    					t.start();
    					return true;
        	        	} 
    				if (args.length == 3) {
    					scheduleArgument = args[1];
    					scheduleEnabled = true;
    					t = new Thread(new MaintenanceSchedule(this, sender));
    					t.start(); 
    					try {
    						remainingMilliseconds = Integer.parseInt(args[2]) * 60 * 1000;
    					} catch (NumberFormatException e){
    						sender.sendMessage( getConfig().getString("inputErrorDuration") );
    					}
    					durationEnabled = true;
    					maintenanceTime = true;
    					this.getConfig().set("maintenanceModeOnStart", true);
    					saveConfig();
    					Bukkit.getServer().broadcastMessage( getConfig().getString("maintenanceStart") );
    					t2 = new Thread(new MaintenanceDuration(this, sender));
    					t2.start();
    					return true;
    				}
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
    			    	if (durationEnabled == true) {
    						t2.interrupt();
    						durationEnabled = false;
        					maintenanceTime = false;
        					this.getConfig().set("maintenanceModeOnStart", false);
        					saveConfig();
    			    	} else {
    					maintenanceTime = false;
    					this.getConfig().set("maintenanceModeOnStart", false);
    					saveConfig();
    					Bukkit.getServer().broadcastMessage( getConfig().getString("maintenanceEnd") );
    			    	}
    				} else {
    					sender.sendMessage( getConfig().getString("noMaintenanceLaunched") );
    				}
    				return true;
    			} else if (args[0].equalsIgnoreCase( "reload" ) && sender.hasPermission( "maintenance.reload")) {
    				this.reloadConfig();
    				sender.sendMessage( "MaintenanceManager config reloaded!" );
    				return true;
    			} else if (args[0].equalsIgnoreCase( "disable" ) && sender.hasPermission( "maintenance.manage.plugins")) {
    				if (args.length == 2) {
    					pluginName = args[1];
    					plugin = pm.getPlugin(pluginName);
    					pm.disablePlugin(plugin);
    					disabledPlugins.add(pluginName);
    					getConfig().set("disabledPlugins", disabledPlugins);
            			saveConfig();
    					pluginName = pluginName + " ";
    					sender.sendMessage( ChatColor.GOLD + "§l" + pluginName + ChatColor.RESET + getConfig().getString("pluginDisabled") );
    				} else {
    					sender.sendMessage( getConfig().getString("pluginManagementArgumentErrorDisable") );
    				}
    				return true;
    			} else if (args[0].equalsIgnoreCase( "enable" ) && sender.hasPermission( "maintenance.manage.plugins")) {
    				if (args.length == 2) {
    					pluginName = args[1];
    					plugin = pm.getPlugin(pluginName);
    					pm.enablePlugin(plugin);
    					disabledPlugins.remove(pluginName);
    					getConfig().set("disabledPlugins", disabledPlugins);
            			saveConfig();
    					pluginName = pluginName + " ";
    					sender.sendMessage( ChatColor.GOLD +  "§l" + pluginName + ChatColor.RESET + getConfig().getString("pluginEnabled") );
    				} else {
    					sender.sendMessage( getConfig().getString("pluginManagementArgumentErrorDisable") );
    				}
    				return true;
    			} else if (args[0].equalsIgnoreCase( "cancel" ) && sender.hasPermission( "mainteance.maintenance.cancel" )) {
    				if (scheduleEnabled == true) {
    					t.interrupt();
    					scheduleEnabled = false;
    					Bukkit.getServer().broadcastMessage( getConfig().getString("scheduleCanceled") );
    				} else {
    					sender.sendMessage( getConfig().getString("noMaintenanceScheduled") );
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
        		if (schedule != 0 || schedule > 0) {
        			Bukkit.getServer().broadcastMessage( scheduleMessageBegin + schedule + scheduleMessageEnd);
        			scheduleM = Integer.parseInt(scheduleArgument) * 60 * 1000;
        			try {
        				t.sleep(scheduleM / 2);
            			if (schedule / 2 == 0) {
            				Bukkit.getServer().broadcastMessage( getConfig().getString("scheduleLessThanOneMinute") );
            			} else {
            				Bukkit.getServer().broadcastMessage( scheduleMessageBegin + schedule / 2 + scheduleMessageEnd);
            			}
        			} catch (InterruptedException e) {
        				t.interrupt();
        			}
        			try {
        				t.sleep(scheduleM / 2);
            			scheduleEnabled = false;
            			maintenanceTime = true;
            			getConfig().set("maintenanceModeOnStart", true);
            			saveConfig();
            			Bukkit.getServer().broadcastMessage( getConfig().getString("maintenanceStart") );
        			} catch (InterruptedException e) {
        				t.interrupt();
        			}
        	}
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
    		if (durationEnabled == true ) {
    			String motdDurationEnd = " " + getConfig().getString("maintenanceWithDurationMOTDEnd");
    			try {
    				if (remainingMilliseconds / 60 / 1000 < 1) {
        				event.setMotd( getConfig().getString("maintenanceWithDurationMOTDBegin") + "\n" + getConfig().getString("maintenanceWithDurationMOTDLessThanOneMinute") );
    				} else {
    					event.setMotd( getConfig().getString("maintenanceWithDurationMOTDBegin") + "\n" + remainingMilliseconds / 60 / 1000 + motdDurationEnd );
    				}
    			} catch (NumberFormatException e){
    				getLogger().info(getConfig().getString("inputErrorDuration"));
    			}
    		} else {
        		event.setMotd( getConfig().getString("maintenanceMOTD") );
    		}
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
        		for ( int i = remainingMilliseconds ; i >= 0 ; i--) {
        			remainingMilliseconds--;
        			getConfig().set( "remainingMilliseconds" , i);
                	try {
        				t2.sleep(1);
                		if ( getConfig().getInt("remainingMilliseconds") == 0 && i == 0 && durationEnabled == true && maintenanceTime == true ) {
                   			durationEnabled = false;
                			getConfig().set("maintenanceModeOnStart", false);
                			maintenanceTime = false;
                			remainingMilliseconds = 0;
                        	Bukkit.getServer().broadcastMessage( getConfig().getString("maintenanceEnd") );
                			getConfig().set( "remainingMilliseconds" , remainingMilliseconds);
                			saveConfig();
                    		}
        			} catch (InterruptedException e) {
        				t2.interrupt();
        			}
        		}
        	} catch (NumberFormatException e){
    			sender.sendMessage( getConfig().getString("inputErrorDuration") );
        	}
        }
    }
    
    public class disablingPluginsOnStart extends BukkitRunnable {

		@SuppressWarnings("unused")
		private final Plugin plugin;
    	
        public disablingPluginsOnStart(JavaPlugin plugin) {
            this.plugin = plugin;
        }
    	
		@SuppressWarnings("static-access")
		@Override
		public void run() {
			try {
				t3.sleep(2000);
				getLogger().info("Disabling the selected plugins...");
		    	for ( String name : disabledPlugins ) {
					pluginToDisable = pm.getPlugin(name);
					pm.disablePlugin(pluginToDisable);
		    	}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (NullPointerException e1) {
				getLogger().severe( "No plugin named " + pluginToDisable + " to disable!" );
			}
		}
    	
    }
}
