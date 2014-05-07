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
- Colors in texts
- You can reload the config without restarting the server or reloading it entirely with /maintenance reload. 
- Full permissions supported.
 

##Commands:

**/maintenance** is the main command. You'll have to add the following arguments:
* on: turn on the maintenance mode
    - {schedule in minutes} (optionnal)
    - {duration of the maintenance in minutes} (optionnal)
* off: turn off the maintenance mode
* reload: reload the plugin's config file


##Permissions:

maintenance.maintenance: Allows to start or stop a maintenance.

maintenance.access: Allows to enter the server during maintenance mode (and don't be kicked when the maintenance begin)

Of course, OP players have these permissions.


##Configuration:

By default, the config file looks like this:

```
maintenanceModeOnStart: false
kickMessage: The server is currently under maintenance. Come back later.
maintenanceMessage: The server is currently under maintenance. Come back later.
maintenanceMOTD: Maintenance mode...
maintenanceStart: Maintenance time!
maintenanceEnd: Maintenance finished!
scheduleMessageBegin: Maintenance in
scheduleMessageEnd: minutes!
scheduleLessThanOneMinute: Maintenance in less than one minute!
inputErrorSchedule: The time value for schedule must be an integer!
inputErrorDuration: The time value for duration must be an integer!
pluginManagementArgumentErrorDisable: Please name the plugin you want to disable!
pluginManagementArgumentErrorEnable: Please name the plugin you want to enable!
pluginDisabled: successfully disabled!
pluginEnabled: successfully enabled!
maintenanceAlreadyLaunched: The server is already in maintenance mode.
noMaintenanceLaunched: Maintenance mode is already off.
maxPlayersOnMaintenance: 10
maintenanceIcon: http://image.noelshack.com/fichiers/2014/19/1399387761-maintenancelogo.png
```

*Note: Your icon must be a 64X64 png image! Protected adresses (https://) may not work!*

Advice: If you don't want to change the icon, upload your original one and put the adress in the config file.

**Important: If your server is running without icon, the one which is in the config file will be displayed even out of a maintenance!**

To add colors to your texts, just put '§' and a character from 0-9 and a-f before the words you want to color. Please refer you to this page to know formatting codes: http://minecraft.gamepedia.com/Formatting_codes


##To do

* Improve the countdown broadcasts for scheduled maintenance.
* Add permission and message for /maintenance reload.
* Change the number of available slots during a maintenance.
* Custom message for schedule.
* Disable or enable other plugins.
* Recurrent maintenances.
* Add a message on /command reload
* Schedule interruption
* Need improves on /command [on|off]
* Improve plugin.yml
