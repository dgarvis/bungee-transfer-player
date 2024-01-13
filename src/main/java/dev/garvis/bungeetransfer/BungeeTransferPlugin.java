package dev.garvis.bungeetransfer;

import dev.garvis.bungeetransfer.KafkaManager;
import dev.garvis.bungeetransfer.Events;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.File;

public class BungeeTransferPlugin extends Plugin {

    private KafkaManager kafka = new KafkaManager();
    private boolean running = true;
    
    @Override
    public void onEnable() {
	try {
	    makeConfig();
	} catch (IOException e) {
	    getLogger().warning("Could not create default config.");
	}

	this.attemptToConnectToKafka();
	
	//for (Map.Entry<String,ServerInfo> entry : getProxy().getServers().entrySet()) {
	//    getLogger().info(entry.getKey());
	//}
	//getProxy().getPluginManager().registerListener(this, new Events(this));
	
	getLogger().info("onEnable");
    }
        
    @Override
    public void onDisable() {
	this.running = false;
	getLogger().info("onDisable");
    }

    private void attemptToConnectToKafka() {
	if (!this.running) return;

	final String[] events = {
	    "TRANSFER_PLAYER"
	};

	Configuration config = loadConfig();
	if (config == null ||
	    config.getString("kafkaServer").isEmpty() ||
	    config.getString("kafkaName").isEmpty() ||
	    config.getString("kafkaTopic").isEmpty() ||
	    config.getString("kafkaGroup").isEmpty()) {
	    
	    getLogger().warning("Kafka connection not configured.");
	    getProxy().getScheduler().schedule(this, () -> {
		    attemptToConnectToKafka();
		}, 60, TimeUnit.SECONDS); 
	}

	try {
	    kafka.connect(config.getString("kafkaName"),
			  config.getString("kafkaServer"),
			  config.getString("kafkaTopic"),
			  new String[]{config.getString("kafkaTopic")},
			  events,
			  (LinkedList<KafkaManager.Message> messages) -> {
			      for (KafkaManager.Message message : messages)
				  this.processMessage(message);
			  });
	    getLogger().info("Connected to Kafka");
	} catch (Exception e) {
	    getLogger().warning("Not connected to kafka, check plugin config.");
	    getProxy().getScheduler().schedule(this, () -> {
		    attemptToConnectToKafka();
		}, 60, TimeUnit.SECONDS); 	    
	}
	    
    }

    private void processMessage(KafkaManager.Message message) {
	System.out.println("Got Message: " + message.toString());

	switch ((String)message.get("eventType")) {
	case "TRANSFER_PLAYER":
	    // TODO - should really send the player a message and let them click to
	    // confirm the transfer

	    // Get World
	    ServerInfo world = getProxy().getServers().get((String) message.get("world"));

	    // Get Player
	    ProxiedPlayer player = getProxy().getPlayer((String) message.get("playerName"));

	    // Move the player
	    if (world != null && player != null)
		player.connect(world);
		
	    break;
	}
    }

    private Configuration loadConfig() {
	try {
	    return ConfigurationProvider.getProvider(YamlConfiguration.class)
		.load(new File(getDataFolder(), "config.yml"));
	} catch (IOException e) {
	    getLogger().warning("Could not load config.");
	    return null;
	}
    }

    private void makeConfig() throws IOException {
	// Create plugin config folder if it doesn't exist
	if (!getDataFolder().exists()) {
	    getLogger().info("Created config folder: " + getDataFolder().mkdir());
	}
	
	File configFile = new File(getDataFolder(), "config.yml");

	// Copy default config if it doesn't exist
	if (!configFile.exists()) {
	    FileOutputStream outputStream = new FileOutputStream(configFile); // Throws IOException
	    InputStream in = getResourceAsStream("config.yml"); // This file must exist in the jar resources folder
	    in.transferTo(outputStream); // Throws IOException
	}
    }
}
