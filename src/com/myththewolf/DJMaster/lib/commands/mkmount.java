package com.myththewolf.DJMaster.lib.commands;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.myththewolf.BotServ.lib.API.command.CommandExecutor;
import com.myththewolf.BotServ.lib.API.command.DiscordCommand;

public class mkmount implements CommandExecutor {
	boolean found = false;

	@Override
	public void onCommand(DiscordCommand arg0) {
		JSONArray ADMINS = arg0.getPlugin().getJSONConfig().getJSONArray("Admins");
		found = false;
		ADMINS.forEach(con -> {
			if (con.equals(arg0.getSender().getId())) {
				found = true;
			}
		});
		if (!found) {
			arg0.failed("You are not a admin!");
			return;
		}
		if (arg0.e.getMessage().getMentionedUsers().size() < 1 || arg0.getArgs().length < 2) {
			arg0.failed("Invalid command usage! Use !man mkmount for help");
			return;
		}

		JSONObject CONFIG = arg0.getPlugin().getJSONConfig();
		if (!arg0.getArgs()[0].startsWith("https://")) {
			arg0.failed("Invalid URL: " + arg0.getArgs()[0]);
			return;
		}
		String mount = arg0.getArgs()[0];
		JSONObject add = new JSONObject();
		List<String> tmpIDS = new ArrayList<>();
		CONFIG.getJSONArray("mountpoints").forEach(ob -> {
			tmpIDS.add(((JSONObject) ob).getString("DJId"));
		});
		if (tmpIDS.contains(arg0.e.getMessage().getMentionedUsers().get(0).getId())) {
			arg0.failed("A mountpoint for that user already exists!");
			return;
		}
		add.put("mount", mount);
		add.put("channels", new JSONArray());
		add.put("DJId", arg0.e.getMessage().getMentionedUsers().get(0).getId());
		CONFIG.getJSONArray("mountpoints").put(add);
		arg0.getPlugin().saveConfig(CONFIG);
	}

}
