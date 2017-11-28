package com.myththewolf.DJMaster.lib.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;

import com.myththewolf.BotServ.lib.API.command.CommandExecutor;
import com.myththewolf.BotServ.lib.API.command.DiscordCommand;
import com.myththewolf.DJMaster.Tools;
import com.myththewolf.DJMaster.lib.bots.Maps;
import com.myththewolf.DJMaster.lib.bots.StreamPlayer;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class PlayStream implements CommandExecutor {

	@Override
	public void onCommand(DiscordCommand command) {
		JSONArray DJs = command.getPlugin().getJSONConfig().getJSONArray("DJs");
		if (!Tools.JSONArray_Contains(DJs, command.getSender().getId())) {
			command.failed("You are not a dj!");
			return;
		}
		command.reply(":mag: Searching for availible shards...");
		List<StreamPlayer> players = Maps.PLAYERS.stream().filter(player -> player.isAvailible())
				.collect(Collectors.toList());
		if (players.isEmpty()) {
			command.failed(
					":warning: All stream players are currently in use. Would you like me to DM you when one is availible? [Reply y,n]");
			EventListener LAMB = (event) -> {
				if (event instanceof MessageReceivedEvent
						&& ((MessageReceivedEvent) event).getAuthor().getId().equals(command.getSender().getId())
						&& (Maps.WAITING_DJS.stream().filter(i -> i.getId().equals(command.getSender().getId()))
								.collect(Collectors.toList()).size() < 1)) {
					MessageReceivedEvent cast = (MessageReceivedEvent) event;
					if (cast.getMessage().getContent().toLowerCase().equals("y")) {
						command.reply(":white_check_mark: Subscribed");
						Maps.WAITING_DJS.add(command.getSender());
					}
				}
			};
			command.e.getJDA().addEventListener(LAMB);
		} else {
			StreamPlayer select = players.get(0);
			select.playAudio(command);
			command.reply(":white_check_mark: Playing.");
		}
	}

}
