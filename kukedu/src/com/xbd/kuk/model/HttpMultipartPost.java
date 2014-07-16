package com.xbd.kuk.model;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.xbd.kuk.model.CustomMultipartEntity.ProgressListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
* @description 
* @version 1.0
* @author wangpan
* @update 2013-10-22 下午05:20:09 
*/

public class HttpMultipartPost extends AsyncTask<String, Integer, String> {

    private Context context;  
    private String filePath;  
    private ProgressDialog pd; 
    private String url;
    private long totalSize; 
	
    public HttpMultipartPost(Context context, String filePath,String url) {  
        this.context = context;  
        this.filePath = filePath;  
        this.url = url;
    }  
  
    @Override  
    protected void onPreExecute() {  
        pd = new ProgressDialog(context);  
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
        pd.setMessage("Uploading Picture...");  
        pd.setCancelable(false);  
        pd.show();  
    }  
    
    
	@Override
	protected String doInBackground(String... params) {
		
		String serverResponse = null;  
		  
        HttpClient httpClient = new DefaultHttpClient();  
        HttpContext httpContext = new BasicHttpContext();  
        HttpPost httpPost = new HttpPost(url);  
        //  设置HTTP POST请求参数必须用NameValuePair对象  
//        List<NameValuePair> param = new ArrayList<NameValuePair>();  
//        param.add(new BasicNameValuePair("fromUser", "1885"));
//        param.add(new BasicNameValuePair("toUser", "1881"));
//        try {
//			httpPost.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
        
//        httpPost.addHeader("fromUser", "1885");
//        httpPost.addHeader("toUser", "1881");
        
  
        try {  
            CustomMultipartEntity multipartContent = new CustomMultipartEntity(  
                    new ProgressListener() {  
                        @Override  
                        public void transferred(long num) {  
                            publishProgress((int) ((num / (float) totalSize) * 100));  
                        }  
                    });  
  
            multipartContent.addPart("fromUser", new StringBody("1885"));
            multipartContent.addPart("toUser", new StringBody("1881"));
//            params.put("token", token);
            
            // We use FileBody to transfer an image  
            multipartContent.addPart("uploadFile", new FileBody(new File(  
                    filePath)));  
           
            Log.e("Mqtt", " filePath::  "+filePath);
//            multipartContent.addPart("uploadFile",new FileBody(new File(filePath), "application/octet-stream", "UTF-8"));  
       
            totalSize = multipartContent.getContentLength();  
  
            // Send it  
            httpPost.setEntity(multipartContent);  
            HttpResponse response = httpClient.execute(httpPost, httpContext);  
            serverResponse = EntityUtils.toString(response.getEntity());  
            Log.e("Mqtt", " serverResponse::  "+serverResponse);
              
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return serverResponse;  
	}
	
	@Override  
    protected void onProgressUpdate(Integer... progress) {  
        pd.setProgress((int) (progress[0]));  
    }  
  
    @Override  
    protected void onPostExecute(String result) {  
        System.out.println("onPostExecute result: " + result);  
        pd.dismiss();  
    }  
  
    @Override  
    protected void onCancelled() {  
        System.out.println("cancle");  
    }  
	

}
