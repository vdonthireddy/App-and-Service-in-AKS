package com.vijay.vjService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.MediaType;
import org.springframework.util.SystemPropertyUtils;

// import com.squareup.okhttp.MediaType;
// import com.squareup.okhttp.OkHttpClient;
// import com.squareup.okhttp.Request;
// import com.squareup.okhttp.RequestBody;
// import com.squareup.okhttp.Response;
import org.json.JSONObject;
import org.json.JSONException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SpringBootApplication
@RestController
public class GoTApplication {

	@RequestMapping("/")
	String hello() {
		return "Hello Vijay!!!\n";
	}

	@GetMapping(value="/got", produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> got() {

		String ipAddress = "Not Available";
		try {
			InetAddress inet = InetAddress.getLocalHost();
			ipAddress = inet.getHostAddress();
		} catch (UnknownHostException uhe) {
			ipAddress = uhe.getMessage();
		}
		
		HashMap<String, String> map = new HashMap<>();
		map.put("Daenerys Targaryen", "Emilia Clarke");
		map.put("Arya Stark", "Maisie Williams");
		map.put("Sansa Stark", "Sophie Turner");
		map.put("Cersei Lannister", "Lena Headey");
		map.put("IP Address", ipAddress);
		return map;
	 }

	 @GetMapping(value="/secret")
	 public String getSecret() {
		 System.out.println("secret called");
		return getSecretValue();
	 }

	 @GetMapping(value="/db", produces=MediaType.APPLICATION_JSON_VALUE)
	 public Map<String, String> getDb() throws Exception {
		String host = "donpostgres.postgres.database.azure.com";
		String dbName="dondb";
		String dbUser = "vdonthireddy@donpostgres";
		String password = "Nihar007!!!";
		
		try {	
			Class.forName("org.postgresql.Driver");
		}catch(ClassNotFoundException cnfe) {
			throw new ClassNotFoundException("PostgreSQL JDBC driver NOT detected in library path", cnfe);
		}

		System.out.println("Driver detected in the library path");

		Connection connection = null;
		try {
			String url = String.format("jdbc:postgresql://%s/%s", host, dbName);
			Properties properties = new Properties();
			properties.setProperty("user", dbUser);
			properties.setProperty("password", password);
			properties.setProperty("ssl", "false");

			connection = DriverManager.getConnection(url, properties);
		} catch (Exception e) {
			System.out.println("Failed to connection to DB");
		}

		if (connection != null) {
            System.out.println("Successfully created connection to database.");

            // Perform some SQL queries over the connection.
            try
            {
                // Drop previous table of same name if one exists.
                Statement statement = connection.createStatement();
                statement.execute("DROP TABLE IF EXISTS inventory;");
                System.out.println("Finished dropping table (if existed).");

                // Create table.
                statement.execute("CREATE TABLE inventory (id serial PRIMARY KEY, name VARCHAR(50), quantity INTEGER, createdon VARCHAR(50));");
                System.out.println("Created table.");

				SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");
				sdf.setTimeZone(TimeZone.getTimeZone("PST"));
				String timestamp = sdf.format(new Date()) + " PST";

                // Insert some data into table.
                int nRowsInserted = 0;
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO inventory (name, quantity, createdon) VALUES (?, ?, ?);");
                preparedStatement.setString(1, "banana");
				preparedStatement.setInt(2, 150);
				preparedStatement.setString(3, timestamp);
                nRowsInserted += preparedStatement.executeUpdate();

                preparedStatement.setString(1, "orange");
                preparedStatement.setInt(2, 154);
				preparedStatement.setString(3, timestamp);
                nRowsInserted += preparedStatement.executeUpdate();

                preparedStatement.setString(1, "apple");
                preparedStatement.setInt(2, 100);
				preparedStatement.setString(3, timestamp);
                nRowsInserted += preparedStatement.executeUpdate();
                System.out.println(String.format("Inserted %d row(s) of data.", nRowsInserted));

                // NOTE No need to commit all changes to database, as auto-commit is enabled by default.

            }
            catch (SQLException e)
            {
                throw new SQLException("Encountered an error when executing given sql statement.", e);
            }       
        }
        else {
            System.out.println("Failed to create connection to database.");
        }
        System.out.println("Execution finished.");

		if (connection != null) {
			connection = null;
		}
		return null;
	 }
	 
	//vj.redis.cache.windows.net:6380,password=ATueZKr4E7lDX6SUBMElYC6KkPgBkQIgaTrcQ66THHA=,ssl=True,abortConnect=False

	public static void main(String[] args) {
		SpringApplication.run(GoTApplication.class, args);
	}

	public String getToken() {
		System.out.println("getToken called");

		OkHttpClient client = new OkHttpClient();
		okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=<SERVICE_PRINCIPAL_APP_ID>>&client_secret=<SERVICE_PRINCIPAL_PASSWORD(KEY)>&resource=https%3A%2F%2Fvault.azure.net");
		Request request = new Request.Builder()
		.url("https://login.microsoftonline.com/<TENANT-ID>/oauth2/token")
		.post(body)
		.addHeader("Content-Type", "application/x-www-form-urlencoded")
		.build();

		String output = "";
		try {
			Response response = client.newCall(request).execute();
			JSONObject objJsonObject = new JSONObject(response.body().string());
			System.out.println(objJsonObject.getString("access_token"));
			output = objJsonObject.getString("access_token");
		} catch (Exception e) {
			output = "ERROR: " + e.getMessage();
		}
		
		System.out.println("getToken output: " + output);
		
		return output;
	}

	public String getSecretValue() {
		System.out.println("getSecretValue called");
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
		.url("https://donvault.vault.azure.net/secrets/<Secret Key Name>/?api-version=2016-10-01")
		.get()
		.addHeader("Authorization", "Bearer " + getToken())
		.addHeader("Content-Type", "application/json")
		.build();

		String output = "";
		try {
			Response response = client.newCall(request).execute();
			
			JSONObject objJsonObject = new JSONObject(response.body().string());
			System.out.println(objJsonObject.getString("value"));
			output = objJsonObject.getString("value");

		} catch (Exception e) {
			output = "ERROR: " + e.getMessage();
		}

		System.out.println("getSecretValue output: " + output);

		return output;
	}
}
