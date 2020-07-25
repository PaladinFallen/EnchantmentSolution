package org.ctp.enchantmentsolution.utils.config;

import java.io.File;

import org.ctp.enchantmentsolution.EnchantmentSolution;
import org.ctp.enchantmentsolution.crashapi.config.Configuration;
import org.ctp.enchantmentsolution.crashapi.config.Language;
import org.ctp.enchantmentsolution.crashapi.config.yaml.YamlConfigBackup;
import org.ctp.enchantmentsolution.crashapi.db.BackupDB;
import org.ctp.enchantmentsolution.utils.Configurations;
import org.ctp.enchantmentsolution.utils.files.ESLanguageFile;

public class LanguageConfiguration extends Configuration {

	private ESLanguageFile language;

	public LanguageConfiguration(File file, String languageFile, ESLanguageFile language, BackupDB db) {
		super(EnchantmentSolution.getPlugin(), new File(file + "/" + languageFile), db, false);

		this.language = language;

		setDefaults();
		migrateVersion();
		save();
	}

	@Override
	public void setDefaults() {
		YamlConfigBackup config = getConfig();
		config.addDefault("starter", "&8[&dEnchantment Solution&8]");
		config.copyDefaults(language.getConfig());

		config.writeDefaults();
	}

	@Override
	public void migrateVersion() {
		Configurations config = Configurations.getConfigurations();
		if (config.getConfig().getString("starter") != null) {
			getConfig().set("starter", config.getConfig().getString("starter"));
			config.getConfig().getConfig().removeKey("starter");
			config.getConfig().save();
		}
	}

	@Override
	public void repairConfig() {}

	public Language getLanguage() {
		return language.getLanguage();
	}

}
