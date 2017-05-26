package com.bsi.dms.webservice;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import com.bsi.dms.update.ErrorCode;

import android.util.Log;

public class UpdateWebServiceImpl implements UpdateWebService {
	private static final String TAG = "UPDATE";
	private static final String NAMESPACE = "com.bsi.dms/";
	private static final String METHOD_GET_LATEST_UPDATE_INFO = "GetLatestUpdateInfo";
	private static final String METHOD_GET_WEB_INFO = "GetWebInfo";
	private static final String METHOD_SET_EQUIPMENT_VERSION = "SetEquipmentVersion";
	private static final String KEY_VERSION_NO = "versionNo";
	private static final String KEY_EQUIPMENT_ID = "equipmentID";
	private static final String KEY_CLIENT_TYPE = "clientType";
	private static final String KEY_IS_PUBLISH = "isPublish";

	private String mServiceUrl;

	public UpdateWebServiceImpl(String url) {
		mServiceUrl = url;
	}

	@Override
	public String[] getLatestUpdateInfo(String versionNo, String equipmentID,
			int clientType) {
		Log.d(TAG, "getLatestUpdate versionNo=" + versionNo + " equipmentId="
				+ equipmentID + " clientType=" + clientType);
		if (versionNo == null) {
			versionNo = "";
		}
		if (equipmentID == null) {
			equipmentID = "";
		}
		String getInfoAction = NAMESPACE + METHOD_GET_LATEST_UPDATE_INFO;
		SoapObject request = new SoapObject(NAMESPACE,
				METHOD_GET_LATEST_UPDATE_INFO);
		request.addProperty(KEY_VERSION_NO, versionNo);
		request.addProperty(KEY_EQUIPMENT_ID, equipmentID);
		request.addProperty(KEY_CLIENT_TYPE, Integer.valueOf(clientType));
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = request;
		envelope.dotNet = true;
		HttpTransportSE trans = new HttpTransportSE(mServiceUrl);
		try {
			trans.call(getInfoAction, envelope);
			SoapObject response = (SoapObject) envelope.getResponse();
			int propertyCount = response.getPropertyCount();
			if (response == null || propertyCount == 0) {
				return null;
			}
			String[] result = new String[propertyCount];
			for (int i = 0; i < propertyCount; i++) {
				if (response.getProperty(i) instanceof SoapPrimitive) {
					result[i] = ((SoapPrimitive) response.getProperty(i))
							.toString();
				} else {
					result[i] = "";
				}
			}
			return result;
		} catch (IOException e) {
			fireFailure(ErrorCode.NETWORK_ERROR);
		} catch (XmlPullParserException e) {
			fireFailure(ErrorCode.NETWORK_ERROR);
		}
		return null;
	}

	@Override
	public String[] getWebInfo() {
		Log.d(TAG, "getWebInfo");
		String getInfoAction = NAMESPACE + METHOD_GET_WEB_INFO;
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		HttpTransportSE trans = new HttpTransportSE(mServiceUrl);
		try {
			trans.call(getInfoAction, envelope);
			SoapObject response = (SoapObject) envelope.getResponse();
			int propertyCount = response.getPropertyCount();
			if (response == null || propertyCount == 0) {
				return null;
			}
			String[] result = new String[propertyCount];
			for (int i = 0; i < propertyCount; i++) {
				if (response.getProperty(i) instanceof SoapPrimitive) {
					result[i] = ((SoapPrimitive) response.getProperty(i))
							.toString();
				} else {
					result[i] = "";
				}
			}
			return result;
		} catch (IOException e) {
			fireFailure(ErrorCode.NETWORK_ERROR);
		} catch (XmlPullParserException e) {
			fireFailure(ErrorCode.NETWORK_ERROR);
		}
		return null;
	}

	@Override
	public void setEquipmentVersion(String equipmentID, String versionNo,
			int isPublish) {
		Log.d(TAG, "setEquipmentVersion equipmentID=" + equipmentID
				+ " versionNo=" + versionNo + " isPublish=" + isPublish);
		if (versionNo == null) {
			versionNo = "";
		}
		if (equipmentID == null) {
			equipmentID = "";
		}
		String getInfoAction = NAMESPACE + METHOD_SET_EQUIPMENT_VERSION;
		SoapObject request = new SoapObject(NAMESPACE,
				METHOD_SET_EQUIPMENT_VERSION);
		request.addProperty(KEY_VERSION_NO, versionNo);
		request.addProperty(KEY_EQUIPMENT_ID, equipmentID);
		request.addProperty(KEY_IS_PUBLISH, Integer.valueOf(isPublish));
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = request;
		envelope.dotNet = true;
		HttpTransportSE trans = new HttpTransportSE(mServiceUrl);
		try {
			trans.call(getInfoAction, envelope);
		} catch (IOException e) {
			fireFailure(ErrorCode.NETWORK_ERROR);
		} catch (XmlPullParserException e) {
			fireFailure(ErrorCode.NETWORK_ERROR);
		}
	}

	private void fireFailure(int errorCode) {
		Log.e(TAG, "error:" + errorCode);
	}

}
