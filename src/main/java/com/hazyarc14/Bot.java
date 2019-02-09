package com.hazyarc14;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Bot extends ListenerAdapter {
    public static final Logger log = LoggerFactory.getLogger(Bot.class);

    public static void main(String[] args) throws Exception {
        JDA jda = new JDABuilder(System.getenv("BOT_TOKEN")).build();
        jda.addEventListener(new Bot());
    }

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    private Bot() {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager, guild);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] command = event.getMessage().getContentRaw().split(" ", 2);
        Guild guild = event.getGuild();
        VoiceChannel voiceChannel = null;

        if (guild != null) {

            if (command.length == 2 && command[1].startsWith("#")) {
                try {
                    voiceChannel = event.getGuild().getVoiceChannelById(command[1].substring(1));
                } catch (Exception e) {
                    log.error("Could not get voice channel by id " + command[1] + " :: ", e);
                }
            }

            if (voiceChannel == null) {
                voiceChannel = event.getMember().getVoiceState().getChannel();
            }

            if ("!help".equalsIgnoreCase(command[0])) {
                event.getMessage().delete().queue();
                guild.getDefaultChannel().sendMessage("```" +
                        "Since Your Little Bitch Ass Can't Remember Shit!\n\n" +
                        "Audio Triggers:\n" +
                        "!arams\n" +
                        "!boutTime\n" +
                        "!celsoHere\n" +
                        "!clap\n" +
                        "!clickyBoi\n" +
                        "!croissant\n" +
                        "!dickHer\n" +
                        "!dumbassGame\n" +
                        "!fortFucker\n" +
                        "!goldfish\n" +
                        "!homie\n" +
                        "!horn\n" +
                        "!horse\n" +
                        "!kirk\n" +
                        "!lag\n" +
                        "!licker\n" +
                        "!magicResit\n" +
                        "!monkey\n" +
                        "!oil\n" +
                        "!ripDoggo\n" +
                        "!sameGame\n" +
                        "!snap\n" +
                        "!tinsel\n" +
                        "!warus\n" +
                        "!watch\n" +
                        "!weeee\n" +
                        "!yooo" +
                        "```")
                        .queue();

            }

            if (voiceChannel != null) {

                if ("!arams".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !arams");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/arams.mp3");
                }
                if ("!boutTime".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !boutTime");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/boutTime.mp3");
                }
                if ("!celsoHere".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !celsoHere");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/celsoHere.mp3");
                }
                if ("!clap".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !arams");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/clap.mp3");
                }
                if ("!clickyBoi".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !arams");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/clickyBoi.mp3");
                }
                if ("!croissant".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !croissant");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/croissant.mp3");
                }
                if ("!dickHer".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !dickHer");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/dickHer.mp3");
                }
                if ("!dumbassGame".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !dumbassGame");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/dumbassGame.mp3");
                }
                if ("!fortFucker".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !fortFucker");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/fortFucker.mp3");
                }
                if ("!goldfish".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !goldfish");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/goldfish.mp3");
                }
                if ("!homie".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !homie");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/homie.mp3");
                }
                if ("!horn".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !horn");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/horn.mp3");
                }
                if ("!horse".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !horse");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/horse.mp3");
                }
                if ("!kirk".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !kirk");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/kirk.mp3");
                }
                if ("!lag".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !lag");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/lag.mp3");
                }
                if ("!licker".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !licker");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/licker.mp3");
                }
                if ("!magicResist".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !magicResist");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/magicResist.mp3");
                }
                if ("!monkey".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !monkey");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/monkey.mp3");
                }
                if ("!oil".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !oil");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/oil.mp3");
                }
                if (event.getAuthor().getIdLong() != 93140127949287424L && "!ripDoggo".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !ripDoggo");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/ripDoggo.mp3");
                }
                if ("!sameGame".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !sameGame");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/sameGame.mp3");
                }
                if ("!snap".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !snap");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/snap.mp3");
                }
                if ("!sweetheart".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !sweetheart");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/sweetheart.mp3");
                }
                if ("!tinsel".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !tinsel");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/tinsel.mp3");
                }
                if ("!warus".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !warus");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/warus.mp3");
                }
                if ("!watch".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !watch");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/watch.mp3");
                }
                if ("!weeee".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !weeee");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/weeee.mp3");
                }
                if ("!yooo".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !yooo");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/yooo.mp3");
                }
                
                if ("~play".equals(command[0]) && command.length == 2) {
                    log.info("User: " + event.getAuthor().getName() + " Command: ~play");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, command[1]);
                } else if ("~skip".equals(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: ~skip");
                    event.getMessage().delete().queue();
                    skipTrack(event.getTextChannel());
                } else if ("~leave".equals(command[0])) {
                    guild.getAudioManager().closeAudioConnection();
                }
            }
        }

        super.onMessageReceived(event);
    }

    private void loadAndPlay(final TextChannel channel, final VoiceChannel voiceChannel, final String trackUrl) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
//                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();
                play(channel.getGuild(), musicManager, voiceChannel, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

                play(channel.getGuild(), musicManager, voiceChannel, firstTrack);
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    private void play(Guild guild, GuildMusicManager musicManager, VoiceChannel voiceChannel, AudioTrack track) {
        connectVoiceChannel(guild.getAudioManager(), voiceChannel);

        musicManager.scheduler.queue(track);
    }

    private void skipTrack(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();

//        channel.sendMessage("Skipped to next track.").queue();
    }

    private static void connectVoiceChannel(AudioManager audioManager, VoiceChannel voiceChannel) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            audioManager.openAudioConnection(voiceChannel);
        }
    }
}
