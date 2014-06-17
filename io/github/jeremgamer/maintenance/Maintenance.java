package io.github.jeremgamer.maintenance;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.imageio.ImageIO;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
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
    private Thread t4;
    private Thread t5;
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
    SmokeDetector sd = new SmokeDetector(this);
    static long sleep;

   
    @SuppressWarnings("static-access")
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
		sleep = getConfig().getLong("waitTime");
    	disabledPlugins = getConfig().getStringList("disabledPlugins");
    	if ( !disabledPlugins.isEmpty() ) {
			t3 = new Thread(new disablingPluginsOnStart(this));
    		t3.start();
    	}
    	
    	try {
			sd.loadLibraries();
		} catch (IOException e) {
			e.printStackTrace();
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
	
    public void backupProcess(CommandSender sender)  {

			Calendar cal = Calendar.getInstance();
			DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd - HH mm ss");
			String zipFile = new File("").getAbsolutePath() + "/backups/" + dateFormat.format(cal.getTime()) + ".zip";
			File zip = new File(zipFile);
    		String srcDir = new File("").getAbsolutePath();
			
    		try {

				try {
					zip.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

       			File dir = new File(srcDir);
       			
    			List<File> filesList = (List<File>) FileUtils.listFilesAndDirs(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    			File[] files = filesList.toArray(new File[filesList.size()]);
				

				OutputStream out = new FileOutputStream(zip);
				ArchiveOutputStream zipOutput = new ZipArchiveOutputStream(out);
				String filePath;
				
    			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					
				} else {					
				
					filePath = files[i].getAbsolutePath().substring(srcDir.length() + 1);

					try {	
						ZipArchiveEntry entry = new ZipArchiveEntry(filePath);
						entry.setSize(new File(files[i].getAbsolutePath()).length());
						zipOutput.putArchiveEntry(entry);
						IOUtils.copy(new FileInputStream(files[i].getAbsolutePath()), zipOutput); 
						zipOutput.closeArchiveEntry();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}				
    			}
					zipOutput.finish();  
					zipOutput.close();  
					out.close(); 
    		} catch (IOException ioe) {
    			ioe.printStackTrace();
    		} catch (IllegalArgumentException iae) {    			
    			iae.printStackTrace();
    		}
				
    			sender.sendMessage("Backup success!");
    			getLogger().info("Backup success!");
	            getServer().dispatchCommand(getServer().getConsoleSender(), "save-on");
		}
    	

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

    	if (cmd.getName().equalsIgnoreCase( "maintenance" ) || cmd.getName().equalsIgnoreCase( "mmode" ) || cmd.getName().equalsIgnoreCase( "maint" )) {
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
    					durationEnabled = true;
    					t2 = new Thread(new MaintenanceDuration(this, sender));
    					t.start();
    					try {
    						remainingMilliseconds = Integer.parseInt(args[2]) * 60 * 1000;
    					} catch (NumberFormatException e){
    						sender.sendMessage( getConfig().getString("inputErrorDuration") );
    					}
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
    				sleep = getConfig().getLong("waitTime");
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
    			} else if (args[0].equalsIgnoreCase( "backup" ) && sender.hasPermission( "maintenance.maintenance.backup" )) {
    			
    				String folderPath = new File("").getAbsolutePath() + "/backups/";
    				File folder = new File(folderPath);
    				if (!folder.exists()) {
    					if (folder.mkdir()) {
    						getLogger().info("Backup folder created!");
    					} else {
    						getLogger().info("Failed to create backup folder...");
    					}
    				}
  	    		
    	            sender.sendMessage("Backing up. The server will lag brievely.");
    	            getLogger().info("Backing up. The server will lag brievely.");
    	            
    	            
	    	            if (getServer().isPrimaryThread()) {

	    	                getServer().dispatchCommand(getServer().getConsoleSender(), "save-all");
	    	                getServer().dispatchCommand(getServer().getConsoleSender(), "save-off");
				    	    
	    	                backupProcess(sender);

		    	            return true;
	    	            }
    	            
	    	            getServer().dispatchCommand(getServer().getConsoleSender(), "save-on");
    	           	          
    	    		return true;
    			}
    		return false;
    		}
    	
    	if (cmd.getName().equalsIgnoreCase( "cpu" ) && sender.hasPermission("maintenance.cpu")) { 
			t4 = new Thread(new cpuCommand(this, sender));
			t4.start();
    		return true;
    	}
    	
    	if (cmd.getName().equalsIgnoreCase( "ram" ) && sender.hasPermission("maintenance.ram")) { 
    		t5 = new Thread(new ramCommand(this , sender));
    		t5.start();
    		return true;
    	}
			return false;
    }
    
    
   
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin (final PlayerLoginEvent event) {
    	Player player = event.getPlayer();
    	if (maintenanceTime == true && !player.hasPermission("maintenance.acess")) {    		
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
                		for (Player player: Bukkit.getServer().getOnlinePlayers()) {
                			if ( !player.hasPermission( "maintenance.access" ) || !player.isOp() ) {
                				player.kickPlayer( getConfig().getString("kickMessage") );;
                			}
                		}
    					t2.start();
    					durationEnabled = true;
    					maintenanceTime = true;
    					getConfig().set("maintenanceModeOnStart", true);
    					saveConfig();
        			} catch (InterruptedException e) {
        				t.interrupt();
        			}        			
        	} else if (schedule <= 0) {
    			scheduleEnabled = false;
    			maintenanceTime = true;
    			getConfig().set("maintenanceModeOnStart", true);
    			saveConfig();
    			Bukkit.getServer().broadcastMessage( getConfig().getString("maintenanceStart") );
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
    
    public class cpuCommand extends BukkitRunnable {

		private final Plugin plugin;
        private final CommandSender sender;
    	
        public cpuCommand(JavaPlugin plugin , CommandSender sender) {
            this.plugin = plugin;
            this.sender = sender;
        }
    	
		@SuppressWarnings("static-access")
		@Override
		public void run() {
    		try {
				sd.sendRequest("cpu");
    			File smokeAnswer = new File( plugin.getDataFolder() + "/smokeAnswer.yml" );
				while (!smokeAnswer.exists()) {
					t4.sleep(1);
				}
				if (smokeAnswer.exists()) {
					t4.sleep(sleep);
					String answer = " " + sd.getInfo();
		    		sender.sendMessage( getConfig().getString("cpuUsage") + answer + "%");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    	
    }
    
    public class ramCommand extends BukkitRunnable {

		private final Plugin plugin;
        private final CommandSender sender;
    	
        public ramCommand(JavaPlugin plugin , CommandSender sender) {
            this.plugin = plugin;
            this.sender = sender;
        }
    	
		@SuppressWarnings("static-access")
		@Override
		public void run() {
    		try {
    			File smokeAnswer = new File( plugin.getDataFolder() + "/smokeAnswer.yml" );
				sd.sendRequest("ram");
				while (!smokeAnswer.exists()) {
					t5.sleep(1);
				}
				if (smokeAnswer.exists()) {
					t5.sleep(sleep);
					String answer = " " + sd.getInfo();
		    		sender.sendMessage( getConfig().getString("ramUsage") + answer );
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			
		}
    	
    }
    
}
