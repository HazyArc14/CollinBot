package com.hazyarc14;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@EnableScheduling
public class Bot extends ListenerAdapter {

    public static final Logger log = LoggerFactory.getLogger(Bot.class);
    private BotSettings botSettings = new BotSettings();

    private static final String githubAudioBaseURL = "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/audio/";
    private static final String githubImageBaseURL = "https://raw.githubusercontent.com/HazyArc14/CollinBot/master/src/main/resources/images/";

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
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        log.info("Set base role for new user " + event.getUser().getName());
        event.getGuild().addRoleToMember(event.getMember(), event.getJDA().getRoleById("478762864530817036")).complete();

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        Integer trackPosition = 0;
        String voiceChannelId = "";

        String[] commandList = event.getMessage().getContentRaw().split(" ");

        for(String command: commandList) {
            if (command.contains("?t=")) {
                trackPosition = new Integer(command.substring(command.lastIndexOf("?t=") + 3));
                log.info("trackPosition set: " + trackPosition.toString());
            }
            if (command.contains("$vc")) {
                voiceChannelId = command.substring(command.lastIndexOf("$vc") + 3);
                log.info("voiceChannelId: " + voiceChannelId);
            }
            if (command.contains("$f")) {
                Long userId = new Long(command.substring(command.lastIndexOf("$f") + 2));
                botSettings.setVoiceFollowMode(true);
                botSettings.setVoiceFollowUserId(userId);
            }
        }

        Guild guild = event.getGuild();
        VoiceChannel voiceChannel = null;

        if (guild != null) {

            if (!voiceChannelId.equalsIgnoreCase("")) {
                try {
                    voiceChannel = event.getGuild().getVoiceChannelById(voiceChannelId);
                } catch (Exception e) {
                    log.error("Could not get voice channel by id " + voiceChannelId + " :: ", e);
                }
            } else {
                voiceChannel = event.getMember().getVoiceState().getChannel();
            }

            if ("!follow".equalsIgnoreCase(commandList[0]) && event.getAuthor().getIdLong() == 148630426548699136L) {
                event.getMessage().delete().queue();
                botSettings.setVoiceFollowMode(false);
                botSettings.setVoiceFollowUserId(null);
                log.info("follow mode off");
            } else if ("!voiceActionToggle".equalsIgnoreCase(commandList[0]) && event.getAuthor().getIdLong() == 148630426548699136L) {
                event.getMessage().delete().queue();
                botSettings.setVoiceJoinActions(!botSettings.getVoiceJoinActions());
                log.info("voiceActionToggle set to: " + botSettings.getVoiceJoinActions());
            } else if ("!skipToggle".equalsIgnoreCase(commandList[0]) && event.getAuthor().getIdLong() == 148630426548699136L) {
                event.getMessage().delete().queue();
                botSettings.setSkipMode(!botSettings.getSkipMode());
                log.info("skipToggle set to: " + botSettings.getSkipMode());
            } else if ("!timer".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: !timer");
                event.getMessage().delete().queue();
                try {
                    createTimer(guild, event.getTextChannel(), commandList);
                } catch (InterruptedException e) {
                    log.error("Exception: ", e);
                }
            } else if ("!help".equalsIgnoreCase(commandList[0])) {

                event.getMessage().delete().queue();

                String helpMessage = "```" +
                        "Since Your Little Bitch Ass Can't Remember Shit!\n\n" +
                        "Audio Triggers:\n" +
                        "!ahhha\n" +
                        "!arams\n" +
                        "!auPhau\n" +
                        "!babyDik\n" +
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
                        "!heyJude\n" +
                        "!homie\n" +
                        "!horn\n" +
                        "!horse\n" +
                        "!kirk\n" +
                        "!lag\n" +
                        "!licker\n" +
                        "!lilBitchAss\n" +
                        "!magicResit\n" +
                        "!meetYa\n" +
                        "!monkeyDik\n" +
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
                        ";bus;\n" +
                        ";butterfly;\n" +
                        ";coggers;\n" +
                        ";crabPls;\n" +
                        ";dance;\n" +
                        ";pepeD;\n" +
                        ";pepegaPls;\n" +
                        ";pepeGun;\n" +
                        ";pepeJam;\n" +
                        ";pepeWave;\n" +
                        ";pepoBed;\n" +
                        ";pepoDance;\n" +
                        ";pepoLeave;\n" +
                        ";pepoLeaveC;\n" +
                        ";pepoRun;\n" +
                        ";pepoSabers;\n" +
                        ";pepoSalami;\n" +
                        ";pepoShut;\n" +
                        ";ppHop;\n" +
                        ";rainbowWeeb;\n" +
                        ";schubertWalk;\n" +
                        ";triKool;\n" +
                        "```";


                event.getAuthor().openPrivateChannel().queue((channel) -> channel.sendMessage(helpMessage).queue());

            }

            if (voiceChannel != null) {

                if ("!ahhha".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !ahhha");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "ahhha.mp3", 0);
                }
                if ("!arams".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !arams");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "arams.mp3", 0);
                }
                if ("!auPhau".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !auPhau");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "auPhau.mp3", 0);
                }
                if ("!babyDik".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !babyDik");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "babyDik.mp3", 0);
                }
                if ("!boutTime".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !boutTime");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "boutTime.mp3", 0);
                }
                if ("!celsoHere".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !celsoHere");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "celsoHere.mp3", 0);
                }
                if ("!ckelso".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !ckelso");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "ckelso.mp3", 0);
                }
                if ("!clap".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !arams");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "clap.mp3", 0);
                }
                if ("!clickyBoi".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !arams");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "clickyBoi.mp3", 0);
                }
                if ("!croissant".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !croissant");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "croissant.mp3", 0);
                }
                if ("!dickHer".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !dickHer");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "dickHer.mp3", 0);
                }
                if ("!dumbassGame".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !dumbassGame");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "dumbassGame.mp3", 0);
                }
                if ("!fortFucker".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !fortFucker");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "fortFucker.mp3", 0);
                }
                if ("!goldfish".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !goldfish");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "goldfish.mp3", 0);
                }
                if ("!heyJude".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !heyJude");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "heyJude.mp3", 0);
                }
                if ("!homie".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !homie");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "homie.mp3", 0);
                }
                if ("!horn".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !horn");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "horn.mp3", 0);
                }
                if ("!horse".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !horse");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "horse.mp3", 0);
                }
                if ("!kirk".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !kirk");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "kirk.mp3", 0);
                }
                if ("!lag".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !lag");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "lag.mp3", 0);
                }
                if ("!licker".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !licker");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "licker.mp3", 0);
                }
                if ("!lilBitchAss".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !lilBitchAss");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "lilBitchAss.mp3", 0);
                }
                if ("!magicResist".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !magicResist");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "magicResist.mp3", 0);
                }
                if ("!meetYa".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !meetYa");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "meetYa.mp3", 0);
                }
                if ("!monkeyDik".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !monkeyDik");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "monkeyDik.mp3", 0);
                }
                if ("!monkeys".equalsIgnoreCase(commandList[0])) {
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
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "monkeys.mp3", 0);
                }
                if ("!oil".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !oil");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "oil.mp3", 0);
                }
                if (event.getAuthor().getIdLong() != 93140127949287424L && "!ripDoggo".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !ripDoggo");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "ripDoggo.mp3", 0);
                }
                if ("!sameGame".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !sameGame");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "sameGame.mp3", 0);
                }
                if ("!snap".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !snap");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "snap.mp3", 0);
                }
                if ("!sweetheart".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !sweetheart");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "sweetheart.mp3", 0);
                }
                if ("!tinsel".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !tinsel");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "tinsel.mp3", 0);
                }
                if ("!warus".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !warus");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "warus.mp3", 0);
                }
                if ("!watch".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !watch");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "watch.mp3", 0);
                }
                if ("!weeee".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !weeee");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "weeee.mp3", 0);
                }
                if ("!yooo".equalsIgnoreCase(commandList[0])) {
                    log.info("User: " + event.getAuthor().getName() + " Command: !yooo");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, githubAudioBaseURL + "yooo.mp3", 0);
                }
                
                if ("~play".equals(commandList[0]) && commandList.length >= 2) {
                    log.info("User: " + event.getAuthor().getName() + " Command: ~play");
                    event.getMessage().delete().queue();
                    loadAndPlay(event.getTextChannel(), voiceChannel, commandList[1], trackPosition);
                } else if ("~skip".equals(commandList[0]) && (botSettings.getSkipMode() || event.getAuthor().getIdLong() == 148630426548699136L)) {
                    log.info("User: " + event.getAuthor().getName() + " Command: ~skip");
                    event.getMessage().delete().queue();
                    skipTrack(event.getTextChannel());
                } else if ("~leave".equals(commandList[0]) && (botSettings.getSkipMode() || event.getAuthor().getIdLong() == 148630426548699136L)) {
                    guild.getAudioManager().closeAudioConnection();
                }

            }

            if (";bus;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;bus;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "bus", githubImageBaseURL + "bus.gif");
            }
            if (";butterfly;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;butterfly;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "butterfly", githubImageBaseURL + "butterfly.gif");
            }
            if (";coggers;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;coggers;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "coggers", githubImageBaseURL + "coggers.gif");
            }
            if (";crabPls;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;crabPls;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "crabPls", githubImageBaseURL + "crabPls.gif");
            }
            if (";dance;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;dance;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "dance", githubImageBaseURL + "dance.gif");
            }
            if (";pepeD;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepeD;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepeD", githubImageBaseURL + "pepeD.gif");
            }
            if (";pepegaPls;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepegaPls;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepegaPls", githubImageBaseURL + "pepegaPls.gif");
            }
            if (";pepeGun;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepeGun;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepeGun", githubImageBaseURL + "pepeGun.gif");
            }
            if (";pepeJam;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepeJam;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepeJam", githubImageBaseURL + "pepeJam.gif");
            }
            if (";pepeWave;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepeWave;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepeWave", githubImageBaseURL + "pepeWave.gif");
            }
            if (";pepoBed;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepoBed;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoBed", githubImageBaseURL + "pepoBed.gif");
            }
            if (";pepoDance;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepoDance;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoDance", githubImageBaseURL + "pepoDance.gif");
            }
            if (";pepoLeave;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepoLeave;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoLeave", githubImageBaseURL + "pepoLeave.gif");
            }
            if (";pepoLeaveC;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepoLeaveC;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoLeaveC", githubImageBaseURL + "pepoLeaveC.gif");
            }
            if (";pepoRun;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepoRun;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoRun", githubImageBaseURL + "pepoRun.gif");
            }
            if (";pepoSabers;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepoSabers;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoSabers", githubImageBaseURL + "pepoSabers.gif");
            }
            if (";pepoSalami;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepoSalami;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoSalami", githubImageBaseURL + "pepoSalami.gif");
            }
            if (";pepoShut;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;pepoShut;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "pepoShut", githubImageBaseURL + "pepoShut.gif");
            }
            if (";ppHop;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;ppHop;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "ppHop", githubImageBaseURL + "ppHop.gif");
            }
            if (";rainbowWeeb;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;rainbowWeeb;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "rainbowWeeb", githubImageBaseURL + "rainbowWeeb.gif");
            }
            if (";schubertWalk;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;schubertWalk;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "schubertWalk", githubImageBaseURL + "schubertWalk.gif");
            }
            if (";triKool;".equalsIgnoreCase(commandList[0])) {
                log.info("User: " + event.getAuthor().getName() + " Command: ;triKool;");
                event.getMessage().delete().queue();
                sendEmote(guild.getDefaultChannel(), "triKool", githubImageBaseURL + "triKool.gif");
            }

        }

        super.onMessageReceived(event);
    }

    private void loadAndPlay(final TextChannel channel, final VoiceChannel voiceChannel, final String trackUrl, final Integer trackPosition) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
//                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();
                if (trackPosition != 0) {
                    track.setPosition(1000 * trackPosition);
                }
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

    private static void sendEmbed(TextChannel channel, String authorName, String authorAvatarUrl, String emoteUrl) {

        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(new Color(0x4AF458));
        eb.setAuthor(authorName, null, authorAvatarUrl);
        eb.setImage(emoteUrl);

        channel.sendMessage(eb.build()).queue();

    }

    private static void createTimer(Guild guild, TextChannel textChannel, String[] commandList) throws InterruptedException {

        String userId = "";
        Integer timerLength = 0;

        for (String command: commandList) {

            if (command.contains("t=")) {
                timerLength = new Integer(command.substring(command.lastIndexOf("t=") + 2));
                log.info("timerLength: " + timerLength);
            }
            if (command.matches("^<@\\d*>")) {
                userId = command.substring(2, command.length() - 1);
                log.info("userId: " + userId);
            }

        }

        if (!userId.equalsIgnoreCase("") && !timerLength.equals(0)) {

            log.info("Creating Timer Thread");
            Thread thread = new Thread(new TimerRunnable(timerLength, new Long(userId)));
            thread.start();
            thread.join();

        }

    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {

        Guild guild = event.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        Long userMoveId = event.getMember().getUser().getIdLong();

        if (audioManager.isConnected() && botSettings.getVoiceFollowMode() && (userMoveId == botSettings.getVoiceFollowUserId())) {

            VoiceChannel channelJoined = event.getChannelJoined();
            guild.moveVoiceMember(guild.getMemberById(469639004078211072L), channelJoined);

        }

    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {

        Guild guild = event.getGuild();
        AudioManager audioManager = guild.getAudioManager();
        Long userMoveId = event.getMember().getUser().getIdLong();

        if (audioManager.isConnected() && botSettings.getVoiceFollowMode() && (userMoveId == botSettings.getVoiceFollowUserId())) {

            VoiceChannel channelJoined = event.getChannelJoined();
            guild.moveVoiceMember(guild.getMemberById(469639004078211072L), channelJoined);

        }

        Long ckelsoId = new Long("93105200365043712");
        Long derpIsland = new Long("93546382438174720");

        if (botSettings.getVoiceJoinActions()) {

            if (event.getMember().getUser().getIdLong() == derpIsland) {
                log.info("Will Joined a VoiceChannel");
                loadAndPlay(guild.getDefaultChannel(), event.getChannelJoined(), githubAudioBaseURL + "homie.mp3", 0);
            } else if (event.getMember().getUser().getIdLong() == ckelsoId) {
                log.info("Kelso Joined a VoiceChannel");
                loadAndPlay(guild.getDefaultChannel(), event.getChannelJoined(), githubAudioBaseURL + "celsoHere.mp3", 0);
            }

        }

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

    }
}
