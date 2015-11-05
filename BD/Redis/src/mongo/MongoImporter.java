package src.mongo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.bson.Document;
import org.json.simple.parser.JSONParser;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

public class MongoImporter {

	public static void main(String[] args) throws IOException {
		System.out.println("Verbinde zu Redis auf localhost:6379");
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		MongoDatabase database = mongoClient.getDatabase("plz");

		MongoCollection<Document> collection = database.getCollection("plz");
		// clear collection
		collection.deleteMany(new Document());
	
		BufferedReader b = new BufferedReader(new FileReader("data/plz.data"));
		long lineCounter = 0;
		String line = b.readLine();
		do {
			try {
				DBObject dbObject = (DBObject) JSON.parse(line);
				collection.insertOne(new Document(dbObject.toMap()));
				if (++lineCounter % 50 == 0)
					System.out.println(lineCounter + " Zeilen hinzugef�gt. ");
			} catch (Exception e) {
				System.out.println("Zeile die einen Fehler geschmissen hat: " + line);
				e.printStackTrace();
			}
			line = b.readLine();
		} while (line != null);

		b.close();
		mongoClient.close();
		System.out.println("Der Import ist fertig");
	}

}
