package dev.garvis.bungeetransfer;

import dev.garvis.bungeetransfer.Events;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import net.md_5.bungee.api.config.ServerInfo;

import java.util.Map;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.File;

public class BungeeTransferPlugin extends Plugin {

    
    @Override
    public void onEnable() {
	try {
	    makeConfig();
	} catch (IOException e) {
	    getLogger().warning("Could not create default config.");
	}

	Configuration config;
	try {
	    config = ConfigurationProvider.getProvider(YamlConfiguration.class)
		.load(new File(getDataFolder(), "config.yml"));
	} catch (IOException e) {
	    getLogger().warning("Could not load config.");
	    return;
	}

	// TODO
	for (Map.Entry<String,ServerInfo> entry : getProxy().getServers().entrySet()) {
	    getLogger().info(entry.getKey());
	}
	getProxy().getPluginManager().registerListener(this, new Events(this));
	
	getLogger().info("onEnable");
    }

    public void makeConfig() throws IOException {
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
