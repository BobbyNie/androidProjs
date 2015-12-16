package com.bobby.firstsms;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

public class WeChartSender {

	private static final int SET_CONNECTION_TIMEOUT = 1000 * 30;
	private static final int SET_SOCKET_TIMEOUT = 1000 * 30;

	public WeChartSender() {
	}

	private static String accessTokenUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=wx64e31cf31b8302bc&corpsecret=pCKuUGAfeHSjvefFFvc9GeEfQU7L5Ec8PYk_cYmpPeqkkpChAUm6LRD4ve10CKgA";
	private static String sendUrl = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=";

	// 发送微信消息
	public static boolean sendWeChart(String msg) {
		String token = getAccessToken();
		try {
			HttpClient flashClient = getHttpsClient();
			HttpPost req = new HttpPost(sendUrl + token);
			JSONObject data = new JSONObject();

			data.put("touser", "@all");

			data.put("msgtype", "text");
			data.put("agentid", "2");
			
			JSONObject content = new JSONObject();			
			content.put("content", msg);
			data.put("text", content);
			data.put("safe", 0);

			HttpEntity entity = new ByteArrayEntity(data.toString().getBytes("UTF-8"));

			req.setEntity(entity);

			HttpResponse resp = flashClient.execute(req);
			int out = resp.getStatusLine().getStatusCode();
			if (out == 200) {
				InputStream input = resp.getEntity().getContent();
				int len = (int) resp.getEntity().getContentLength();
				byte[] outBytes = new byte[len];
				int outlen = input.read(outBytes);
				while (outlen < len) {
					input.read(outBytes, outlen, len - outlen);
				}
				String str = new String(outBytes);
				JSONObject ret = new JSONObject(str);
				String errorCode = ret.getString("errcode");
				if("0".equals(errorCode))
					return true;	
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}			
		return false;
	}

	private static long accessTokenTime = -1;
	private static String accessToken = "";

	/**
	 * 获取accessToken
	 * 
	 * @return
	 */
	public static String getAccessToken() {
		if (System.currentTimeMillis() - accessTokenTime < 3000) {
			return accessToken;
		}
		try {
			HttpUriRequest req = new HttpGet(accessTokenUrl);
			HttpClient flashClient = getHttpsClient();
			HttpResponse resp = flashClient.execute(req);
			int out = resp.getStatusLine().getStatusCode();
			if (out < 200 || out > 299) {
				return "error httpstate:" + out;
			}
			InputStream input = resp.getEntity().getContent();
			int len = (int) resp.getEntity().getContentLength();
			byte[] outBytes = new byte[len];
			int outlen = input.read(outBytes);
			while (outlen < len) {
				input.read(outBytes, outlen, len - outlen);
			}
			JSONObject obj = new JSONObject(new String(outBytes, "UTF-8"));
			input.close();
			accessToken = obj.getString("access_token");
			return accessToken;
		} catch (IOException e) {
			return "error IOException";
		} catch (JSONException e) {
			return "error JSONException";
		}
	}

	private static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	private static HttpClient getHttpsClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 10000);

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			HttpConnectionParams.setConnectionTimeout(params, SET_CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, SET_SOCKET_TIMEOUT);
			HttpClient client = new DefaultHttpClient(ccm, params);

			return client;
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		sendWeChart("test");
	}

}
