package com.bsi.dms.tts;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class WebserviceTTSEngine implements TTSEngine, TTSServiceCallback {

	private static final String TAG = "WebserviceTTSEngine";
	private static final String TTS_SERVICE_SOAP = "http://192.168.7.195:88/Test_DMS/DMS.Web.Broadcast/WebServiceBroadcast.asmx";
	private static final String TTS_ARG_KEY = "arg0";
	private static final String CONTENT_LENGTH = "Content-Length";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String TTS_CONTENT_TYPE = "text/xml; charset=utf-8";
	private static final String SOAP_ACTION = "SOAPAction";
	private static final String TTS_SOAP_ACTION = "http://tempuri.org/TextToVoice";
	private static final String METHOD_POST = "POST";
	private static final int TIME_OUT = 20 * 1000;

	private static final int SPEAK = 1;
	/**
	 * single instance
	 */
	private static WebserviceTTSEngine instance;
	private Context mContext;
	private SoundManager mSoundManager;
	/**
	 * Hold tts id which is being converted to sound data. tts string first
	 * convert to ttsid, then put ttsid in this set, check ttsid in the set when
	 * converting, remove ttsid when finish converting string to sound data
	 */
	private HashSet<String> mConvertIdSet;
	/**
	 * To synchronize mConvertIdSet
	 */
	private Object mHashSetMutex = new Object();

	private HandlerThread mHandlerThread;

	private Handler mHandler;

	public synchronized static WebserviceTTSEngine getInstance(Context c) {
		if (instance == null) {
			instance = new WebserviceTTSEngine(c);
		}
		return instance;
	}

	private WebserviceTTSEngine(Context c) {
		mContext = c;
		mConvertIdSet = new HashSet<String>();
		mSoundManager = SoundManagerImpl.getInstance(c);
		mHandlerThread = new HandlerThread("tts");
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SPEAK:
					TTSArguments args = (TTSArguments) msg.obj;
					convertAndPlay(args);
					break;
				}
			}

		};
	}

	private void addTTSId(String ttsId) {
		Log.d(TAG, "addTTSId:" + ttsId);
		synchronized (mHashSetMutex) {
			mConvertIdSet.add(ttsId);
		}
	}

	@Override
	public void stop() {
		removeAllTTSId();
		mSoundManager.stop();
	}

	private void convertAndPlay(TTSArguments args) {
		TTSServiceCallback callback = args.callback;
		String filename = getConvertedFilename(args);
		if (TextUtils.isEmpty(filename)) {
			Log.e(TAG, "convert error:" + args.text);
			if (callback != null) {
				args.callback.onTTSSoundData(args, null);
			}
			removeTTSId(args.ttsId);
			return;
		}
		byte[] data = null;
		if (shouldSpeak(args.ttsId)) {
			data = downloadAudioData(args.ttsId, filename);
		}

		if (shouldSpeak(args.ttsId)) {
			if (callback != null) {
				callback.onTTSSoundData(args, data);
			}
		}
		removeTTSId(args.ttsId);
	}

	private byte[] downloadAudioData(String ttsId, String fileName) {
		HttpGet httpRequest = new HttpGet(fileName);
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, TIME_OUT);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				TIME_OUT);
		try {
			HttpResponse httpResponse = httpclient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				byte[] data = EntityUtils.toByteArray(httpResponse.getEntity());
				return data;
			} else {
				Log.d(TAG, "download error!");
				return null;
			}
		} catch (ClientProtocolException e) {
			Log.d(TAG, "download error:" + e.getMessage());
		} catch (IOException e) {
			Log.d(TAG, "download error:" + e.getMessage());
		}
		return null;
	}

	private String genTTSId(String text) {
		if (text == null || text.length() == 0) {
			text = "null";
		}
		return UUID.nameUUIDFromBytes(text.getBytes()).toString();
	}

	private String getConvertedFilename(TTSArguments args) {
		String request = buildTTSWebserviceString();
		request = request.replace(TTS_ARG_KEY, args.text);
		HttpURLConnection httpConn = null;
		OutputStream out = null;
		BufferedReader br = null;
		InputStreamReader isr = null;
		try {
			URL url = new URL(TTS_SERVICE_SOAP);
			URLConnection connection = url.openConnection();
			httpConn = (HttpURLConnection) connection;
			byte[] bytes = request.getBytes();
			httpConn.setRequestProperty(CONTENT_LENGTH,
					String.valueOf(bytes.length));
			httpConn.setRequestProperty(CONTENT_TYPE, TTS_CONTENT_TYPE);
			httpConn.setRequestProperty(SOAP_ACTION, TTS_SOAP_ACTION);
			httpConn.setRequestMethod(METHOD_POST);
			httpConn.setConnectTimeout(TIME_OUT);
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			out = httpConn.getOutputStream();
			out.write(bytes);
			out.flush();

			isr = new InputStreamReader(httpConn.getInputStream());
			br = new BufferedReader(isr);
			String inputLine = "";
			StringBuilder responseBuffer = new StringBuilder();
			while ((inputLine = br.readLine()) != null) {
				responseBuffer.append(inputLine);
			}
			byte[] response = responseBuffer.toString().getBytes();
			return retriveTTSFilename(response);
		} catch (MalformedURLException e) {
			Log.d(TAG, "convert string error:" + e.getMessage());
		} catch (IOException e) {
			Log.d(TAG, "convert string error:" + e.getMessage());
		} finally {
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					Log.d(TAG, "convert string error:" + e.getMessage());
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					Log.d(TAG, "convert string error:" + e.getMessage());
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					Log.d(TAG, "convert string error:" + e.getMessage());
				}
			}
			if (httpConn != null) {
				httpConn.disconnect();
			}
		}
		return "";
	}

	private String retriveTTSFilename(byte[] data) {
		ByteArrayInputStream bais = null;
		try {
			bais = new ByteArrayInputStream(data, 0, data.length);
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(bais);
			NodeList nl = document.getElementsByTagName("TextToVoiceResult");
			if (nl == null || nl.getLength() == 0) {
				return "";
			}
			Node node = nl.item(0);
			if (node == null) {
				return "";
			}
			NodeList nl2 = node.getChildNodes();
			if (nl2 == null || nl2.getLength() == 0) {
				return "";
			}
			Node node2 = nl2.item(0);
			if (node2 == null) {
				return "";
			}
			return node2.getNodeValue();
		} catch (ParserConfigurationException e) {
			Log.d(TAG, "retrive TTSFilename error:" + e.getMessage());
		} catch (SAXException e) {
			Log.d(TAG, "retrive TTSFilename error:" + e.getMessage());
		} catch (IOException e) {
			Log.d(TAG, "retrive TTSFilename error:" + e.getMessage());
		} finally {
			if (bais != null) {
				try {
					bais.close();
				} catch (IOException e) {
					Log.d(TAG, "retrive TTSFilename error:" + e.getMessage());
				}
			}
		}
		return "";
	}

	private String buildTTSWebserviceString() {
		String context = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
				+ " <soap:Body>"
				+ "<TextToVoice xmlns=\"http://tempuri.org/\">"
				+ " <text>arg0</text>" + "</TextToVoice>" + "</soap:Body>"
				+ "</soap:Envelope>";
		return context;
	}

	@Override
	public void onTTSSoundData(TTSArguments args, byte[] soundData) {
		if (args == null) {
			Log.e(TAG, " TTSArguments null in onTTSResult");
			return;
		}
		if (soundData == null || soundData.length == 0) {
			Log.e(TAG, " soundData null");
			return;
		}
		if (args.cancelPrevious) {
			mSoundManager.stop();
		}
		TTSData ttsData = new TTSData(args.text, TTSData.TTSDataState.WAIT,
				soundData);
		mSoundManager.play(ttsData);
	}

	private void removeAllTTSId() {
		synchronized (mHashSetMutex) {
			mConvertIdSet.clear();
		}
	}

	private void removeTTSId(String ttsId) {
		Log.d(TAG, "removeTTSId:" + ttsId);
		synchronized (mHashSetMutex) {
			mConvertIdSet.remove(ttsId);
		}
	}

	public boolean shouldSpeak(String ttsId) {
		return mConvertIdSet.contains(ttsId);
	}

	@Override
	public void speak(String text) {
		mSoundManager.stop();
		if (TextUtils.isEmpty(text)) {
			Log.e(TAG, "skip empty text tts!");
			return;
		}
		String ttsId = genTTSId(text);
		if (mConvertIdSet.contains(ttsId)) {
			Log.e(TAG, "ttsId:" + ttsId + " is already in convert!");
			return;
		}
		addTTSId(ttsId);
		TTSArguments args = new TTSArguments(text, true, ttsId, this);
		Message msg = new Message();
		msg.what = SPEAK;
		msg.obj = args;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onDestroy() {
		stop();
	}
}
