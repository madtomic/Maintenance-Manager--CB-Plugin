![Logo] (http://image.noelshack.com/fichiers/2014/19/1399305972-maintenance.png)
===========================

Maintenance is a simple plugin which makes maintenances times on your server way more easier.

This is my first plugin and I don't know very well Bukkit API for the moment but don't care, I'll make updates and improve the plugin!


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
- You can reload the config without restarting the server or reloading it entirely with /maintenance reload. 
- Full permissions supported: 

 maintenance.acess (give the permission to log-in during maintenance and to not being kicked when a maintenance begin)
 
 maintenance.maintenance (allow to start or stop a maintenance)
 
 OP players have these permissions 
 

##Commands:

**/maintenance** is the main command. You'll have to add the following arguments:
* on: turn on the maintenance mode
    - <schedule in minutes> (optionnal)
    - <duration of the maintenance in minutes> (optionnal)
* off: turn off the maintenance mode
* reload: reload the plugin's config file


##Permissions:

maintenance.maintenance: Allows to start or stop a maintenance.
maintenance.access: Allows to enter the server during maintenance mode (and don't be kicked when the maintenance begin)
Of course, OP players have these permissions.


##Configuration:

By default, the config file looks like this:

```
maintenanceModeOnStart: false //This line updates when a maintenance is launched (reload support)
kickMessage: The server is currently under maintenance. Come back later. //This message is displayed when a player is kicked by MaintenanceManager.
maintenanceMessage: The server is currently under maintenance. Come back later. //When a player who hasn't got the permission attempt to log in the server, this message would be displayed.
maintenanceMOTD: Maintenance mode... //The motd of your server when running in maintenance mode.
maintenanceStart: Maintenance time! //The announcement of the begin of a maintenance.
maintenanceEnd: Maintenance finished! //The announcement of the end of a maintenance.
inputErrorSchedule: The time value for schedule must be an integer! //Error message for invalid argument
inputErrorDuration: The time value for duration must be an integer! //Error message for invalid argument
maintenanceIcon: http://image.noelshack.com/fichiers/2014/19/1399387761-maintenancelogo.png //Put here the adress your the custom icon for maintenance mode.
```

*Note: Your icon must be a 64X64 png image!*

Advice: If you don't want to change the icon, upload your original one and put the adress in the config file.

**Important: If your server is running without icon, the one which is in the config file will be displayed even out of a maintenance!**


##To do

- Add colours to broadcasts
- Improve the countdown broadcasts for scheduled maintenance.
