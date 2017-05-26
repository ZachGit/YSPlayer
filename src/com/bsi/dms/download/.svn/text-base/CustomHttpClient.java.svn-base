package com.bsi.dms.download;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class CustomHttpClient {
	private static HttpClient instance;
	private CustomHttpClient() {
	}

	public static synchronized HttpClient getHttpClient() {
		if (instance == null) {
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setUseExpectContinue(params, true);

			// Mozilla/4.0 (compatible; MSIE 7.0b; Windows NT 6.0)
			// Firefox3.03
			// Mozilla/5.0 (Windows; U; Windows NT 5.2; zh-CN; rv:1.9.0.3)
			// Gecko/2008092417 Firefox/3.0.3
			HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
//			ConnManagerParams.setTimeout(params, 10000);
//			HttpConnectionParams.setConnectionTimeout(params, 50000);
//			HttpConnectionParams.setSoTimeout(params, 10000);
			SchemeRegistry schreg = new SchemeRegistry();
			schreg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schreg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
			ClientConnectionManager clitConnMgr = new ThreadSafeClientConnManager(params, schreg);
			instance = new DefaultHttpClient(clitConnMgr, params);
		}
		return instance;
	}

}
