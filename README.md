![Logo] (http://image.noelshack.com/fichiers/2014/19/1399305972-maintenance.png)
===========================

Maintenance is a simple plugin which makes maintenances times on your server way more easier.

I want to make it the best maintenance plugin for Bukkit so I'll do the maximum of myself to make frequent updates and add a maximum of stable features.


##Features:

- /maintenance command manages the maintenance time: /maintenance on|off
- Maintenance schedule. Schedule your maintenance time and advert players with /maintenance on {time before maintenance time} (Optionnal, in minutes)
- Define the duration of your maintenance with /maintenance on {time before maintenance time} <duration> (Optionnal, in minutes)
- Kicks non-op players and players who don't have the permission to log-in during the maintenance.
- Prevents non authorized players to log-in during maintenance.
- Changes the motd on the multi-player screen when the server is in maintenance.
- Commands can be casted from console or in-game.
- Config file for custom texts ,broadcasts and motd.
- Custom server icon for maintenance.
- Server reload support.
- Colors in texts.
- You can reload the config without restarting the server or reloading it entirely with /maintenance reload. 
- Full permissions supported.
- Full texts customization.
- You can choose the number of slots available during a maintenance.
- Manage your plugins in real-time without having to reload or restart the server with /maintenance [enable|disable] (plugin name)

##Commands:

**/maintenance** is the main command. You'll have to add the following arguments:
* on: turn on the maintenance mode
    - {schedule in minutes} (optionnal)
    - {duration of the maintenance in minutes} (optionnal)
* off: turn off the maintenance mode
* reload: reload the plugin's config file
* enable {plugin name}: enable the selected plugin.
* disable {plugin name}: disable the selected plugin.
**Note: If you are not sure about the plugin name, type /plugins.**


##Permissions:

maintenance.maintenance: Allows to start or stop a maintenance.

maintenance.access: Allows to enter the server during maintenance mode (and don't be kicked when the maintenance begin)

maintenance.reload: Allows to reload the config file.

maintenance.manage.plugins: Allows to disable or enable plugins.

Of course, OP players have these permissions.


##Configuration:

By default, the config file looks like this:

```
maintenanceModeOnStart: false //This line is mainly for total reload support
kickMessage: The server is currently under maintenance. Come back later. //Displayed to players kicked by MaintenanceManager.
maintenanceMessage: The server is currently under maintenance. Come back later. //Displayed to players who attempt to connect during a maintenance and who havn't the permission to connect.
maintenanceMOTD: Maintenance mode... //The motd displayed on the multiplayer screen during a maintenance.
maintenanceStart: Maintenance time! //Broadcast displayed at the beginning of a maintenance.
maintenanceEnd: Maintenance finished! //Broadcast displayed at the end of a maintenance.
scheduleMessageBegin: Maintenance in //The begin of the schedule message, the time value follows in the code...
scheduleMessageEnd: minutes! //The end of the schedule message.
scheduleLessThanOneMinute: Maintenance in less than one minute! //Schedule message when the maintenance will begin in less than one minute
inputErrorSchedule: The time value for schedule must be an integer! //Error message for wrong input
inputErrorDuration: The time value for duration must be an integer! //Error message for wrong input
pluginManagementArgumentErrorDisable: Please name the plugin you want to disable! //Error message for wrong input
pluginManagementArgumentErrorEnable: Please name the plugin you want to enable! //Error message for wrong input
pluginDisabled: successfully disabled! //Displayed when you disable a plugin. The plugin name will be just before it, no space needed.
pluginEnabled: successfully enabled! //Displayed when you enable a plugin. The plugin name will be just before it, no space needed.
maintenanceAlreadyLaunched: The server is already in maintenance mode. //Displayed when /maintenance on is performed during a maintenance.
noMaintenanceLaunched: Maintenance mode is already off. //Displayed when /maintenance off is performed when the maintenance mode is off.
maxPlayersOnMaintenance: 10 //Must be an integer! The number of slots available during a maintenance
maintenanceIcon: http://image.noelshack.com/fichiers/2014/19/1399387761-maintenancelogo.png //Check the adress two times before loading the config!
```

*Note: Your icon must be a 64X64 png image! Protected adresses (https://) may not work!*

Advice: If you don't want to change the icon, upload your original one and put the adress in the config file.

**Important: If your server is running without icon, the one which is in the config file will be displayed even out of a maintenance!**

To add colors to your texts, just put '§' and a character from 0-9 and a-f before the words you want to color. Please refer you to this page to know formatting codes: http://minecraft.gamepedia.com/Formatting_codes


##To do

* Fix the config.yml issue.
* Recurrent maintenances.
* Schedule interruption
