package com.myththewolf.DJMaster.lib.commands;

import com.myththewolf.BotServ.lib.API.command.CommandExecutor;
import com.myththewolf.BotServ.lib.API.command.DiscordCommand;
import com.myththewolf.DJMaster.Tools;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;

public class shardify implements CommandExecutor {
	String URLS = "";

	@Override
	public void onCommand(DiscordCommand arg0) {
		URLS = "";
		arg0.reply("Retriving URLS... This is going to take a while.");
		if (Tools.JSONArray_Contains(arg0.getPlugin().getJSONConfig().getJSONArray("Admins"),
				arg0.getSender().getId())) {

			arg0.getPlugin().getJSONConfig().getJSONArray("shards").forEach(shard -> {
				try {
					JDA instanceTMP = new JDABuilder(AccountType.BOT).setToken(shard.toString()).buildBlocking();

					URLS += instanceTMP.asBot().getInviteUrl(Permission.MANAGE_SERVER, Permission.ADMINISTRATOR) + "\n";
					Thread.sleep(5000);
				} catch (Exception e) {
					// TODO: handle exception
				}

			});
		}
		arg0.reply("Links to add all shards. They require permissions to manage each other! \n" + URLS);
	}

}
