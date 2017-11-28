package com.myththewolf.DJMaster.lib.bots;

import java.awt.Color;

import com.myththewolf.BotServ.lib.API.command.DiscordCommand;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import net.dv8tion.jda.core.EmbedBuilder;

public class AudioEvents extends AudioEventAdapter {
	private DiscordCommand cmd;
	private StreamPlayer botPlayer;

	public AudioEvents(DiscordCommand impl, StreamPlayer inst) {
		cmd = impl;
		botPlayer = inst;
	}

	@Override
	public void onPlayerPause(AudioPlayer player) {
		// Player was paused
	}

	@Override
	public void onPlayerResume(AudioPlayer player) {
		// Player was resumed
	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		// A track started playing
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason.mayStartNext) {
			// Start next track
		}
		if (endReason.equals(AudioTrackEndReason.FINISHED)) {
			botPlayer.shutdown();
		}
		// endReason == FINISHED: A track finished or died by an exception (mayStartNext
		// = true).
		// endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
		// endReason == STOPPED: The player was stopped.
		// endReason == REPLACED: Another track started playing while this had not
		// finished
		// endReason == CLEANUP: Player hasn't been queried for a while, if you want you
		// can put a
		// clone of this back to your queue
	}

	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
		cmd.failed(exception.getMessage());
		botPlayer.shutdown();
	}

	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {

		cmd.reply(new EmbedBuilder().setColor(Color.YELLOW).setTitle("Stuck")
				.setDescription(":warning: Stream seems to be silent/stuck. Aborting."));
		botPlayer.shutdown();
	}
}
