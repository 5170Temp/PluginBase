package dev.isnow.pluginbase.config.impl.database;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.PostProcess;
import dev.isnow.pluginbase.PluginBase;
import dev.isnow.pluginbase.config.MasterConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
public class DatabaseConfig extends MasterConfig {
    @Comment({"", "The host of the database."})
    private String host = "127.0.0.1";
    @Comment("The port of the database.")
    private int port = 3306;
    @Comment({"", "The username of the database."})
    private String username = "root";
    @Comment({"", "The password of the database."})
    private String password = "";
    @Comment({"", "The name of the database."})
    private String database = PluginBase.getInstance().getPluginMeta().getName();

    @Comment({"", "The type of the database.", "Available types: MYSQL, MARIADB, MONGODB, H2,"})
    private DatabaseTypeConfig databaseType = DatabaseTypeConfig.H2;

    public DatabaseConfig() {
        super("database");
    }

    @PostProcess
    private void postProcess() {
        if (databaseType == DatabaseTypeConfig.MONGODB && port == 3306) {
            port = 27017;
        }
    }
}