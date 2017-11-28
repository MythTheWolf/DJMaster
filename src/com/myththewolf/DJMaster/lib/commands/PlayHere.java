package com.myththewolf.DJMaster.lib.commands;

import org.json.JSONArray;
import org.json.JSONObject;

import com.myththewolf.BotServ.lib.API.command.CommandExecutor;
import com.myththewolf.BotServ.lib.API.command.DiscordCommand;
import com.myththewolf.DJMaster.Tools;

public class PlayHere implements CommandExecutor {

	private int pos = 0;
	private int selected = -1;
	
	@Override
	public void onCommand(DiscordCommand arg0) {
		if (!Tools.JSONArray_Contains(arg0.getPlugin().getJSONConfig().getJSONArray("DJs"), arg0.getSender().getId())) {
			arg0.failed("You are not a DJ!");
			return;
		}
		
		
		String VC = null;
		if (!arg0.e.getMember().getVoiceState().inVoiceChannel()) {
			arg0.failed("You are not in a voice channel!");
			return;
		}
		VC = arg0.e.getMember().getVoiceState().getAudioChannel().getId();
		pos = 0;
		selected = 0;
		JSONObject CONFIG = arg0.getPlugin().getJSONConfig();
		CONFIG.getJSONArray("mountpoints").forEach(mount -> {
			if (mount instanceof JSONObject) {
				if (((JSONObject) mount).getString("DJId").equals(arg0.getSender().getId())) {
					selected = pos;
				}
			}
			pos++;
		});
		if(selected == -1) {
			arg0.failed("You don't have a mountpoint configured. Have an admin add one.");
		}
		JSONArray channels = CONFIG.getJSONArray("mountpoints").getJSONObject(selected).getJSONArray("channels");
		CONFIG.getJSONArray("mountpoints").getJSONObject(selected).remove("channels");
		channels.put(VC);
		JSONArray add = new JSONArray();
		channels.forEach(channel -> {
			add.put(channel);
		});
		CONFIG.getJSONArray("mountpoints").getJSONObject(selected).put("channels", add);
		arg0.getPlugin().saveConfig(CONFIG);
		arg0.reply("New JSON: " + arg0.getPlugin().getJSONConfig().toString());
	}

}
