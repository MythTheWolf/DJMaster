package com.myththewolf.DJMaster.lib.commands;

import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.myththewolf.BotServ.lib.API.command.CommandExecutor;
import com.myththewolf.BotServ.lib.API.command.DiscordCommand;
import com.myththewolf.BotServ.lib.tool.Utils;
import com.myththewolf.DJMaster.Tools;

public class IsAdmin implements CommandExecutor {
	private boolean found = false;

	@Override
	public void onCommand(DiscordCommand arg0) {
		found = false;
		JSONArray ADMINS = arg0.getPlugin().getJSONConfig().getJSONArray("Admins");

		ADMINS.forEach(con -> {
			if (con.equals(arg0.getSender().getId())) {
				found = true;
			}
		});
		if ((arg0.e.getGuild().getOwner().getUser().getId().equals(arg0.getSender().getId()) || found)
				) {
			arg0.e.getMessage().getMentionedUsers().stream().filter(user -> !Tools.JSONArray_Contains(ADMINS, user.getId())).collect(Collectors.toList()).forEach(user -> {
				JSONObject conf = arg0.getPlugin().getJSONConfig();
				conf.getJSONArray("Admins").put(user.getId());
				arg0.getPlugin().saveConfig(conf);
			});
			arg0.reply(":ok_hand:");
		
			arg0.reply("New JSON config: " + Utils.readFile(arg0.getPlugin().getConfig()));
		} else {
			arg0.failed("You don't have permissions to do that.");
		}
	}

}
