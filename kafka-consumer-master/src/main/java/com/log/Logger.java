package com.log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Logger {
	
	public void info(String ... strings) {
		for(String s : strings)
			System.out.println( s );
		
		try {
			callApi();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void callApi() throws Exception {
		
		ignoreSSL();

		String request        = "https://so1:8088/services/collector";		
		String urlParameters  = "{\"event\": \"From Inside...!\", \"sourcetype\": \"JAVA!!!!\"}";
		String authorization = "Splunk f6ced127-f84e-4f72-a370-f86a05c9ed87";
		
		byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
		int    postDataLength = postData.length;
		URL    url            = new URL( request );		
		
		//HttpURLConnection conn= (HttpURLConnection) url.openConnection();
		 HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
		
		conn.addRequestProperty("Authorization", authorization);
		
		conn.setDoOutput( true );
		conn.setInstanceFollowRedirects( false );
		conn.setRequestMethod( "POST" );
		conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
		conn.setRequestProperty( "charset", "utf-8");
		conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
		conn.setUseCaches( false );
		try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
		   wr.write( postData );
		}
		
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                		conn.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            response.append(inputLine);

        in.close();
		
		System.out.println("API Chamada com sucesso.");
		System.out.println( response.toString() );
		
	}
	
	public void ignoreSSL() {
		
		try {
			   // Create a trust manager that does not validate certificate chains
			   TrustManager[] trustAllCerts = new TrustManager[] {
			      new X509TrustManager() {
			       public X509Certificate[] getAcceptedIssuers() {
			           return null;
			       }
			       public void checkClientTrusted(X509Certificate[] certs, String authType) {
			       }
			       public void checkServerTrusted(X509Certificate[] certs, String authType) {
			       }
			      }
			   };

			   // Install the all-trusting trust manager
			   SSLContext sc = SSLContext.getInstance("SSL");
			   sc.init(null, trustAllCerts, new java.security.SecureRandom());
			   HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			   // Create all-trusting host name verifier
			   HostnameVerifier allHostsValid = new HostnameVerifier() {
			       public boolean verify(String hostname, SSLSession session) {
			           return true;
			       }
			   };

			   // Install the all-trusting host verifier
			   HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			} catch (NoSuchAlgorithmException | KeyManagementException e) {
			    e.printStackTrace();
			}		
		
	}
	

}
