import com.mongodb.client.*;
import org.bson.Document;

public class MongoStandaloneTest {
    public static void main(String[] args) {
        // Replace these with your actual values
        String uri = "mongodb+srv://imayavaramban2124:RcUPWgpaagQTBYqo@cluster0.qfyfrr0.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
        String dbName = "rapido";
        String collectionName = "drivers";

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase(dbName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            FindIterable<Document> documents = collection.find();
            System.out.println("Documents in collection:");
            for (Document doc : documents) {
                System.out.println(doc.toJson());
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
