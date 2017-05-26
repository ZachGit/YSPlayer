package com.bsi.dms.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.URL;

import org.apache.commons.net.ftp.FTPClient;

import android.content.Context;
import android.util.Log;

import com.bsi.dms.utils.CommonUtil;

public class DownloadEngine {
	private static final String TAG = "DownloadEngine";
	Context context;
	public static final int DEFAULT_FTP_PORT = 21;
	private static final int BUFFER_SIZE = 1024 * 20;
	private static DownloadEngine downloadEngine;
	static {
		downloadEngine = new DownloadEngine();
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public static DownloadEngine getInstance() {
		return downloadEngine;
	}

	public boolean download(String downloadUrl, File fileSaveDir) {
		Downloader fileDownload = new Downloader(context, null);
		try {
			Log.w("DL", downloadUrl);
			if (isMediaFile(downloadUrl
					.substring(downloadUrl.lastIndexOf("/") + 1))) {
				return fileDownload.download(downloadUrl, fileSaveDir, 3);
			} else {
				return fileDownload.downloadhtml(downloadUrl, fileSaveDir);
			}
		} catch (Exception e) {
			Log.e("DL", " " + e.getLocalizedMessage());
			return false;
		}

	}

	public boolean downloadFtp(String server, String filepath, String username,
			String password, String saveFilepath) {
		FTPClient ftpClient = null;
		InputStream inStream = null;
		FileOutputStream fos = null;
		try {
			if (server.endsWith("/")) {
				server = server.substring(0, server.length() - 1);
			}
			if (filepath.startsWith("/")) {
				filepath = filepath.substring(1);
			}
			URL url = new URL(server + "/" + filepath);
			String filepathOnServer = url.getFile();
			String host = url.getHost();
			int port = url.getPort();
			if (port == -1) {
				port = DEFAULT_FTP_PORT;
			}
			ftpClient = new FTPClient();
			ftpClient.connect(host, port);
			boolean canLogin = ftpClient.login(username, password);
			if (!canLogin) {
				Log.d(TAG, "Error ftp username and password");
				return false;
			}
			ftpClient
					.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
			Log.d(TAG, "start download");
			long start = System.currentTimeMillis();
			inStream = ftpClient.retrieveFileStream(filepathOnServer);
			if (inStream == null) {
				Log.e(TAG, "can not download file:" + filepathOnServer + " on:"
						+ host);
				return false;
			}
			CommonUtil.ensureFileExist(saveFilepath);
			fos = new FileOutputStream(saveFilepath);
			byte[] buffer = new byte[BUFFER_SIZE];
			int read = inStream.read(buffer);
			while (read != -1) {
				fos.write(buffer, 0, read);
				read = inStream.read(buffer);
			}
			Log.d(TAG, "end download time "
					+ (System.currentTimeMillis() - start));
			fos.flush();
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			if (ftpClient != null) {
				try {
					ftpClient.logout();
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	public boolean isMediaFile(String filename) {
		if (filename.endsWith(".mp4") || filename.endsWith(".wmv")
				|| filename.endsWith(".mp3")) {
			return true;
		} else {
			return false;
		}
	}

	private class DownloadListener implements DownloadProgressListener {
		public void onDownloadSize(int size) {
			// TODO Auto-generated method stub
		}
	}

}
