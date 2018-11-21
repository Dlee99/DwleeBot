package dlee99.DiscordBot;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class QueueListing implements AudioEventListener {
    private final AudioPlayer player;
    private final VoiceChannel voiceChannel;
    public QueueListing(AudioPlayer player, VoiceChannel voiceChannel) {
        this.player = player;
        this.voiceChannel = voiceChannel;
    }



    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        voiceChannel.getGuild().getAudioManager().openAudioConnection(voiceChannel);
        player.playTrack(track);

    }


    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        voiceChannel.getGuild().getAudioManager().closeAudioConnection();
        player.stopTrack();
        player.destroy();
        System.out.println("Track Ended");
    }

    @Override
    public void onEvent(AudioEvent event) {

    }
}
