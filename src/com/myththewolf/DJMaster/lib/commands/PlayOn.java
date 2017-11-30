package com.myththewolf.DJMaster.lib.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.myththewolf.BotServ.lib.API.command.CommandExecutor;
import com.myththewolf.BotServ.lib.API.command.DiscordCommand;
import com.myththewolf.DJMaster.Tools;

public class PlayOn implements CommandExecutor {
	private boolean OK = false;
	String f = "";
	int selected = 0;
	int pos = 0;
	JSONObject CONFIG;
	String bads = "";

	@Override
	public void onCommand(DiscordCommand arg0) {
		pos = 0;
		selected = 0;
		CONFIG = null;
		OK = false;
		bads = "NOP";
		if (!Tools.JSONArray_Contains(arg0.getPlugin().getJSONConfig().getJSONArray("DJs"), arg0.getSender().getId())) {
			arg0.failed("You are not a DJ!");
			return;
		}
		if (arg0.getArgs().length < 1) {
			arg0.failed("Invalid paramters. Use `!man PlayOn` for help.");
			return;
		}
		List<String> finalIDs = new ArrayList<>();
		CONFIG = arg0.getPlugin().getJSONConfig();
		CONFIG.getJSONArray("mountpoints").forEach(mount -> {
			if (mount instanceof JSONObject) {
				if (((JSONObject) mount).getString("DJId").equals(arg0.getSender().getId())) {
					selected = pos;
				}
			}
			pos++;
		});
		if (selected == -1) {
			arg0.failed("You don't have a mountpoint configured. Have an admin add one.");
			return;
		}
		Arrays.stream(arg0.getArgs()).forEach(channel -> {
			finalIDs.add(channel);
		});
		finalIDs.stream().filter(ID -> {
			OK = false;
			arg0.e.getJDA().getGuilds().forEach(guild -> {
				guild.getVoiceChannels().forEach(vc -> {
					if (vc.getId().equals(ID)) {
						OK = true;
					}
				});
			});
			return OK;
		}).collect(Collectors.toList()).forEach(filter -> {
			if (!Tools.JSONArray_Contains(
					((JSONObject) CONFIG.getJSONArray("mountpoints").get(selected)).getJSONArray("channels"), filter)) {
				((JSONObject) CONFIG.getJSONArray("mountpoints").get(selected)).getJSONArray("channels").put(filter);
				arg0.getPlugin().saveConfig(CONFIG);
			}
		});

		finalIDs.stream().filter(ID -> {
			OK = false;
			arg0.e.getJDA().getGuilds().forEach(guild -> {
				guild.getVoiceChannels().forEach(vc -> {
					if (vc.getId().equals(ID)) {
						OK = true;
					}
				});
			});
			return !OK;

		}).collect(Collectors.toList()).forEach(i -> bads += i + "\n");
		if (!bads.equals("NOP")) {
			arg0.reply("Could not add the following Voice Channel IDs: I can't see them! (Am I invited to the server?"
					+ bads.replaceAll("NOP", ""));
		}
	}

}
