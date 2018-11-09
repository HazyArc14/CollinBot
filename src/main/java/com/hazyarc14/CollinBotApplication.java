package com.hazyarc14;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.events.TrackFinishEvent;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
public class CollinBotApplication {

    private static final Logger log = LoggerFactory.getLogger(CollinBotApplication.class);

    // The token that the bot will use.
    private static final String TOKEN = "NDY5NjM5MDA0MDc4MjExMDcy.DsXc0Q.1yfBO_ENQGZE0tpA1Ep4thzKrkA";
    // The prefix that the bot will use.
    private static final String PREFIX = "!";

    private static IDiscordClient client;

    public static void main(String[] args) throws DiscordException, RateLimitException {
        System.out.println("Logging bot in...");
        client = new ClientBuilder().withToken(TOKEN).build();
        client.getDispatcher().registerListener(new CollinBotApplication());
        client.login();
        SpringApplication.run(CollinBotApplication.class, args);
    }

    @EventSubscriber
    public void onMessage(TrackFinishEvent event) {
        if (event.getPlayer().getPlaylistSize() == 0) {
            IVoiceChannel voiceChannel = event.getPlayer().getGuild().getConnectedVoiceChannel();
            voiceChannel.leave();
        }
    }

    @EventSubscriber
    public void onMessage(MessageReceivedEvent event) throws RateLimitException, DiscordException,
            MissingPermissionsException, IOException, UnsupportedAudioFileException {

        IMessage message = event.getMessage();
        IChannel channel = message.getChannel();
        IUser user = message.getAuthor();
        IGuild guild = message.getGuild();
        String content = message.getContent();

        // Make sure the message starts with the prefix
        if (content.startsWith(PREFIX)) {

            String[] split = content.split(" ");
            String alias = split[0].replaceFirst(PREFIX, "");
            String[] args = Arrays.copyOfRange(split, 1, split.length);

            IVoiceChannel voiceChannel = null;
            if (args.length != 0) {
                voiceChannel = guild.getVoiceChannelByID(new Long(args[0]));
            } else if (user.getVoiceStateForGuild(guild).getChannel() != null) {
                voiceChannel = user.getVoiceStateForGuild(guild).getChannel();
            }

            if (voiceChannel != null) {

                if (guild.getConnectedVoiceChannel() == null) {
                    join(voiceChannel);
                }

                try {

                    if (alias.equalsIgnoreCase("aram")) {
                        message.delete();
                        ClassPathResource audioFile = new ClassPathResource("audio/aram.mp3");
                        queueFile(channel, audioFile.getFile());
                    }

                } catch (FileNotFoundException e) {
                    log.error("Error with finding the file :: ", e);
                    leave(voiceChannel);
                }

            }

        }
    }

    private void join(IVoiceChannel voiceChannel) throws RateLimitException, DiscordException, MissingPermissionsException {
        voiceChannel.join();
    }

    private void leave(IVoiceChannel voiceChannel) throws RateLimitException, DiscordException, MissingPermissionsException {
        voiceChannel.leave();
    }

    private void queueFile(IChannel channel, File file) throws RateLimitException, DiscordException,
            MissingPermissionsException, IOException, UnsupportedAudioFileException {
        getPlayer(channel.getGuild()).queue(file);
    }

    private AudioPlayer getPlayer(IGuild guild) {
        return AudioPlayer.getAudioPlayerForGuild(guild);
    }

}