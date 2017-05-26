package com.bsi.dms.download;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.bsi.dms.config.PlayerApplication;
import com.bsi.dms.utils.CommonUtil;

public class Downloader {
	private static final String TAG = "Downloader";

	private int done;
	private InfoDao dao;
	private int fileLen;
	private Handler handler;
	private boolean isPause;
	private DownloadThread[] threads; // 根据线程数设置下载线程池
	private static final int BUFFER_SIZE = 1024 * 10;
	private static final int CONNECTION_TIMEOUT = 10*1000;

	public Downloader(Context context, Handler handler) {
		dao = new InfoDao(context);
		// this.handler = handler;
	}

	/**
	 * 多线程下载
	 * 
	 * @param path
	 *            下载路径
	 * @param thCount
	 *            需要开启多少个线程
	 * @throws Exception
	 */
	public boolean download(String path, File fileSaveDir, int thCount) {
		URL url = null;
		HttpURLConnection conn = null;
		try {
			url = new URL(path);
			conn = (HttpURLConnection) url.openConnection();
			// 设置超时时间
			conn.setConnectTimeout(CONNECTION_TIMEOUT);
			if (conn.getResponseCode() == 200) {
				fileLen = conn.getContentLength();
				// Handler发送消息，主线程接收消息，获取数据的长度
				if (CommonUtil.getFreeSpaceSize() <= fileLen - 5000) {
					Toast.makeText(PlayerApplication.getInstance(), "存储空间不足!",
							Toast.LENGTH_SHORT).show();
					return false;
				}
				String name = path.substring(path.lastIndexOf("/") + 1);
				if (!fileSaveDir.exists()) {
					fileSaveDir.mkdirs(); // 如果指定的文件不存在，则创建目录，此处可以创建多层目录
				}
				File file = new File(fileSaveDir, name);
				RandomAccessFile raf = new RandomAccessFile(file, "rws");
				raf.setLength(fileLen);
				raf.close();
				this.threads = new DownloadThread[thCount]; // 根据下载的线程数创建下载线程池

				// 计算每个线程下载的字节数
				int partLen = (fileLen + thCount - 1) / thCount;
				for (int i = 0; i < thCount; i++) {
					this.threads[i] = new DownloadThread(url, file, partLen, i);
					this.threads[i].start(); // 启动线程
				}

				boolean notFinished = true;// 下载未完成
				while (notFinished) {// 循环判断所有线程是否完成下载
					Thread.sleep(900);
					notFinished = false;// 假定全部线程下载完成
					for (int i = 0; i < this.threads.length; i++) {
						if (this.threads[i] != null
								&& !this.threads[i].isFinished()) {// 如果发现线程未完成下载
							notFinished = true;// 设置标志为下载没有完成
							if (this.threads[i].getDownloadedLength() == -1) {// 如果下载失败,再重新在已经下载的数据长度的基础上下载
								this.threads[i] = new DownloadThread(url, file,
										partLen, i);
								this.threads[i].start(); // 启动线程
							}
						}
					}
				}
				return true;
			} else {
				Log.w("DL", "404 path: " + path);
				return false;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
			dao.closeDb();
		}
	}

	public boolean downloadhtml(String path, File fileSaveDir) {
		Log.d("DL", "downloadhtml start download:" + path);
		long start = System.currentTimeMillis();
		HttpURLConnection conn = null;
		URL url = null;
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			url = new URL(path);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(CONNECTION_TIMEOUT);
			if (conn.getResponseCode() != 200) {
				Log.e(TAG, "download error,network not available,url=" + path);
				return false;
			}

			is = conn.getInputStream();
			String name = path.substring(path.lastIndexOf("/") + 1);
			if (!fileSaveDir.exists()) {
				fileSaveDir.mkdirs();
			}
			File file = new File(fileSaveDir, name);
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			byte[] buffer = new byte[BUFFER_SIZE];
			int read = 0;
			while ((read = is.read(buffer)) != -1) {
				fos.write(buffer, 0, read);
			}
			fos.flush();
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			closeStream(fos);
			closeStream(is);
			if (conn != null) {
				conn.disconnect();
			}
			long end = System.currentTimeMillis();
			Log.d("DL", "downloadhtml download end time :" + (end - start));
		}
	}

	public final void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private final class DownloadThread extends Thread {
		private URL url;
		private File file;
		private int partLen;
		private int id;
		private boolean finished = false; // 该线程是否完成下载的标志

		public DownloadThread(URL url, File file, int partLen, int id) {
			this.url = url;
			this.file = file;
			this.partLen = partLen;
			this.id = id;
		}

		/**
		 * 写入操作
		 */
		public void run() {
			// 判断上次是否有未完成任务
			Info info = dao.query(url.toString(), id);
			if (info != null) {
				// 如果有, 读取当前线程已下载量
				done += info.getDone();
			} else {
				// 如果没有, 则创建一个新记录存入
				info = new Info(url.toString(), id, 0);
				dao.insert(info);
			}

			int start = id * partLen + info.getDone(); // 开始位置 += 已下载量
			int end = (id + 1) * partLen - 1;
			HttpURLConnection conn = null;
			InputStream inStream = null;
			RandomAccessFile raf = null;
			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(CONNECTION_TIMEOUT);
				// 获取指定位置的数据，Range范围如果超出服务器上数据范围, 会以服务器数据末尾为准
				conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
				raf = new RandomAccessFile(file, "rws");
				raf.seek(start);
				// 开始读写数据
				inStream = conn.getInputStream();
				byte[] buf = new byte[1024 * 10];
				int len;
				while ((len = inStream.read(buf)) != -1) {
					if (isPause) {
						// 使用线程锁锁定该线程
						synchronized (dao) {
							try {
								dao.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					raf.write(buf, 0, len);
					done += len;
					info.setDone(info.getDone() + len);
					// 记录每个线程已下载的数据量
					dao.update(info);
				}
				// 删除下载记录
				dao.deleteAll(info.getPath(), fileLen);
				finished = true; // 设置完成标志为true，无论是下载完成还是用户主动中断下载
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (inStream != null) {
					try {
						inStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (raf != null) {
					try {
						raf.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (conn != null) {
					conn.disconnect();
				}
			}
		}

		/**
		 * 下载是否完成
		 * 
		 * @return
		 */
		public boolean isFinished() {
			return finished;
		}

		/**
		 * 已经下载的内容大小
		 * 
		 * @return 如果返回值为-1,代表下载失败
		 */
		public long getDownloadedLength() {
			return done;
		}
	}

	// 暂停下载
	public void pause() {
		isPause = true;
	}

	// 继续下载
	public void resume() {
		isPause = false;
		// 恢复所有线程
		synchronized (dao) {
			dao.notifyAll();
		}
	}
}
