![Logo] (http://image.noelshack.com/fichiers/2014/19/1399305972-maintenance.png)
===========================

**Maintenance Manager is the ultimate maintenance plugin which makes maintenances times on your server way easier!**
For the moment, it provides several exclusivities I havn't found anywhere else. It is very simple to use and to configure (even if the configuration is optionnal). I want to make it the best maintenance plugin for Bukkit so I'll do the maximum of myself to make frequent updates and add a maximum of features.

*Current version: 1.01*


##**Check out [this] (https://github.com/JeremGamer/Maintenance-Manager--CB-Plugin/wiki/Download) page for downloads!**##


##Features:

- /maintenance command manages the maintenance time: /maintenance on|off
- Maintenance schedule. Schedule your maintenance time and advert players with /maintenance on [time before maintenance time] (Optionnal, in minutes)
- Schedule interruption with /maintenance cancel.
- Define the duration of your maintenance with /maintenance on [time before maintenance time] <duration> (Optionnal, in minutes)
- Kicks non-op players and players who don't have the permission to log-in during the maintenance.
- Prevents non authorized players to log-in during maintenance.
- Changes the motd on the multi-player screen when the server is in maintenance.
- Commands can be casted from console or in-game.
- Custom server icon for maintenance.
- Server reload support.
- You can reload the config without restarting the server or reloading it entirely with /maintenance reload.
- Full permissions supported.
- Full texts customization.
- You can choose the number of slots available during a maintenance.
- Manage your plugins in real-time without having to reload or restart the server with /maintenance [enable|disable] (plugin name). Disabled plugins stay disabled after restart or reload until you re-enable it. 
- Countdown in motd when a maintenance has a duration planned. 
- Check the server hardware at any time with /cpu and /ram!
- Aliases on /maintenance command. See the Commands section for further informations. 
- Backups on command.

##Commands:

**/maintenance** is the main command. You'll have to add the following arguments:

*Aliases:* **/mmode , /maint**
* on: turn on the maintenance mode
    - {schedule in minutes} (optionnal)
    - {duration of the maintenance in minutes} (optionnal)
* cancel: cancels a scheduled maintenance. 
* off: turn off the maintenance mode
* reload: reload the plugin's config file
* enable {plugin name}: enable the selected plugin.
* disable {plugin name}: disable the selected plugin.
**Note: If you are not sure about the plugin name, type /plugins.**
* backup: Saves the server folder into a zip file located in the "backups" folder.

**/cpu**: Returns the current use of the server's CPU in percent.

**/ram**: Returns the current use of the server's RAM in percent and the current amount of MB used. Note: Works only with Windows, Linux and MacOS.

##Permissions:

maintenance.maintenance: Allows to start or stop a maintenance.

maintenance.maintenance.cancel: Allows to cancel a scheduled maintenance.

maintenance.maintenance.backup: Allows to use the /maintenance backup command.

maintenance.access: Allows to enter the server during maintenance mode (and don't be kicked when the maintenance begin)

maintenance.reload: Allows to reload the config file.

maintenance.manage.plugins: Allows to disable or enable plugins.

maintenance.cpu: Allows to perform /cpu command.

maintenance.ram: Allows to perform /ram command.

Of course, OP players have these permissions. Caution: *maintenance.** doesn't work!


##Configuration:

By default, the config file looks like this:

```
maintenanceModeOnStart: false //Don't touch this line!
remainingMilliseconds: 0 //Don't touch this line!
kickMessage: "\xa7c\xa7nThe server is currently under maintenance. Come back later." //Displayed to players kicked by MaintenanceManager.
maintenanceMessage: "\xa7c\xa7nThe server is currently under maintenance. Come back\
  \ later." //Displayed to players who attempt to connect during a maintenance and who havn't the permission to connect.
maintenanceMOTD: "\xa76\xa7o\xa7lMaintenance mode..." //The motd displayed on the multiplayer screen during a maintenance.
maintenanceWithDurationMOTDBegin: "\xa76\xa7o\xa7lMaintenance mode...\xa72\xa7l" //The first line of the motd when the maintenance has a duration planned
maintenanceWithDurationMOTDEnd: "\xa7r\xa7eminutes remaining!" //The second line of the motd when the maintenance has a duration planned. Follows the number of minutes remaining.
maintenanceWithDurationMOTDLessThanOneMinute: "\xa72Less than one minute remaining!"  //The second line of the motd when the maintenance has a duration planned and there is less than one minute remaining.
maintenanceStart: "\xa72\xa7o\xa7lMaintenance time!" //Broadcast displayed at the beginning of a maintenance.
maintenanceEnd: "\xa76\xa7o\xa7lMaintenance finished!" //Broadcast displayed at the end of a maintenance.
scheduleMessageBegin: "\xa75\xa7oMaintenance in\xa7r\xa74\xa7l" //The begin of the schedule message, the time value follows in the code...
scheduleMessageEnd: "\xa7r\xa75\xa7ominutes!" //The end of the schedule message.
scheduleCanceled: "\xa7a\xa7oThe scheduled maintenance have been canceled!" //Displays in a broadcast when a maintenance is canceled.
noMaintenanceScheduled: "\xa7cNo maintenance scheduled..." //Displays when /maintenance cancel is performed while no maintenance is scheduled.
scheduleLessThanOneMinute: "\xa75\xa7oMaintenance in less than one minute!" //Schedule message when the maintenance will begin in less than one minute
inputErrorSchedule: "\xa7cThe time value for schedule must be an integer!" //Error message for wrong input
inputErrorDuration: "\xa7cThe time value for duration must be an integer!" //Error message for wrong input
pluginManagementArgumentErrorDisable: "\xa7cPlease name the plugin you want to disable!" //Error message for wrong input
pluginManagementArgumentErrorEnable: "\xa7cPlease name the plugin you want to enable!" //Error message for wrong input
pluginDisabled: "\xa7a\xa7osuccessfully disabled!" //Displayed when you disable a plugin. The plugin name will be just before it, no space needed.
pluginEnabled: "\xa7a\xa7osuccessfully enabled!" //Displayed when you enable a plugin. The plugin name will be just before it, no space needed.
maintenanceAlreadyLaunched: "\xa7cThe server is already in maintenance mode." //Displayed when /maintenance on is performed during a maintenance.
noMaintenanceLaunched: "\xa7cMaintenance mode is already off." //Displayed when /maintenance off is performed when the maintenance mode is off.
cpuUsage: "\xa73CPU used at\xa72\xa7l" //Followed by the CPU usage percentage.
ramUsage: "\xa73RAM used at\xa72\xa7l" //Followed by the RAM usage. (Format: "x% || yMB")
maxPlayersOnMaintenance: 10 //Must be an integer! The number of slots available during a maintenance
maintenanceIcon: http://image.noelshack.com/fichiers/2014/19/1399387761-maintenancelogo.png //Check the adress two times before loading the config!
disabledPlugins: [] //Take care when you modify the list!
```

*Note: Your icon must be a 64X64 png image! Protected adresses (https://) may not work!*

Advice: If you don't want to change the icon, upload your original one and put the adress in the config file.  You can also use a maintenance icon located on your local drive using a file:// URI, e.g. file:///C:/craftbukkit/server-icon-maintenance.png

**Important: If your server is running without icon, the one which is in the config file will be displayed even out of a maintenance!**

To add colors to your texts, just put '§' and a character from 0-9 and a-f before the words you want to color. Please refer you to this page to know formatting codes: http://minecraft.gamepedia.com/Formatting_codes


##Setup


To set up Maintenance Manager, simply download the jar file and put it into "plugins" folder!

And now enjoy the uniqueness of MaintenanceManager!


##To do

* Your suggestions. 
