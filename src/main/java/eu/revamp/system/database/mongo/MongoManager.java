package eu.revamp.system.database.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import eu.revamp.system.utilities.Manager;
import eu.revamp.system.utilities.chat.Color;
import eu.revamp.system.utilities.file.ConfigFile;
import lombok.Getter;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.enums.Language;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class MongoManager extends Manager {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> documentation,
            ranks,
            globalCooldowns,
            bans,
            mutes,
            kicks,
            warns,
            blacklists,
            punishPlayerData,
            tags,
            punishHistory,
            notes;

    private final ConfigFile configFile = plugin.getDataBase();

    public MongoManager(RevampSystem plugin) {
        super(plugin);
    }

    @SuppressWarnings("deprecation")
    public boolean connect() {
        Logger.getLogger("org.mongodb.driver").setLevel(Level.OFF);
        try {
            if (this.configFile.getBoolean("MONGODB.AUTHENTICATION.ENABLED")) {
                MongoCredential credential = MongoCredential.createCredential(
                        this.configFile.getString("MONGODB.AUTHENTICATION.USERNAME"),
                        this.configFile.getString("MONGODB.AUTHENTICATION.DATABASE"),
                        this.configFile.getString("MONGODB.AUTHENTICATION.PASSWORD").toCharArray()
                );

                mongoClient = new MongoClient(new ServerAddress(this.configFile.getString("MONGODB.ADDRESS"),
                        this.configFile.getInt("MONGODB.PORT")), Collections.singletonList(credential));
            } else {
                mongoClient = new MongoClient(this.configFile.getString("MONGODB.ADDRESS"),
                        this.configFile.getInt("MONGODB.PORT"));
            }
            mongoDatabase = mongoClient.getDatabase(this.configFile.getString("MONGODB.DATABASE"));
            documentation = mongoDatabase.getCollection("Revamp-Documentation");
            ranks = mongoDatabase.getCollection("Revamp-Ranks");
            tags = mongoDatabase.getCollection("Revamp-Tags");
            globalCooldowns = mongoDatabase.getCollection("Revamp-GCooldowns");
            bans = mongoDatabase.getCollection("Revamp-Bans");
            mutes = mongoDatabase.getCollection("Revamp-Mutes");
            kicks = mongoDatabase.getCollection("Revamp-Kicks");
            warns = mongoDatabase.getCollection("Revamp-Warns");
            blacklists = mongoDatabase.getCollection("Revamp-Blacklists");
            punishPlayerData = mongoDatabase.getCollection("Revamp-Data");
            punishHistory = mongoDatabase.getCollection("Revamp-PunishHistory");
            notes = mongoDatabase.getCollection("Revamp-Notes");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(Color.translate(Language.PREFIX + "&cDisabling Revamp System due to issues with mongo database."));
            Bukkit.getServer().getPluginManager().disablePlugin(this.plugin);
            return false;
        }
    }
}
