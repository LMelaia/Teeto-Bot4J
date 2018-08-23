package net.lmelaia.teeto.command.commands;

import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.GuildController;
import net.lmelaia.teeto.LogManager;
import net.lmelaia.teeto.Teeto;
import net.lmelaia.teeto.audio.AudioFile;
import net.lmelaia.teeto.audio.AudioManager;
import net.lmelaia.teeto.audio.AudioPlayer;
import net.lmelaia.teeto.command.CommandHandler;
import net.lmelaia.teeto.command.CommandManager;
import net.lmelaia.teeto.messaging.Responses;
import org.apache.logging.log4j.Logger;

/**
 * Commands related to audio.
 */
public final class AudioCommands {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * The applications audio manager.
     */
    private static final AudioManager AUDIO_MANAGER = AudioManager.getAudioManager();

    /**
     * The audio file we will play.
     */
    private static final AudioFile AUDIO_FILE = AudioManager.getAudioManager().getAudioFileFromName("nyan");

    /**
     * The applications responses object
     */
    private static final Responses RESPONSES = Teeto.getTeeto().getResponses();

    //Private constructor
    private AudioCommands(){}

    /**
     * Disconnects the bot from the voice channel it
     * is in within the current guild.
     *
     * @param g the guild who's voice channel we are disconnecting from.
     * @return response to user.
     */
    @CommandHandler(".audio.disconnect")
    public static String disconnect(Guild g){
        AudioPlayer guildPlayer = AUDIO_MANAGER.getAudioPlayer(g);

        if(!guildPlayer.isConnected()){
            return RESPONSES.getResponse("audio.not_connected").get();
        }

        VoiceChannel channel = guildPlayer.getConnectedChannel();
        guildPlayer.disconnectFromVoice();

        return RESPONSES.getResponse("audio.left")
                .setPlaceholder("{@channel}", channel.getName())
                .get();
    }

    /**
     * Attempts to fix any audio issues that may have arrived.
     *
     * @param g the guilds who's audio player we are trying to fix.
     * @return the response to the user.
     */
    @CommandHandler(".audio.reset")
    public static String reset(Guild g){
        AudioPlayer guildPlayer = AUDIO_MANAGER.getAudioPlayer(g);
        guildPlayer.stop();
        guildPlayer.disconnectFromVoice();

        try {
            Thread.sleep(500);
            guildPlayer.connectToVoice(getDesignatedHellChannel(g), true);
            Thread.sleep(1000);
            guildPlayer.stop();
            guildPlayer.disconnectFromVoice();
        } catch (InterruptedException e) {
            //Should not happen.
        }

        return RESPONSES.getResponse("audio.reset").get();
    }

    /**
     * Connects the bot to the designated hell channel
     * and plays the designated song if possible.
     *
     * @param g the guild who's voice channel channel
     *          we are connecting to.
     * @return the response to the user.
     */
    @CommandHandler(".audio.play")
    public static String play(Guild g){
        AudioPlayer guildPlayer = AUDIO_MANAGER.getAudioPlayer(g);

        VoiceChannel channel = getDesignatedHellChannel(g);

        if(channel == null)
            return RESPONSES.getResponse("audio.no_channel").get();

        guildPlayer.connectToVoice(channel, true);

        guildPlayer.addListener(new AudioEventAdapter() {
            @Override
            public void onTrackEnd(com.sedmelluq.discord.lavaplayer.player.AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason){
                if(endReason.mayStartNext)
                    guildPlayer.play(AUDIO_FILE.getAudioFile().getAbsolutePath());
            }

            @Override
            public void onPlayerPause(com.sedmelluq.discord.lavaplayer.player.AudioPlayer player) {
                super.onPlayerPause(player);
            }
        });

        guildPlayer.play(AUDIO_FILE.getAudioFile().getAbsolutePath());

        return RESPONSES.getResponse("audio.joined")
                .setPlaceholder("{@channel}", channel.getName())
                .get();
    }

    /**
     * Not yet implemented.
     *
     * @param g the guild we are acting on.
     * @param args the command arguments.
     * @return the response to the user.
     */
    @CommandHandler(".audio.set")
    public static String set(Guild g, String[] args){
        return Teeto.getTeeto().getResponses().getResponse("cmd.not_implemented").get();
    }

    /**
     * Moves a user to the designated hell channel
     * and invokes the {@link #play(Guild)} command
     * if possible.
     *
     * @param g the guild we are acting on.
     * @param author the user who sent the commands.
     * @return the response to the user.
     */
    @SuppressWarnings("ConstantConditions")
    @CommandHandler(".audio.play_with")
    public static String playWith(Guild g, User author){
        AudioPlayer guildPlayer = AUDIO_MANAGER.getAudioPlayer(g);
        GuildController controller = new GuildController(g);

        if(getDesignatedHellChannel(g) == null){
            return RESPONSES.getResponse("audio.no_channel").get();
        }

        try{
            controller.moveVoiceMember(g.getMemberById(author.getIdLong()), getDesignatedHellChannel(g)).queue();
        } catch (IllegalStateException e){
            return RESPONSES.getResponse("audio.user_not_in_voice")
                    .setPlaceholder("{@channel}", getDesignatedHellChannel(g).getName())
                    .get();
        }

        CommandManager.getInstance().invokeCommand(".audio.play", g);

        return RESPONSES.getResponse("audio.enjoy").get();
    }

    /**
     * Not yet implemented
     *
     * @param g the guild we are acting on.
     * @param args the command arguments.
     * @return the response to the user.
     */
    @CommandHandler(".audio.set_channel")
    public static String setChannel(Guild g, String[] args){
        return Teeto.getTeeto().getResponses().getResponse("cmd.not_implemented").get();
    }

    /**
     * @param g the guild we are getting the channel from.
     * @return the guilds designated hell channel.
     */
    private static VoiceChannel getDesignatedHellChannel(Guild g){
        for(VoiceChannel channel : g.getVoiceChannels())
            if(channel.getName().equals("Hell"))
                return channel;

        return null;
    }
}
