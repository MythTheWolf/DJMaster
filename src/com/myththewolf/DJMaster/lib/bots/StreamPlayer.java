package com.myththewolf.DJMaster.lib.bots;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.myththewolf.BotServ.lib.API.command.DiscordCommand;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

public class StreamPlayer {
	private boolean isPlaying;
	private String DJHolder;
	private JDA instance;
	private List<AudioManager> audioManagers = new ArrayList<>();
	private List<AudioPlayerManager> playerManagers = new ArrayList<>();
	private List<AudioPlayer> players = new ArrayList<>();
	private String token;
	private List<VoiceChannel> SUBCHANNELS = new ArrayList<>();
	private List<String> GUILDIS = new ArrayList<>();
	private DiscordCommand command;

	public StreamPlayer(String botToken) {
		token = botToken;

	}

	public boolean isAvailible() {
		return !isPlaying;
	}

	public User getDJ() {
		if (this.DJHolder == null)
			return null;
		else
			return this.instance.getUserById(DJHolder);

	}

	public void playAudio(DiscordCommand cmdimpl) {
		GUILDIS = new ArrayList<>();
		this.command = cmdimpl;
		cmdimpl.reply(":timer: Getting info..");
		this.DJHolder = cmdimpl.getSender().getId();
		JSONArray MOUNTS = cmdimpl.getPlugin().getJSONConfig().getJSONArray("mountpoints");
		JSONObject mountpoint = null;
		for (int i = 0; i < MOUNTS.length(); i++) {
			if (MOUNTS.getJSONObject(i).getString("DJId").equals(cmdimpl.getSender().getId())) {
				mountpoint = MOUNTS.getJSONObject(i);
				break;
			}
		}
		String URL = mountpoint.getString("mount");
		JSONArray CHANNELS = mountpoint.getJSONArray("channels");
		try {
			cmdimpl.reply(":timer: Starting shard..");
			this.instance = new JDABuilder(AccountType.BOT).setToken(token).buildBlocking();
		} catch (Exception e) {
			cmdimpl.failed(e.getMessage());
		}
		cmdimpl.reply("Building channel job list..");
		CHANNELS.forEach(xyz -> {
			String channelID = xyz.toString();
			String GUILDID = instance.getVoiceChannelById(channelID).getGuild().getId();
			VoiceChannel VC = instance.getVoiceChannelById(channelID);
			if (GUILDIS.contains(VC.getGuild().getId())) {
				cmdimpl.reply(":warning: Skipping channel of same guild: "
						+ instance.getVoiceChannelById(channelID).getName());
			} else {

				AudioManager AM = instance.getGuildById(GUILDID).getAudioManager();

				AudioPlayerManager APM = new DefaultAudioPlayerManager();
				AudioSourceManagers.registerRemoteSources(APM);
				AudioPlayer AP = APM.createPlayer();
				AM.setSendingHandler(new AudioPlayerSendHandler(AP));
				cmdimpl.reply(":timer: Connecting and playing on: " + VC.getGuild().getName() + "/" + VC.getName());
				APM.loadItem(URL, new AudioLoadEvent(this, AP));
				AM.openAudioConnection(VC);
				this.audioManagers.add(AM);
				this.playerManagers.add(APM);
				this.players.add(AP);
				GUILDIS.add(VC.getGuild().getId());
				this.isPlaying = true;
				this.SUBCHANNELS.add(VC);
				instance.getPresence().setGame(Game.streaming("Cloud 9.75 Radio", URL));
			}
		});
		EmbedBuilder EB = new EmbedBuilder();
		EB.setColor(Color.GREEN);
		EB.setTitle("Playing stream", URL);
		EB.setImage(getDJ().getAvatarUrl());
		EB.addField("DJ Name", getDJ().getName(), true);
		EB.addField("Stream URL", URL, true);
		EB.setFooter("Playing this stream on " + (this.SUBCHANNELS.size()) + " channels", null);
		cmdimpl.reply(EB);
	}

	public void shutdown() {
		List<User> avoidconcurrentmodification = new ArrayList<>(Maps.WAITING_DJS);
		
		avoidconcurrentmodification.forEach(DJ -> {
			DJ.openPrivateChannel().complete().sendMessage("A new shard just opened!").queue();
			Maps.WAITING_DJS.remove(DJ);
		});
		this.command.reply("Received shutdown signal. Terminating shard");
		this.audioManagers.forEach(i -> i.closeAudioConnection());
		this.playerManagers.clear();
		this.players.forEach(p -> p.destroy());
		this.GUILDIS.clear();
		this.isPlaying = false;
		this.DJHolder = null;
		this.SUBCHANNELS.clear();
		this.instance.shutdown();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.instance = null;
	}

}
