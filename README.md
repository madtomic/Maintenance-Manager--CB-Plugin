![Logo] (http://image.noelshack.com/fichiers/2014/19/1399305972-maintenance.png)
===========================

(Please note this plugin is a very begin and a snapshot, please report me issues and suggest me things to add to make it better!)

Maintenance is a simple plugin which makes maintenances times on your server way more easier.

This is my first plugin and I don't know very well Bukkit API for the moment but don't care, I'll make updates and improve the plugin!


##Features:

- /maintenance command manages the maintenance time: /maintenance on|off
- Maintenance schedule. Schedule your maintenance time and advert players with /maintenance on [time before maintenance time] (Optionnal, in minutes)
- Define the duration of your maintenance with /maintenance on |time before maintenance time| <duration> (Optionnal, in minutes)
- Kicks non-op players and players who don't have the permission to log-in during the maintenance.
- Prevent non authorized players to log-in during maintenance.
- Change the motd on the multi-player screen when the server is in maintenance.
- Commands can be casted from console or in-game.
- Full permissions supported: 

 maintenance.acess (give the permission to log-in during maintenance and to not being kicked when a maintenance begin)
 
 maintenance.maintenance (allow to start or stop a maintenance)
 
 OP players have these permissions 


##To do

- Handle reload
- Add colours to broadcasts
- Improve the countdown broadcasts for scheduled maintenance.
- Add a message when schedule or duration values are not int values instead of an ugly error message in console. 
