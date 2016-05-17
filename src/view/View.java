package view;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.bucket.BucketManager;
import com.couchbase.client.java.view.DefaultView;
import com.couchbase.client.java.view.DesignDocument;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;

public class View {
	Cluster cluster;
	BucketManager bucketManager;
	Bucket bucket;
	public void getConnection(){
		cluster = CouchbaseCluster.create("192.168.0.21");
		bucket = cluster.openBucket("test", 60, TimeUnit.SECONDS);
		bucketManager = bucket.bucketManager();
	}
	public void createView(){
		DesignDocument designDoc = DesignDocument.create("persontest",
				Arrays.asList(
						DefaultView.create("by_name",
							"function (doc, meta) { emit(meta.id, [doc.firstname, doc.age]); }", "_count")
						));
		bucketManager.insertDesignDocument(designDoc, true);
	}
	public void getView(){
		DesignDocument designDoc = bucketManager.getDesignDocument("persontest", true);
		System.out.println(designDoc.name() + " has " + designDoc.views().size() + " views");
		List<DesignDocument> designDocs = bucketManager.getDesignDocuments(true);
		System.out.println("bucket 'test' has " + designDocs.size() + " design documents:");
		for (DesignDocument doc : designDocs) {
			System.out.println(doc.name() + " has " + doc.views().size() + " views");
		}
	}
	public void deleteView(){
		bucketManager.removeDesignDocument("persontest");
		bucketManager.removeDesignDocument("persontest",true);
	}
	private void updateView() {
		DesignDocument designDoc = bucketManager.getDesignDocument("persontest", true);
	
		designDoc.views().add(
			DefaultView.create("by_name", 
					"function (doc, meta) { emit(meta.id, [doc.lastname, doc.age]); }", "_count")
			);
		bucketManager.upsertDesignDocument(designDoc, true);
	}
	public void queryView(){
		ViewResult result = bucket.query(ViewQuery.from("persontest", "by_name").development(true));
		for (ViewRow row : result) {
		    System.out.println(row);
		}
	}
	public static void main(String args[]) {
		View view = new View();
		view.getConnection();
		view.getView();
		view.updateView();
		view.queryView();
		view.cluster.disconnect();
	}
}