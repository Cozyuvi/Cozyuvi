import com.mongodb.client.*;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import java.util.concurrent.TimeUnit;



public class MongoDBHelper {
    private MongoClient mongoClient;
    public MongoDatabase database;


    public MongoDBHelper() {
        // Replace <username>, <password>, and <cluster-url> with your actual details
        String uri = "your uri";
        mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase("HostelManagemant");
    }
    public void ensureMessTTLIndex() {
        MongoCollection<Document> collection = database.getCollection("mess");
        IndexOptions options = new IndexOptions().expireAfter(86400L, TimeUnit.SECONDS); // 24 hours
        collection.createIndex(new Document("createdAt", 1), options);
    }
    public MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    public void insertUser(String collectionName, String name, String password) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document user = new Document("name", name)
                .append("password", password);
        collection.insertOne(user);
        System.out.println("User inserted into '" + collectionName + "' collection.");
    }

    public void deleteUser(String collectionName, String name) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.deleteOne(eq("name", name));
        System.out.println("User deleted from '" + collectionName + "' collection.");
    }

    public void updateUserPassword(String collectionName, String name, String newPassword) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.updateOne(eq("name", name), new Document("$set", new Document("password", newPassword)));
        System.out.println("User password updated in '" + collectionName + "' collection.");
    }

    public void fetchUser(String collectionName, String name) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document user = collection.find(eq("name", name)).first();

        if (user != null) {
            System.out.println("User found: " + user.toJson());
        } else {
            System.out.println("User not found in '" + collectionName + "' collection.");
        }
    }
    public Document getDocumentByFields(String collectionName, String field1, String value1, String field2, String value2) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document query = new Document(field1, value1).append(field2, value2);
        return collection.find(query).first();
    }


    public void close() {
        mongoClient.close();
    }
}
