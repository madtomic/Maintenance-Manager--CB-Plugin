/*
 The MIT License (MIT)

Copyright (c) 2014 JeremGamer

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.*/

package io.github.jeremgamer.maintenance;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.CachedServerIcon;
import org.hyperic.sigar.SigarException;

public final class Maintenance extends JavaPlugin implements Listener {
	
	PluginManager pm = Bukkit.getPluginManager();
	
    static boolean maintenanceTime = false;
    int schedule;
    long scheduleM;
    String scheduleArgument;
    String durationArgument;
    private Thread t;
    private Thread t2;
    private Thread t3;
    private Thread t4;
    String pluginName;
    Plugin plugin;
    int maxPlayer;
    String scheduleMessageBegin;
    String scheduleMessageEnd;
    static boolean durationEnabled = false;
    static long remainingMilliseconds;
    static boolean scheduleEnabled = false;
    List<String> disabledPlugins;
    Plugin pluginToDisable;
    SmokeDetector sd = new SmokeDetector(this);
    File icon;
    CachedServerIcon maintenanceIcon;
    boolean isUpToDate = true;
    private final String downloadAdress = "http://goo.gl/NE9nUz";
	String version = null;
	boolean backingUp = false;
	public SimpleConfigManager manager;
	public SimpleConfig config;
    
    URL u;
    InputStream is = null;
    DataInputStream dis;
    String s;
    
    public static boolean isOn(){
    	return maintenanceTime;
    }
    
    public static boolean isDurationPlanned() {
    	return durationEnabled;
    }
    
    public static boolean isMaintenanceScheduled() {
    	return scheduleEnabled;
    }
    
    public static long getRemainingMilliseconds() {
    	return remainingMilliseconds;
    }    
    
    public Plugin getPluginIgnoreCase(final String pluginName) {
        for(final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if(plugin.getName().equalsIgnoreCase(pluginName)) {
                return plugin;
            }
        }
        return null;
    }
    
    @SuppressWarnings("deprecation")
	public void checkUpdate () {
    	try {

    		u = new URL("https://github.com/JeremGamer/Maintenance-Manager--CB-Plugin/blob/master/RELEASE_MANIFEST.version"); 
    		is = u.openStream();
    		dis = new DataInputStream(new BufferedInputStream(is));

				while ((s = dis.readLine()) != null) {
					if (s.contains("1.")) {
						version = s.substring(0 , s.indexOf("1.") + 4);
						version = version.substring(version.indexOf("1.") , version.indexOf("1.") + 4);
						if (!version.equals(this.getDescription().getVersion())) {					
							getLogger().info("MaintenanceManager isn't up to date! Get the " + version + " version here: " + downloadAdress);
							isUpToDate = false;
						}
							break;
						}	
					}	
		} catch (IOException e) {
			getLogger().severe(e.getMessage());
		}
    }
    
    
    @SuppressWarnings("static-access")
	@Override
    public void onEnable() {
    	getServer().getPluginManager().registerEvents(this, this);
    	File configFile = new File(this.getDataFolder(), "config.yml");
    	if (!configFile.exists()) {
        this.saveDefaultConfig();
    	}
    	getConfig().options().header("---------- MAINTENANCEMANAGER CONFIGURATION ----------#").copyHeader(true);
    	maintenanceTime = getConfig().getBoolean("maintenanceModeOnStart");
    	remainingMilliseconds = getConfig().getLong("remainingMilliseconds");
    	if (remainingMilliseconds != 0) {
    		durationEnabled = true;
			t2 = new Thread(new MaintenanceDuration(null));
    		t2.start();
    	}
    	disabledPlugins = getConfig().getStringList("disabledPlugins");
    	if ( !disabledPlugins.isEmpty() ) {
			t3 = new Thread(new disablingPluginsOnStart());
    		t3.start();
    	}
    	
    	try {
			sd.loadLibraries();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
		File srcIcon = new File(new File("").getAbsolutePath() + "/maintenance-icon.png");
		if (!srcIcon.exists()) {
			try (InputStream input = Maintenance.class.getResourceAsStream("maintenance-icon.png");
					OutputStream output = new FileOutputStream(srcIcon)) {
						byte[] buf = new byte[8192];
						int len;
						while ( (len=input.read(buf)) > 0 ) {
							output.write(buf, 0, len);
						}
						output.flush();
						input.close();
						output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		icon = new File( new File("").getAbsolutePath() + "/maintenance-icon.png" );
		BufferedImage maintenanceImage;
		try {
			maintenanceImage = ImageIO.read(icon);
			maintenanceIcon = Bukkit.getServer().loadServerIcon(maintenanceImage);
		} catch (Exception e) {
			getLogger().severe(e.getMessage());
		}
		
		checkUpdate();
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
					
				} else  if (files[i].getAbsolutePath().contains(new File("").getAbsolutePath() + "\\backups\\")){
					
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
				
    			sender.sendMessage( getConfig().getString("backupSuccess").replaceAll("&", "§") );
    			getLogger().info("Backup success!");
		}
    
    
    
    public void normalMaintenance(CommandSender sender) {
		if (!scheduleEnabled == true) {
			maintenanceTime = true;
			this.getConfig().set("maintenanceModeOnStart", true);	 
			saveConfig();
			Bukkit.getServer().broadcastMessage( getConfig().getString("maintenanceStart").replaceAll("&", "§") );
			} else {
				sender.sendMessage( getConfig().getString("maintenanceAlreadyScheduled").replaceAll("&", "§") );
			}
    }
    
    public void scheduledMaintenance(CommandSender sender , String scheduleTime) {
		if (!scheduleEnabled == true) {
		scheduleArgument = scheduleTime;
		scheduleEnabled = true;
		t = new Thread(new MaintenanceSchedule(sender));
		t.start();
		} else {
			sender.sendMessage( getConfig().getString("maintenanceAlreadyScheduled").replaceAll("&", "§") );
		}
    }
    
    public void maintenanceWithDuration(CommandSender sender , String scheduleTime , String duration) {
		if (!scheduleEnabled == true) {
			try {
				remainingMilliseconds = Long.parseLong(duration) * 60 * 1000;
				scheduleArgument = scheduleTime;
				scheduleEnabled = true;
				t = new Thread(new MaintenanceSchedule(sender));
				durationEnabled = true;

				t2 = new Thread(new MaintenanceDuration(sender));
				t.start();
				
			} catch (NumberFormatException e){
				sender.sendMessage( getConfig().getString("inputErrorDuration").replaceAll("&", "§") );
			}
		} else {
			sender.sendMessage( getConfig().getString("maintenanceAlreadyScheduled").replaceAll("&", "§") );
		}
    }
    	

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	try {
    	if (cmd.getName().equalsIgnoreCase( "maintenance" ) || cmd.getName().equalsIgnoreCase( "mmode" ) || cmd.getName().equalsIgnoreCase( "maint" )) {
    		if (args.length == 0) {
    			InputStream input = Maintenance.class.getResourceAsStream("help.yml");
    			InputStreamReader isr = new InputStreamReader(input);
    			String str = "";
    			try {
    				BufferedReader bw = new BufferedReader(isr);
					for (int i = 0; i < 14 ; i++) {
						str += bw.readLine() + "\n";
						str = str.replaceAll("&", "§");
					}
					sender.sendMessage(str);
					return true;
				} catch (FileNotFoundException e) {
					getLogger().warning(e.getMessage());
				} catch (IOException e) {
					getLogger().warning(e.getMessage());
				}
    		}    		
    		if (args[0].equalsIgnoreCase( "on" ) && sender.hasPermission( "maintenance.maintenance")) {
    			if ( maintenanceTime == false ) {
    				
    				if ( args.length == 1 ) {
    					normalMaintenance(sender);
    				}
    				
    				else if (args.length == 2) {
    					scheduledMaintenance(sender , args[1]);
    					return true;
        	        	} 
    				else if (args.length == 3) {
    					maintenanceWithDuration(sender , args[1] , args[2]);
    					return true;
    				}
    				for (Player player: Bukkit.getServer().getOnlinePlayers()) {
    					if ( !player.hasPermission( "maintenance.access" ) || !player.isOp() ) {
    						player.kickPlayer( getConfig().getString("kickMessage").replaceAll("&", "§").replaceAll("<n>", "\n") );
    					}
    				}
    			} else {
    				sender.sendMessage( getConfig().getString("maintenanceAlreadyLaunched").replaceAll("&", "§") );
    			}
    				return true;
    			} else if (args[0].equalsIgnoreCase( "off" ) && sender.hasPermission( "maintenance.maintenance")) {
    				if ( maintenanceTime == true ) {
    			    	if (durationEnabled == true) {
    						t2.interrupt();
    						durationEnabled = false;
        					maintenanceTime = false;
        					this.getConfig().set("maintenanceModeOnStart", false);
        					Bukkit.getServer().broadcastMessage( getConfig().getString("maintenanceEnd").replaceAll("&", "§") );                	 
        					saveConfig();
    			    	} else {
    					maintenanceTime = false;
    					this.getConfig().set("maintenanceModeOnStart", false);            	 
    					saveConfig();
    					Bukkit.getServer().broadcastMessage( getConfig().getString("maintenanceEnd").replaceAll("&", "§") );
    			    	}
    				} else {
    					sender.sendMessage( getConfig().getString("noMaintenanceLaunched").replaceAll("&", "§") );
    				}
    				return true;
    			} else if (args[0].equalsIgnoreCase( "reload" ) && sender.hasPermission( "maintenance.reload")) {
    				this.reloadConfig();
    				icon = new File( new File("").getAbsolutePath() + "/maintenance-icon.png" );
    				BufferedImage maintenanceImage;
    				try {
    					maintenanceImage = ImageIO.read(icon);
    					maintenanceIcon = Bukkit.getServer().loadServerIcon(maintenanceImage);
    				} catch (Exception e) {
    					getLogger().severe(e.getMessage());
    				}
    				checkUpdate();
    		    	if (isUpToDate == false && sender.isOp()) {
    		    		sender.sendMessage("§c§lYour MaintenanceManager is outdated! \n§6§oGet the latest version here: §e§n" + downloadAdress);
    		    	}
    				sender.sendMessage( "§2§oMaintenanceManager config reloaded!" );
    				return true;
    			} else if (args[0].equalsIgnoreCase( "disable" ) && sender.hasPermission( "maintenance.manage.plugins")) {
    				if (args.length == 2) {
    					pluginName = getPluginIgnoreCase(args[1]).getName();
    					pm.disablePlugin(getPluginIgnoreCase(args[1]));
    					disabledPlugins.add(pluginName);
    					getConfig().set("disabledPlugins", disabledPlugins);            	 
            			saveConfig();
    					sender.sendMessage( getConfig().getString("pluginDisabled").replaceAll("&", "§").replaceAll("<plugin>", pluginName) );
    				} else {
    					sender.sendMessage( getConfig().getString("pluginManagementArgumentErrorDisable").replaceAll("&", "§") );
    				}
    				return true;
    			} else if (args[0].equalsIgnoreCase( "enable" ) && sender.hasPermission( "maintenance.manage.plugins")) {
    				if (args.length == 2) {
    					pluginName = getPluginIgnoreCase(args[1]).getName();
    					pm.enablePlugin(getPluginIgnoreCase(args[1]));
    					disabledPlugins.remove(pluginName);
    					getConfig().set("disabledPlugins", disabledPlugins);            	 
            			saveConfig();
    					sender.sendMessage( getConfig().getString("pluginEnabled").replaceAll("&", "§").replaceAll("<plugin>", pluginName) );
    				} else {
    					sender.sendMessage( getConfig().getString("pluginManagementArgumentErrorEnable").replaceAll("&", "§") );
    				}
    				return true;
    			} else if (args[0].equalsIgnoreCase( "cancel" ) && sender.hasPermission( "mainteance.maintenance.cancel" )) {
    				if (scheduleEnabled == true) {
    					t.interrupt();
    					scheduleEnabled = false;
    					Bukkit.getServer().broadcastMessage( getConfig().getString("scheduleCanceled").replaceAll("&", "§") );
    				} else {
    					sender.sendMessage( getConfig().getString("noMaintenanceScheduled").replaceAll("&", "§") );
    				}
    				return true;
    			} else if (args[0].equalsIgnoreCase( "backup" ) && sender.hasPermission( "maintenance.backup" )) {
    			
					t4 = new Thread(new backup(sender));
					t4.start();    	           	          
    	    		return true;
    			}
    		return false;
    		}
    } catch (ArrayIndexOutOfBoundsException aioobe) {}
    	
    	if (cmd.getName().equalsIgnoreCase( "cpu" ) && sender.hasPermission("maintenance.cpu")) { 
    		try {
				sender.sendMessage( getConfig().getString("cpuUsage").replaceAll("&", "§").replaceAll("<cpu>", String.valueOf(SmokeDetector.getCpuUsage())));
			} catch (SigarException e) {
				getLogger().warning(e.getMessage());
			}
    		return true;
    	}
    	
    	if (cmd.getName().equalsIgnoreCase( "ram" ) && sender.hasPermission("maintenance.ram")) { 
			try {
				sender.sendMessage( getConfig().getString("ramUsage").replaceAll("&", "§").replaceAll("<ram%>", String.valueOf(SmokeDetector.getMemUsagePercent())).replaceAll("<ram>", String.valueOf(SmokeDetector.getMemUsage())));
			} catch (SigarException e) {
				getLogger().warning(e.getMessage());
			}
    		return true;
    	}
			return false;
    }
    
    
   
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin (final PlayerLoginEvent event) {
    	Player player = event.getPlayer();
    	if (maintenanceTime == true && !player.hasPermission("maintenance.acess")) {    		
    		event.disallow( org.bukkit.event.player.PlayerLoginEvent.Result.KICK_OTHER, getConfig().getString("maintenanceMessage").replaceAll("&", "§") );
    		return;
    	} else if (maintenanceTime == true && player.hasPermission( "maintenance.access" )) {
    		event.allow();
    	}
    	
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin (final PlayerJoinEvent event) {
    	Player player = event.getPlayer();
    	if (maintenanceTime == true) 
    		player.sendMessage( getConfig().getString("loginMessage").replaceAll("&", "§") );
    	
    	if (isUpToDate == false && player.isOp()) {
    		player.sendMessage("§c§lYour MaintenanceManager is outdated! \n§6§oGet the latest version here: §e§n" + downloadAdress);
    	}
    	
    }
    
    public class MaintenanceSchedule extends BukkitRunnable {
    	
        private final CommandSender sender;
        
        public MaintenanceSchedule(CommandSender sender) {
            this.sender = sender;
        }
     
        @SuppressWarnings("static-access")
		@Override
        public void run() {

        	try{
        		schedule = Integer.parseInt(scheduleArgument);
        		if (schedule != 0 || schedule > 0) {
        			Bukkit.getServer().broadcastMessage( getConfig().getString("scheduleMessage").replaceAll("&", "§").replaceAll("<minutes>", String.valueOf(schedule)) );
        			scheduleM = Integer.parseInt(scheduleArgument) * 60 * 1000;
        			try {
        				t.sleep(scheduleM / 2);
            			if (schedule / 2 == 0) {
            				Bukkit.getServer().broadcastMessage( getConfig().getString("scheduleLessThanOneMinute").replaceAll("&", "§") );
            			} else {
            				Bukkit.getServer().broadcastMessage( getConfig().getString("scheduleMessage").replaceAll("&", "§").replaceAll("<minutes>", String.valueOf(schedule / 2)) );
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
            			Bukkit.getServer().broadcastMessage( getConfig().getString("maintenanceStart").replaceAll("&", "§") );
                		for (Player player: Bukkit.getServer().getOnlinePlayers()) {
                			if ( !player.hasPermission( "maintenance.access" ) || !player.isOp() ) {
                				player.kickPlayer( getConfig().getString("kickMessage").replaceAll("&", "§") );;
                			}
                		}
                		if (remainingMilliseconds != 0) {
    					t2.start();
    					durationEnabled = true;
                		}
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
    			Bukkit.getServer().broadcastMessage( getConfig().getString("maintenanceStart").replaceAll("&", "§") );
    			if (remainingMilliseconds != 0) {
    			t2.start();
    			}
        	}
    		} catch (NumberFormatException e){
    			sender.sendMessage( getConfig().getString("inputErrorSchedule").replaceAll("&", "§") );
    		}    
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void MaintenanceListPing (final ServerListPingEvent event) {
    	if ( maintenanceTime == true ) {
    		if (durationEnabled == true ) {
    			try {
    				if (remainingMilliseconds / 60 / 1000 < 1) {
        				event.setMotd( getConfig().getString("maintenanceWithDurationMOTDLessThanOneMinute").replaceAll("&", "§").replaceAll("<n>", "\n"));
    				} else {
    					event.setMotd( getConfig().getString("maintenanceWithDurationMOTD").replaceAll("&", "§").replaceAll("<n>", "\n").replaceAll("<minutes>", String.valueOf(remainingMilliseconds / 60 / 1000)));
    				}
    			} catch (NumberFormatException e){
    				getLogger().info(getConfig().getString("inputErrorDuration").replaceAll("&", "§"));
    			}
    		} else {
        		event.setMotd( getConfig().getString("maintenanceMOTD").replaceAll("&", "§").replaceAll("<n>", "\n") );
    		}
			try {
	    		event.setServerIcon(maintenanceIcon); 
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			try{
				maxPlayer = Integer.parseInt( getConfig().getString("maxPlayersOnMaintenance").replaceAll("&", "§") );
				event.setMaxPlayers( maxPlayer );
			} catch (NumberFormatException e){
				e.printStackTrace();
			}
    	}  	
    }
    
    public class MaintenanceDuration extends BukkitRunnable {
    	
        private final CommandSender sender;
        
        public MaintenanceDuration(CommandSender sender) {
            this.sender = sender;
        }
     
        @SuppressWarnings("static-access")
		@Override
        public void run() {
        	try{
        		long i2 = remainingMilliseconds;
        		for (long i = i2; i >= 0 ; i--) {
        			remainingMilliseconds--;
        			getConfig().set( "remainingMilliseconds" , i);        	 
        			saveConfig();
                	try {
        				t2.sleep(1);
                		if ( getConfig().getLong("remainingMilliseconds") == 0 && i == 0 && durationEnabled == true && maintenanceTime == true ) {
                   			durationEnabled = false;
                			getConfig().set("maintenanceModeOnStart", false);
                			maintenanceTime = false;
                			remainingMilliseconds = 0;
                        	Bukkit.getServer().broadcastMessage( getConfig().getString("maintenanceEnd").replaceAll("&", "§") );
                			getConfig().set( "remainingMilliseconds" , remainingMilliseconds);                	 
                			saveConfig();
                    		}
        			} catch (InterruptedException e) {
        				t2.interrupt();
        			}
        		}
        	} catch (NumberFormatException e){
    			sender.sendMessage( getConfig().getString("inputErrorDuration").replaceAll("&", "§") );
        	}
        }
    }
    
    public class disablingPluginsOnStart extends BukkitRunnable {

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
    
    public class backup extends BukkitRunnable {

    	private final CommandSender sender;
    	
    	public backup(CommandSender sender) {
    		this.sender = sender;
    	}
    	
		@Override
		public void run() {

			if (backingUp == false) {
				backingUp = true;
			String folderPath = new File("").getAbsolutePath() + "/backups/";
			File folder = new File(folderPath);
			if (!folder.exists()) {
				if (folder.mkdir()) {
					getLogger().info("Backup folder created!");
				} else {
					getLogger().warning("Failed to create backup folder...");
				}
			}
  		
            getServer().broadcastMessage( getConfig().getString("backingUpMessage").replaceAll("&", "§") );
            getLogger().info("Backing up...");
            
            
            getServer().dispatchCommand(getServer().getConsoleSender(), "save-all");
            getServer().dispatchCommand(getServer().getConsoleSender(), "save-off");
		    	    
	        backupProcess(sender);
            
	        getServer().dispatchCommand(getServer().getConsoleSender(), "save-on");
	        backingUp = false;
			} else {
				sender.sendMessage( getConfig().getString("alreadyBackingUp").replaceAll("&", "§") );
			}
		}
    	
    }
    
}
