package com.georgev22.library.minecraft.configmanager;

import com.georgev22.library.minecraft.BukkitMinecraftUtils;
import com.google.common.collect.Sets;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public final class CFG {

    private final static Set<CFG> cachedFiles = Sets.newHashSet();

    public static void reloadFiles() {
        cachedFiles.forEach(CFG::reloadFile);
    }

    /* The plugin instance. */
    private final JavaPlugin plugin;
    /* The file's name (without the .yml) */
    private final String fileName;

    /* The yml file configuration. */
    private FileConfiguration fileConfiguration;
    /* The file. */
    private File file;

    private final boolean saveResource;

    public CFG(final JavaPlugin plugin, final String string, final boolean saveResource) {
        this.plugin = plugin;
        this.fileName = string + ".yml";
        this.saveResource = saveResource;
        this.setup();
        cachedFiles.add(this);
    }

    /**
     * Attempts to load the file.
     *
     * @see #reloadFile()
     * @see JavaPlugin#saveResource(String, boolean)
     */
    public void setup() {
        if (!this.plugin.getDataFolder().exists()) {
            if (this.plugin.getDataFolder().mkdir()) {
                BukkitMinecraftUtils.debug(plugin, "Folder " + this.plugin.getDataFolder().getName() + " has been created!");
            }
        }

        this.file = new File(this.plugin.getDataFolder(), this.fileName);

        if (!this.file.exists()) {
            try {
                if (this.file.createNewFile()) {
                    BukkitMinecraftUtils.debug(plugin, "File " + this.file.getName() + " has been created!");
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            if (saveResource) {
                this.plugin.saveResource(this.fileName, true);
            }
        }

        this.reloadFile();
    }

    /**
     * Saves the file configuration.
     *
     * @see FileConfiguration#save(File)
     * @see #getFileConfiguration()
     */
    public void saveFile() {
        try {
            this.getFileConfiguration().save(this.file);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reloads the file.
     *
     * @see YamlConfiguration#loadConfiguration(File)
     * @see #file
     */
    public void reloadFile() {
        this.fileConfiguration = YamlConfiguration.loadConfiguration(this.file);
    }

    /**
     * @return the file - The {@link FileConfiguration}.
     */
    public FileConfiguration getFileConfiguration() {
        return this.fileConfiguration;
    }

    /**
     * Get the file
     *
     * @return the file
     */
    public File getFile() {
        return file;
    }

    @Override
    public int hashCode() {
        return Objects.hash(plugin, fileName, fileConfiguration, file, saveResource);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CFG cfg = (CFG) o;
        return saveResource == cfg.saveResource && Objects.equals(plugin, cfg.plugin) && Objects.equals(fileName, cfg.fileName) && Objects.equals(fileConfiguration, cfg.fileConfiguration) && Objects.equals(file, cfg.file);
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "CFG{" +
                "plugin=" + plugin +
                ", fileName='" + fileName + '\'' +
                ", fileConfiguration=" + fileConfiguration +
                ", file=" + file +
                ", saveResource=" + saveResource +
                '}';
    }
}