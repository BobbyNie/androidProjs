package com.bobby.gen8auto.http;

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
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Base64;

import com.bobby.gen8auto.conf.Gen8Config;
import com.bobby.mail.MailSender;

@SuppressLint("NewApi")
public class Gen8RestFullPower {
	public Gen8RestFullPower() {
	}

	private static final int SET_CONNECTION_TIMEOUT = 5 * 1000;
	private static final int SET_SOCKET_TIMEOUT = 20 * 1000;
	private static final MailSender mailSender = new MailSender();

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
	 * 获取电源状态
	 * 
	 * @return
	 */
	public static String getPowerState() {
		try {
			HttpUriRequest req = new HttpGet("https://" + Gen8Config.getIp() + ":" + Gen8Config.getPort() + "/rest/v1/Systems/1");
			addAuthorizationHeader(req);
			HttpClient flashClient = getHttpsClient();
			HttpResponse resp = flashClient .execute(req);
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
			return obj.getString("PowerState");
		} catch (IOException e) {
			return "error IOException";
		} catch (JSONException e) {
			return "error JSONException";
		}
	}

	/**
	 * 打开gen8 电源，注意该函数会等待确认 电源状态为on
	 * 
	 * @return
	 */
	public static synchronized boolean startUp() {
		return powerChange("on", "off");
	}

	/**
	 * 关闭gen8 电源，注意该函数会等待确认 电源状态为off
	 * 
	 * @return
	 */
	public static synchronized boolean ShutDown() {
		return powerChange("off", "on");
	}

	private static synchronized boolean powerChange(String curr, String need) {

		String powerState = getPowerState();

		if (powerState.startsWith("error")) {
			sendInfoMail("Gen8 button push error","current State:"+curr+", error Info:"+powerState);
			return false;
		}

		if (powerState.toLowerCase(Locale.ENGLISH).equals(curr)) {
			return true;
		}

		String btnOut = pushButton();

		if ("ok".equals(btnOut)) {

			for (int i = 0; i < 5; i++) {
				try {
					Thread.sleep(10 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				powerState = getPowerState();
				if (powerState.toLowerCase(Locale.ENGLISH).equals(need)) {
					sendInfoMail("Gen8 button push Ok","from State:"+curr+" to State:"+need);
					return true;
				}
			}

		}else {
			sendInfoMail("Gen8 button push error","current State:"+curr+", error Info:"+btnOut);
		}

		return false;
	}

	/**
	 * 
	 * @return ok if bush button success，else return error string start with
	 *         error
	 */
	private static synchronized String pushButton() {
		try {
			HttpPost req = new HttpPost("https://" + Gen8Config.getIp() + ":" + Gen8Config.getPort()
					+ "/rest/v1/Systems/1/Actions/Oem/Hp/ComputerSystemExt.PowerButton");
			addAuthorizationHeader(req);
			req.addHeader("Content-Type", "application/json");
			req.setEntity(new StringEntity("{\"Action\":\"PowerButton\",\"PushType\":\"Press\"}"));

			HttpClient client = getHttpsClient();
			HttpResponse resp = client .execute(req);
			int out = resp.getStatusLine().getStatusCode();
			if (out < 200 || out > 299) {
				return "error http state:" + out;
			}
			HttpEntity entty = resp.getEntity();
			if(entty != null) {
				entty.getContent().close();
			}
			return "ok";
		} catch (IOException e) {
			return "error IOException";
		}
	}

	private static void addAuthorizationHeader(HttpRequest req) throws UnsupportedEncodingException {
		String auth = "BASIC " + new String(Base64.encode((Gen8Config.getUsername() + ":" + Gen8Config.getPasswd()).getBytes("UTF-8"), Base64.DEFAULT), "UTF-8");
		req.addHeader("Authorization",auth.trim());
				
	}
	
	private static void sendInfoMail(String title,String info) {
		try {
			mailSender.sendMail(title, info, Gen8Config.getMailUser(), Gen8Config.getMailToUsers(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
