package com.hazyarc14;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AudioManager;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        String githubAudioBaseURL = "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/";
        String githubImageBaseURL = "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/images/";

        String[] command = event.getMessage().getContentRaw().split(" ", 2);
        Guild guild = event.getGuild();
        VoiceChannel voiceChannel = null;

        if (guild != null) {

            if (command[0] == "~play") {
                try {
                    voiceChannel = event.getGuild().getVoiceChannelById(command[2]);
                } catch (Exception e) {
                    log.error("Could not get voice channel by id " + command[2] + " :: ", e);
                }
            } else if (command.length == 2) {
                try {
                    voiceChannel = event.getGuild().getVoiceChannelById(command[1]);
                } catch (Exception e) {
                    log.error("Could not get voice channel by id " + command[1] + " :: ", e);
                }
            }

            if (voiceChannel == null) {
                voiceChannel = event.getMember().getVoiceState().getChannel();
            }

            if ("!help".equalsIgnoreCase(command[0])) {

                event.getMessage().delete().queue();

                String helpMessage = "```" +
                        "Since Your Little Bitch Ass Can't Remember Shit!\n\n" +
                        "Audio Triggers:\n" +
                        "!ahhha\n" +
                        "!arams\n" +
                        "!auPhau\n" +
                        "!boutTime\n" +
                        "!celsoHere\n" +
                        "!ckelso\n" +
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
                        "!meetYa\n" +
                        "!monkeys\n" +
                        "!oil\n" +
                        "!ripDoggo\n" +
                        "!sameGame\n" +
                        "!snap\n" +
                        "!tinsel\n" +
                        "!warus\n" +
                        "!watch\n" +
                        "!weeee\n" +
                        "!yooo\n" +
                        "\nEmotes:\n" +
                        ";coggers;\n" +
                        ";crabPls;\n" +
                        ";dance;\n" +
                        ";pepeD;\n" +
                        ";pepeDance;\n" +
                        ";pepegaPls;\n" +
                        ";pepeJam;\n" +
                        ";pepoSabers;\n" +
                        ";ppHop;\n" +
                        ";rainbowWeeb;\n" +
                        ";schubertWalk;\n" +
                        ";triKool;\n" +
                        "```";


                event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(helpMessage).queue());

            }

            if (voiceChannel != null) {

                if ("!ahhha".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !ahhha");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "ahhha.mp3");
                }
                if ("!arams".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !arams");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "arams.mp3");
                }
                if ("!auPhau".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !auPhau");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "auPhau.mp3");
                }
                if ("!boutTime".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !boutTime");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "boutTime.mp3");
                }
                if ("!celsoHere".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !celsoHere");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "celsoHere.mp3");
                }
                if ("!ckelso".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !ckelso");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "ckelso.mp3");
                }
                if ("!clap".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !arams");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "clap.mp3");
                }
                if ("!clickyBoi".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !arams");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "clickyBoi.mp3");
                }
                if ("!croissant".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !croissant");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "croissant.mp3");
                }
                if ("!dickHer".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !dickHer");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "dickHer.mp3");
                }
                if ("!dumbassGame".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !dumbassGame");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "dumbassGame.mp3");
                }
                if ("!fortFucker".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !fortFucker");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "fortFucker.mp3");
                }
                if ("!goldfish".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !goldfish");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "goldfish.mp3");
                }
                if ("!homie".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !homie");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "homie.mp3");
                }
                if ("!horn".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !horn");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "horn.mp3");
                }
                if ("!horse".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !horse");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "horse.mp3");
                }
                if ("!kirk".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !kirk");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "kirk.mp3");
                }
                if ("!lag".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !lag");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "lag.mp3");
                }
                if ("!licker".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !licker");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "licker.mp3");
                }
                if ("!magicResist".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !magicResist");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "magicResist.mp3");
                }
                if ("!meetYa".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !meetYa");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "meetYa.mp3");
                }
                if ("!monkey".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !monkey");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "monkeys.mp3");
                }
                if ("!monkeys".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !monkeys");
                    event.getMessage().delete().queue();
                    BufferedImage bufferedImage = null;
                    File image = new File("monkeys.png");
                    try {
                        bufferedImage = ImageIO.read(new URL(githubImageBaseURL + "monkeys.png"));
                        ImageIO.write(bufferedImage, "png", image);
                    } catch (IOException e) {
                        log.error("Exception: ", e);
                    }
                    guild.getDefaultChannel().sendFile(image).queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "monkeys.mp3");
                }
                if ("!oil".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !oil");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "audio/oil.mp3");
                }
                if (event.getAuthor().getIdLong() != 93140127949287424L && "!ripDoggo".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !ripDoggo");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "ripDoggo.mp3");
                }
                if ("!sameGame".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !sameGame");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "sameGame.mp3");
                }
                if ("!snap".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !snap");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "snap.mp3");
                }
                if ("!sweetheart".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !sweetheart");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "sweetheart.mp3");
                }
                if ("!tinsel".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !tinsel");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "tinsel.mp3");
                }
                if ("!warus".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !warus");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "warus.mp3");
                }
                if ("!watch".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !watch");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "watch.mp3");
                }
                if ("!weeee".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !weeee");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "weeee.mp3");
                }
                if ("!yooo".equalsIgnoreCase(command[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !yooo");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "yooo.mp3");
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

            if (";coggers;".equalsIgnoreCase(command[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;coggers;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoSabers", githubImageBaseURL + "coggers.gif");
            }
            if (";crabPls;".equalsIgnoreCase(command[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;crabPls;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoSabers", githubImageBaseURL + "crabPls.gif");
            }
            if (";dance;".equalsIgnoreCase(command[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;dance;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoSabers", githubImageBaseURL + "dance.gif");
            }
            if (";pepeD;".equalsIgnoreCase(command[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepeD;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoSabers", githubImageBaseURL + "pepeD.gif");
            }
            if (";pepeDance;".equalsIgnoreCase(command[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepeDance;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoSabers", githubImageBaseURL + "pepeDance.gif");
            }
            if (";pepegaPls;".equalsIgnoreCase(command[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepegaPls;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoSabers", githubImageBaseURL + "pepegaPls.gif");
            }
            if (";pepeJam;".equalsIgnoreCase(command[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepeJam;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoSabers", githubImageBaseURL + "pepeJam.gif");
            }
            if (";pepoSabers;".equalsIgnoreCase(command[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepoSabers;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoSabers", githubImageBaseURL + "pepoSabers.gif");
            }
            if (";ppHop;".equalsIgnoreCase(command[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;ppHop;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoSabers", githubImageBaseURL + "ppHop.gif");
            }
            if (";rainbowWeeb;".equalsIgnoreCase(command[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;rainbowWeeb;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoSabers", githubImageBaseURL + "rainbowWeeb.gif");
            }
            if (";schubertWalk;".equalsIgnoreCase(command[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;schubertWalk;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoSabers", githubImageBaseURL + "schubertWalk.gif");
            }
            if (";triKool;".equalsIgnoreCase(command[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;triKool;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoSabers", githubImageBaseURL + "triKool.gif");
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

    private static void sendEmote(TextChannel channel, String emoteName, String emoteUrl) {

        File gif = new File(emoteName + ".gif");
        try {
            FileUtils.copyURLToFile(new URL(emoteUrl), gif);
            channel.sendFile(gif).queue();
        } catch (Exception e) {
            log.error("Error: ", e);
        }

    }

//    @Override
//    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
//
//        Guild guild = event.getGuild();
//
//        Long ckelsoId = new Long("93105200365043712");
//        Long spitfire = new Long("93121331700195328");
//        Long gopherit = new Long("93140127949287424");
//
//        List<Long> userIdList = new ArrayList<>();
//        userIdList.add(ckelsoId);
//        userIdList.add(spitfire);
//        userIdList.add(gopherit);
//
//        List<Member> currentVoiceChannelMembers = event.getChannelJoined().getMembers();
//        List<Long> currentVoiceChannelMembersIdList = new ArrayList<>();
//
//        for (Member member: currentVoiceChannelMembers) {
//            currentVoiceChannelMembersIdList.add(member.getUser().getIdLong());
//        }
//
//        if (currentVoiceChannelMembersIdList.containsAll(userIdList)) {
//            guild.getDefaultChannel().sendMessage("Still Playing Dumbass Games!?").queue();
//        }
//
//    }
}
