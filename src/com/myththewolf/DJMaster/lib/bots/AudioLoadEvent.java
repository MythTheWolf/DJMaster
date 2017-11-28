package com.myththewolf.DJMaster.lib.bots;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class AudioLoadEvent implements AudioLoadResultHandler{
	private StreamPlayer player;
	private AudioPlayer AP;
	public AudioLoadEvent(StreamPlayer SP,AudioPlayer AP1) {
		player = SP;
		AP = AP1;
	}
	@Override
	public void loadFailed(FriendlyException arg0) {
		player.shutdown();
		
	}

	@Override
	public void noMatches() {
		player.shutdown();
		
	}

	@Override
	public void playlistLoaded(AudioPlaylist arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trackLoaded(AudioTrack arg0) {
		AP.playTrack(arg0);
	}

}
