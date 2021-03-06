package io.github.gronnmann.coinflipper;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import io.github.gronnmann.utils.coinflipper.Debug;

public class ConfigManager {
	private ConfigManager(){}
	private static ConfigManager mng = new ConfigManager();
	public static ConfigManager getManager(){return mng;}
	
	private Plugin pl;
	private File configF, messagesF, statsF, betsF, mysqlF, materialsF;
	private FileConfiguration config, messages, stats, bets, mysql, materials;
	
	public void setup(Plugin p){
		
		this.pl = p;
		
		//Config
		configF = new File(p.getDataFolder(), "config.yml");
		if (!configF.exists()){
			p.saveDefaultConfig();
			config = YamlConfiguration.loadConfiguration(configF);
			
			String packageName = Bukkit.getServer().getClass().getPackage().getName();
			int vID = Integer.parseInt(packageName.split("_")[1]);
			if (vID < 9){
				config.set("sound_while_choosing", "CLICK");
				config.set("sound_winner_chosen", "FIREWORK_BLAST");
			}
			
			
			this.saveConfig();
			
		}else{
			config = YamlConfiguration.loadConfiguration(configF);
		}
		
		
		
		//Messages
		messagesF = new File(p.getDataFolder(), "messages.yml");
		if (!messagesF.exists()){
			try{
				messagesF.createNewFile();
				messages = YamlConfiguration.loadConfiguration(messagesF);
				this.copyDefaults(messages, "/messages.yml");
				this.saveMessages();
			}catch(Exception e){e.printStackTrace();}
		}else{
			messages = YamlConfiguration.loadConfiguration(messagesF);
		}
		
		statsF = new File(p.getDataFolder(), "stats.yml");
		if (!statsF.exists()){
			try {
				statsF.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		stats = YamlConfiguration.loadConfiguration(statsF);
		
		betsF = new File(p.getDataFolder(), "bets.yml");
		if (!betsF.exists()){
			try {
				betsF.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		bets = YamlConfiguration.loadConfiguration(betsF);
		
		mysqlF = new File(p.getDataFolder(), "mysql.yml");
		if (!mysqlF.exists()){
			try{
				mysqlF.createNewFile();
				mysql = YamlConfiguration.loadConfiguration(mysqlF);
				this.copyDefaults(mysql, "/mysql.yml");
				this.saveMySQL();
			}catch(Exception e){e.printStackTrace();}
		}else{
			mysql = YamlConfiguration.loadConfiguration(mysqlF);
		}
		
		materialsF = new File(p.getDataFolder(), "materials.yml");
		if (!materialsF.exists()){
			try{
				materialsF.createNewFile();
				materials = YamlConfiguration.loadConfiguration(materialsF);
				this.copyDefaults(materials, "/materials.yml");
				this.saveMySQL();
			}catch(Exception e){e.printStackTrace();}
		}else{
			materials = YamlConfiguration.loadConfiguration(materialsF);
		}
		
	}
	
	public void configUpdate(){
		
		
		
		String ver = pl.getDescription().getVersion();
		double pluginVer = Double.parseDouble(ver);
		double configVer = config.getDouble("config_version");
		
		Debug.print("Current plugin version: " + pluginVer + ", Current config version: " + configVer);
		
		if (pluginVer > configVer){
			try{
				InputStream newConfigStream = pl.getClass().getResourceAsStream("/config.yml");
				
				if (newConfigStream == null)return;
				
				InputStreamReader newConfigStreamReader = new InputStreamReader(newConfigStream);
				
				FileConfiguration newConfig = YamlConfiguration.loadConfiguration(newConfigStreamReader);
				
				System.out.println("[CoinFlipper] Old config found. Updating...");
				
				for (String field : newConfig.getConfigurationSection("").getKeys(false)){
					if (config.get(field) == null){
						config.set(field, newConfig.get(field));
						System.out.println("[CoinFlipper] Adding field '" + field + "' with value '" + newConfig.get(field) + "'");
					}
				}
				
				config.set("config_version", Double.parseDouble(pl.getDescription().getVersion()));
				
				this.saveConfig();
				
				newConfigStreamReader.close();
				newConfigStream.close();
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	
	//Copy default option
	public void copyDefaults(FileConfiguration file, String resource){
		InputStream str;
		file.options().copyDefaults(true);
		try{
			str = pl.getClass().getResourceAsStream(resource);
			InputStreamReader strR = new InputStreamReader(str);
			FileConfiguration defaults = YamlConfiguration.loadConfiguration(strR);
			file.setDefaults(defaults);
			str.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
	public FileConfiguration getConfig(){return config;}
	public FileConfiguration getMessages(){return messages;}
	public FileConfiguration getStats(){return stats;}
	public FileConfiguration getBets() {return bets;}
	public FileConfiguration getMySQL(){return mysql;}
	public FileConfiguration getMaterials(){return materials;}
	
	public void saveConfig(){
		try{
			config.save(configF);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void saveMessages(){
		try{
			messages.save(messagesF);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void saveStats(){
		try{
			stats.save(statsF);
		}catch(Exception e){e.printStackTrace();}
	}
	public void saveBets(){
		try{
			bets.save(betsF);
		}catch(Exception e){e.printStackTrace();}
	}
	public void saveMySQL(){
		try{
			mysql.save(mysqlF);
		}catch(Exception e){e.printStackTrace();}
	}
	public void saveMaterials(){
		try{
			materials.save(materialsF);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public void reload(){
		config = YamlConfiguration.loadConfiguration(configF);
		messages = YamlConfiguration.loadConfiguration(messagesF);
		stats = YamlConfiguration.loadConfiguration(statsF);
		bets = YamlConfiguration.loadConfiguration(betsF);
		mysql = YamlConfiguration.loadConfiguration(mysqlF);
		materials = YamlConfiguration.loadConfiguration(materialsF);
	}
}
