package gg.auroramc.aurora.api.config;

import gg.auroramc.aurora.Aurora;
import gg.auroramc.aurora.api.config.decorators.IgnoreField;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class AuroraConfig {
    @IgnoreField
    private final File file;

    @IgnoreField
    private final YamlConfiguration rawConfiguration;

    @Getter
    private int configVersion = 0;

    public AuroraConfig(File file) {
        this.file = file;
        this.rawConfiguration = new YamlConfiguration();

        try {
            this.rawConfiguration.load(file);

            var migrationSteps = getApplicableMigrationSteps(rawConfiguration.getInt("config-version", 0));

            for (var migration : migrationSteps) {
                migration.accept(rawConfiguration);
            }

            if (!migrationSteps.isEmpty()) {
                try {
                    rawConfiguration.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            Aurora.logger().severe("Config file not found: " + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            Aurora.logger().severe("Invalid configuration in file: " + file.getName());
            e.printStackTrace();
        }
    }

    private List<Consumer<YamlConfiguration>> getApplicableMigrationSteps(int from) {
        if (getMigrationSteps().size() < from) return List.of();
        return getMigrationSteps().subList(from, getMigrationSteps().size());
    }

    protected List<Consumer<YamlConfiguration>> getMigrationSteps() {
        return List.of();
    }

    public void saveChanges() {
        ConfigManager.save(this, rawConfiguration, file);
    }

    public CompletableFuture<Void> saveChangesAsync() {
        return CompletableFuture.runAsync(() -> {
            ConfigManager.save(this, rawConfiguration, file);
        });
    }

    public YamlConfiguration getRawConfig() {
        return rawConfiguration;
    }

    public void load() {
        ConfigManager.load(this, rawConfiguration);
    }
}
