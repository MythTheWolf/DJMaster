package com.myththewolf.DJMaster.lib.commands;

import com.myththewolf.BotServ.lib.API.command.CommandExecutor;
import com.myththewolf.BotServ.lib.API.command.DiscordCommand;
import com.myththewolf.DJMaster.Tools;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

public class shardify implements CommandExecutor {

	@Override
	public void onCommand(DiscordCommand arg0) {
		if (Tools.JSONArray_Contains(arg0.getPlugin().getJSONConfig().getJSONArray("Admins"),
				arg0.getSender().getId())) {
			
			arg0.getPlugin().getJSONConfig().getJSONArray("shards").forEach(shard -> {
				try {
					JDA instanceTMP = new JDABuilder(AccountType.BOT).setToken(shard.toString()).buildBlocking();
					
					instanceTMP.asClient().getApplications().complete();
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			});
		}
}

}
