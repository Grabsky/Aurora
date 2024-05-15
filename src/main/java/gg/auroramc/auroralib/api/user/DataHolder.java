package gg.auroramc.auroralib.api.user;

import gg.auroramc.auroralib.api.util.NamespacedId;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

public interface DataHolder {
    NamespacedId getId();
    void serializeInto(ConfigurationSection data);
    void initFrom(@Nullable ConfigurationSection data);
    boolean isDirty();
}
