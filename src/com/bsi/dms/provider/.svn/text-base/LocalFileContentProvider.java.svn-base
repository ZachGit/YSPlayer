package com.bsi.dms.provider;

import java.io.File;
import java.io.FileNotFoundException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.bsi.dms.utils.CommonUtil;

public class LocalFileContentProvider extends ContentProvider {

	//private static final String DIR = "/mnt/nand/";

	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {
		String DIR = CommonUtil.getFontPath()+File.separator;
		String path = uri.getPath().substring(1);
		String file = DIR + path;
		Log.w("provicer", "ask for:"+file);
		int imode = ParcelFileDescriptor.MODE_READ_ONLY;
		ParcelFileDescriptor pfd = ParcelFileDescriptor.open(new File(file),
				imode);
		return pfd;
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public int delete(Uri uri, String s, String[] as) {
		throw new UnsupportedOperationException(
				"Not supported by this provider");
	}

	@Override
	public String getType(Uri uri) {
		throw new UnsupportedOperationException(
				"Not supported by this provider");
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentvalues) {
		throw new UnsupportedOperationException(
				"Not supported by this provider");
	}

	@Override
	public Cursor query(Uri uri, String[] as, String s, String[] as1, String s1) {
		throw new UnsupportedOperationException(
				"Not supported by this provider");
	}

	@Override
	public int update(Uri uri, ContentValues contentvalues, String s,
			String[] as) {
		throw new UnsupportedOperationException(
				"Not supported by this provider");
	}

}
