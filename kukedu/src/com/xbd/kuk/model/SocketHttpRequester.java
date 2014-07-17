package com.xbd.kuk.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.xbd.kuk.bean.FormFile;
import com.xbd.kuk.datastart.DataUtils;
import com.xbd.kuk.util.Constants;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

public class SocketHttpRequester {

	/**
	 * 直接通过HTTP协议提交数据到服务器,实现如下面表单提交功能: <FORM METHOD=POST
	 * ACTION="http://192.168.0.200:8080/ssi/fileload/test.do"
	 * enctype="multipart/form-data"> <INPUT TYPE="text" NAME="name"> <INPUT
	 * TYPE="text" NAME="id"> <input type="file" name="imagefile"/> <input
	 * type="file" name="zip"/> </FORM>
	 * 
	 * @param path
	 * 
	 * @param params
	 *            请求参数 key为参数名,value为参数值
	 * @param file
	 *            上传文件
	 */
	public static boolean post(String path, Map<String, String> params,
			FormFile[] files, Handler mhandler, Context mContext)
			throws Exception {
		String id = null;
		if (params != null && params.containsKey("id")) {
			id = params.get("id");
			params.remove("id");
		}

		final String BOUNDARY = "---------------------------7da2137580612"; // 数据分隔线
		final String endline = "--" + BOUNDARY + "--\r\n";// 数据结束标志

		int fileDataLength = 0;
		for (FormFile uploadFile : files) {// 得到文件类型数据的总长度
			StringBuilder fileExplain = new StringBuilder();
			fileExplain.append("--");
			fileExplain.append(BOUNDARY);
			fileExplain.append("\r\n");
			fileExplain.append("Content-Disposition: form-data;name=\""
					+ uploadFile.getParameterName() + "\";filename=\""
					+ uploadFile.getFilname() + "\"\r\n");
			fileExplain.append("Content-Type: " + uploadFile.getContentType()
					+ "\r\n\r\n");
			fileDataLength += fileExplain.length();
			if (uploadFile.getInStream() != null) {
				fileDataLength += uploadFile.getFile().length();
			} else {
				fileDataLength += uploadFile.getData().length;
			}
			fileDataLength += "\r\n".length();
		}
		StringBuilder textEntity = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {// 构造文本类型参数的实体数据
			textEntity.append("--");
			textEntity.append(BOUNDARY);
			textEntity.append("\r\n");
			textEntity.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"\r\n\r\n");
			textEntity.append(entry.getValue());
			textEntity.append("\r\n");
		}
		// 计算传输给服务器的实体数据总长度
		int dataLength = textEntity.toString().getBytes().length
				+ fileDataLength + endline.getBytes().length;

		URL url = new URL(path);
		int port = url.getPort() == -1 ? 80 : url.getPort();
		Socket socket = new Socket(InetAddress.getByName(url.getHost()), port);
		OutputStream outStream = socket.getOutputStream();
		// 下面完成HTTP请求头的发送
		String requestmethod = "POST " + url.getPath() + " HTTP/1.1\r\n";
		outStream.write(requestmethod.getBytes());
		String accept = "Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*\r\n";
		outStream.write(accept.getBytes());
		String language = "Accept-Language: zh-CN\r\n";
		outStream.write(language.getBytes());
		String token = "token: "+params.get("token")+"\r\n";
		outStream.write(token.getBytes());
		String contenttype = "Content-Type: multipart/form-data; boundary="
				+ BOUNDARY + "\r\n";
		outStream.write(contenttype.getBytes());
		String contentlength = "Content-Length: " + dataLength + "\r\n";
		outStream.write(contentlength.getBytes());
		String alive = "Connection: Keep-Alive\r\n";
		outStream.write(alive.getBytes());
		String host = "Host: " + url.getHost() + ":" + port + "\r\n";
		outStream.write(host.getBytes());
		// 写完HTTP请求头后根据HTTP协议再写一个回车换行
		outStream.write("\r\n".getBytes());
		// 把所有文本类型的实体数据发送出来
		outStream.write(textEntity.toString().getBytes());
		// 把所有文件类型的实体数据发送出来
		for (FormFile uploadFile : files) {
			StringBuilder fileEntity = new StringBuilder();
			fileEntity.append("--");
			fileEntity.append(BOUNDARY);
			fileEntity.append("\r\n");
			fileEntity.append("Content-Disposition: form-data;name=\""
					+ uploadFile.getParameterName() + "\";filename=\""
					+ uploadFile.getFilname() + "\"\r\n");
			fileEntity.append("Content-Type: " + uploadFile.getContentType()
					+ "\r\n\r\n");
			outStream.write(fileEntity.toString().getBytes());
			if (uploadFile.getInStream() != null) {
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = uploadFile.getInStream().read(buffer, 0, 1024)) != -1) {
					outStream.write(buffer, 0, len);
				}
				uploadFile.getInStream().close();
			} else {
				outStream.write(uploadFile.getData(), 0,
						uploadFile.getData().length);
			}
			outStream.write("\r\n".getBytes());
		}
		// 下面发送数据结束标志，表示数据已经结束
		outStream.write(endline.getBytes());

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		String line = null;
		StringBuilder message = new StringBuilder();

		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			if (line.indexOf("success") > 0) {
				message.append(line);
			}

		}
		outStream.flush();
		outStream.close();
		reader.close();
		socket.close();
		
		if (message != null && mContext != null&&!"".equals(new JSONObject(message.toString()).getString("error"))){
			int errorCode = Integer.valueOf(new JSONObject(message.toString()).getString("error"));
			if(errorCode == 800){
				Message obtainMessage = mhandler.obtainMessage();
				obtainMessage.what = Constants.DEAL_TOKEN_LOCKED;
	
				mhandler.sendMessage(obtainMessage);
				return false;
			}
		}
		
		if (message != null && message.toString().contains("content")) {

		} else {
			Message obtainMessage = mhandler.obtainMessage();
			//Log.d("TAG", "the message=" + message.toString());

			// String
			// message1="{\"message\":\"\",\"error\":[null],\"success\":true,\"payload\":{\"id\":\"20131203173331176nqz1519357.jpg\",\"fileName\":\"20131203_173349_-741975334.jpg\",\"url\":\"http://10.96.142.93:17010/O/2013/1203/17/O20131203173331176nqz1519357.jpg\"}}";
			boolean isSuccess = (boolean) (new JSONObject(message.toString())
					.getBoolean("success"));
			if (id != null) {
				Bundle bundle = new Bundle();
				bundle.putString("id", id);
				bundle.putBoolean("isSuccess", isSuccess);
				if (isSuccess) {
					DataUtils.getInstance(mContext).updateSpecityItem(
							id, Constants.MessageSendStatus.SUCCESS);
				} else {
					DataUtils.getInstance(mContext).updateSpecityItem(
							id, Constants.MessageSendStatus.ERROR);

				}
				obtainMessage.what = Constants.RESPONSE_SEND_MESSAGE;
				obtainMessage.setData(bundle);
				mhandler.sendMessage(obtainMessage);
			}
		}
		// if(reader.readLine().indexOf("200")==-1){//读取web服务器返回的数据，判断请求码是否为200，如果不是200，代表请求失败
		// System.out.println(reader.readLine().toString());
		// return false;
		// }

		return true;
	}

	/**
	 * 提交数据到服务器
	 * 
	 * @param path
	 * @param params
	 *            请求参数 key为参数名,value为参数值
	 * @param file
	 *            上传文件
	 */
	public static boolean post(String path, Map<String, String> params,
			FormFile file, Handler mhandler, Context mContext) throws Exception {
		return post(path, params, new FormFile[] { file }, mhandler, mContext);
	}

	/**
	 * 发送Post请求
	 * 
	 * @param path
	 *            请求路径
	 * @param params
	 *            请求参数
	 * @param encoding
	 *            编码
	 * @return 请求是否成功
	 */
	public static boolean sendPOSTRequest(String path,
			Map<String, String> params, String encoding, Handler mhandler,
			Context mContext) throws Exception {
		String id = null;
		if (params != null && params.containsKey("id")) {
			id = params.get("id");
			params.remove("id");
		}

		StringBuilder data = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				data.append(entry.getKey()).append("=");
				data.append(URLEncoder.encode(entry.getValue(), encoding));
				data.append("&");
			}
			data.deleteCharAt(data.length() - 1);
		}
		byte[] entity = data.toString().getBytes();// 生成实体数据
		HttpURLConnection conn = (HttpURLConnection) new URL(path)
				.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);// 允许对外输出数据
		conn.setRequestProperty("token", params.get("token"));
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
		OutputStream outStream = conn.getOutputStream();
		outStream.write(entity);

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String line = null;
		StringBuilder message = new StringBuilder();
		while ((line = reader.readLine()) != null) {

			//Log.d("Mqtt", "SocketHttpRequester: 服务器返回数据 ： " + line);
			message.append(line);
			// System.out.println(new String(Base64.decode(line,
			// Base64.DEFAULT)));
		}

		if (message != null && mContext != null&&!"".equals(new JSONObject(message.toString()).getString("error"))){
			
			
			int errorCode = Integer.valueOf(new JSONObject(message.toString()).getString("error"));;
			if(errorCode == 800){
				Message obtainMessage = mhandler.obtainMessage();
				obtainMessage.what = Constants.DEAL_TOKEN_LOCKED;
				mhandler.sendMessage(obtainMessage);
				return false;
			}
		}
		
		if (message != null && message.toString().contains("content")) {
			// String newMessage = message.toString();
			// JSONObject subObject = JsonHelper.getSubObject(new
			// JSONObject(newMessage), "payload");
			// JSONArray subArrayObject =
			// JsonHelper.getSubArrayObject(subObject, "items");
			// JSONObject object =
			// (JSONObject)subArrayObject.get(subArrayObject.length()-1);
			// JSONObject subObject2 = JsonHelper.getSubObject(object,
			// "message");
			// String s = (String)subObject2.get("content");
			// //Log.d("Mqtt", " 服务器返回数据 ： "+message.toString());
			Message obtainMessage = mhandler.obtainMessage();
			obtainMessage.what = Constants.DEAL_NEW_MESSAGE;
			obtainMessage.obj = message.toString();
			mhandler.sendMessage(obtainMessage);
		} else {
			Message obtainMessage = mhandler.obtainMessage();
			boolean isSuccess = (boolean) (new JSONObject(message.toString())
					.getBoolean("success"));
			if (id != null) {
				Bundle bundle = new Bundle();
				bundle.putString("id", id);
				bundle.putBoolean("isSuccess", isSuccess);
				if (mContext != null) {
					if (isSuccess) {
						DataUtils.getInstance(mContext).updateSpecityItem(
								id, Constants.MessageSendStatus.SUCCESS);
					} else {
						DataUtils.getInstance(mContext).updateSpecityItem(
								id, Constants.MessageSendStatus.ERROR);

					}
				}

				obtainMessage.what = Constants.RESPONSE_SEND_MESSAGE;
				obtainMessage.setData(bundle);
				mhandler.sendMessage(obtainMessage);
			}
		}

		return false;
	}

}
