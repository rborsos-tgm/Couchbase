package crud;

import java.util.concurrent.TimeUnit;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

public class CRUD {
	public static void main(String args[]) {
		//initial connection to the cluster
		Cluster cluster = CouchbaseCluster.create("192.168.0.21");
		//retrieving the data bucket
		Bucket bucket = cluster.openBucket("test", 60, TimeUnit.SECONDS);
		
		//creating a new json object
		JsonObject person1 = JsonObject.empty()
				.put("firstname", "Robert")
				.put("lastname", "Borsos")
				.put("sex", "m")
				.put("age", 17);
		JsonDocument doc1 = JsonDocument.create("rborsos", person1);
		JsonDocument response = bucket.upsert(doc1);
		
		JsonObject person2 = JsonObject.empty()
				.put("firstname", "Patrick")
				.put("lastname", "Kocsis")
				.put("sex", "m")
				.put("age", 19);
		JsonDocument doc2 = JsonDocument.create("pkoscis", person2);
		JsonDocument response2 = bucket.upsert(doc2);
		
		JsonDocument rborsos = bucket.get("rborsos");
		if (rborsos == null) {
			System.err.println("Document not found!");
		} else {
			System.out.println("Age: " + rborsos.content().getInt("age"));
			rborsos.content().put("age", 18);
			System.out.println("Age: " + rborsos.content().getInt("age"));
			JsonDocument updated = bucket.replace(rborsos);
		}
		
		JsonDocument pkoscis = bucket.get("pkoscis");
		if (pkoscis == null) {
			System.err.println("Document not found!");
		} else {
			System.out.println("Last name: " + pkoscis.content().getString("lastname"));
			pkoscis.content().put("lastname", "Borsos");
			System.out.println("Last name: " + pkoscis.content().getString("lastname"));
			JsonDocument updated = bucket.replace(pkoscis);
		}
		//deleting a json document
//		bucket.remove("pkoscis");
		//clsong the connection
		cluster.disconnect();
	}
}