package com.xbd.kuk.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.xbd.kuk.R;
import com.xbd.kuk.bean.FriendInfo;
import com.xbd.kuk.datastart.DataUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * @author szk 主要用来显示头部图片
 * 
 */
public class ImageUtil {

	private final String TAG = "ImageUtil";
	private HashMap<String, Bitmap> imageMap = new HashMap<String, Bitmap>();
	private File cacheDir;
	private static ImageUtil instance;
	private Context mContext;
	private int taskCount = 0;
	private Object lock = new Object();

	/**
	 * 合成带阴影的图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap converShadowBitmap(Bitmap bitmap) {
		BlurMaskFilter blurFilter = new BlurMaskFilter(10,
				BlurMaskFilter.Blur.SOLID);
		Paint shadowPaint = new Paint();
		shadowPaint.setMaskFilter(blurFilter);
		shadowPaint.setColor(Color.BLACK);
		int[] offsetXY = new int[2];
		Bitmap originalBitmap = bitmap;
		Bitmap shadowImage = originalBitmap.extractAlpha(shadowPaint, offsetXY);
		Bitmap shadowImage32 = shadowImage.copy(Bitmap.Config.ARGB_8888, true);

		Canvas c = new Canvas(shadowImage32);
		c.drawBitmap(originalBitmap, -offsetXY[0], -offsetXY[1], null);
		return shadowImage32;
	}

	private ImageUtil(Context context) {
		mContext = context;
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = android.os.Environment.getExternalStorageDirectory();
			cacheDir = new File(sdDir, "/open/codehenge");
		} else {
			cacheDir = context.getCacheDir();
		}
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public static ImageUtil getInstance(Context context) {
		if (instance == null)
			instance = new ImageUtil(context);
		return instance;
	}

	public void displayImage(final ImageView imageView, final String url) {
		if (url == null) {
			imageView.setImageResource(R.drawable.img_user_defualt_icon);// 设置默认图片
		} else if (imageMap.containsKey(url)) {

			imageView.setImageBitmap(imageMap.get(url));

		} else {
			imageView.setImageResource(R.drawable.img_user_defualt_icon);// 设置默认图片

			new AsyncTask<String, Void, Bitmap>() {
				@Override
				protected Bitmap doInBackground(String... urls) {
					String filename = String.valueOf(url.hashCode());
					final File file = new File(cacheDir, filename);
					Bitmap bitmap;
					synchronized (ImageUtil.class) {
						// bitmap在缓存中？
						bitmap = BitmapFactory.decodeFile(file.getPath());
					}
					if (bitmap != null) {
						return bitmap;
					}
					// 否则，下载之
					try {
						bitmap = BitmapFactory.decodeStream(new URL(urls[0])
								.openConnection().getInputStream());
						synchronized ((ImageUtil.class)) {
							writeFile(bitmap, file);
						}
						return bitmap;
					} catch (Exception e) {
						e.printStackTrace();
						Log.e(TAG, "getBitmap() " + e.getMessage(), e);
						return null;
					}
				}

				@Override
				protected void onPostExecute(Bitmap result) {
					if (result != null) {
						// imageMap.put(url, result);
						imageView.setImageBitmap(result);
					}
				}
			}.execute(url);
		}

	}

	public void displaySetDrawable(final ImageView imageView, final String url) {
		if (url == null) {
			// imageView.setImageResource(Type);// 设置默认图片
		} else if (imageMap.containsKey(url)) {
			Drawable drawable = new BitmapDrawable(imageMap.get(url));
			imageView.setBackgroundDrawable(drawable);
		} else {
			// imageView.setImageResource(Type);// 设置默认图片

			new AsyncTask<String, Void, Bitmap>() {
				@Override
				protected Bitmap doInBackground(String... urls) {
					String filename = String.valueOf(url.hashCode());
					final File file = new File(cacheDir, filename);
					Bitmap bitmap;
					synchronized (ImageUtil.class) {
						// bitmap在缓存中？
						bitmap = BitmapFactory.decodeFile(file.getPath());
					}
					if (bitmap != null) {
						return bitmap;
					}
					// 否则，下载之
					try {

						bitmap = BitmapFactory.decodeStream(new URL(urls[0])
								.openConnection().getInputStream());
						synchronized ((ImageUtil.class)) {
							writeFile(bitmap, file);
						}
						return bitmap;
					} catch (Exception e) {
						e.printStackTrace();
						Log.e(TAG, "getBitmap() " + e.getMessage(), e);
						return null;
					}
				}

				@Override
				protected void onPostExecute(Bitmap result) {
					if (result != null) {
						imageMap.put(url, result);
						Drawable drawable = new BitmapDrawable(result);
						imageView.setBackgroundDrawable(drawable);
					}
				}
			}.execute(url);
		}

	}

	// szk 2012 -3-15 添加,因为图片默认显示不同
	public void displayImageTopic(final ImageView imageView, final String url) {
		if (url == null) {
			// imageView.setImageResource(Type);// 设置默认图片
		} else if (imageMap.containsKey(url)) {
			imageView.setImageBitmap(imageMap.get(url));
		} else {
			// imageView.setImageResource(Type);// 设置默认图片

			new AsyncTask<String, Void, Bitmap>() {
				@Override
				protected Bitmap doInBackground(String... urls) {
					String filename = String.valueOf(url.hashCode());
					final File file = new File(cacheDir, filename);
					Bitmap bitmap;
					synchronized (ImageUtil.class) {
						// bitmap在缓存中？
						bitmap = BitmapFactory.decodeFile(file.getPath());
					}
					if (bitmap != null) {
						return bitmap;
					}
					// 否则，下载之
					try {

						bitmap = BitmapFactory.decodeStream(new URL(urls[0])
								.openConnection().getInputStream());
						synchronized ((ImageUtil.class)) {
							writeFile(bitmap, file);
						}
						return bitmap;
					} catch (Exception e) {
						e.printStackTrace();
						Log.e(TAG, "getBitmap() " + e.getMessage(), e);
						return null;
					}
				}

				@Override
				protected void onPostExecute(Bitmap result) {
					if (result != null) {
						imageMap.put(url, result);
						imageView.setImageBitmap(result);
					}
				}
			}.execute(url);
		}

	}

	public void displayImageAccount(final ImageView imageView,
			final String url, int id) {
		if (url == null) {
			imageView.setImageResource(id);// 设置默认图片
		} else if (imageMap.containsKey(url)) {
			imageView.setImageBitmap(imageMap.get(url));
		} else {
			imageView.setImageResource(id);// 设置默认图片

			new AsyncTask<String, Void, Bitmap>() {
				@Override
				protected Bitmap doInBackground(String... urls) {
					String filename = String.valueOf(url.hashCode());
					final File file = new File(cacheDir, filename);
					Bitmap bitmap;
					synchronized (ImageUtil.class) {
						// bitmap在缓存中？
						bitmap = BitmapFactory.decodeFile(file.getPath());
					}
					if (bitmap != null) {
						return bitmap;
					}
					// 否则，下载之
					try {
						bitmap = BitmapFactory.decodeStream(new URL(urls[0])
								.openConnection().getInputStream());
						synchronized ((ImageUtil.class)) {
							writeFile(bitmap, file);
						}
						return bitmap;
					} catch (Exception e) {
						e.printStackTrace();
						Log.e(TAG, "getBitmap() " + e.getMessage(), e);
						return null;
					}
				}

				@Override
				protected void onPostExecute(Bitmap result) {
					if (result != null) {
						// imageMap.put(url, result);
						imageView.setImageBitmap(result);
					}
				}
			}.execute(url);
		}

	}

	public void displayVideoList(final View imageView, final String url, int id) {
		Log.d(TAG, "The current url" + url + taskCount);
		if (url == null) {
			imageView.setBackgroundResource(id);// 设置默认图片
		} else if (imageMap.containsKey(url)) {
			imageView.setBackgroundDrawable(new BitmapDrawable(imageMap
					.get(url)));
		} else {
			imageView.setBackgroundResource(id);// 设置默认图片
			new AsyncTask<String, Void, Bitmap>() {
				@Override
				protected Bitmap doInBackground(String... urls) {
					String filename = String.valueOf(url.hashCode());
					final File file = new File(cacheDir, filename);
					Bitmap bitmap;
					synchronized (ImageUtil.class) {
						// bitmap在缓存中？
						bitmap = BitmapFactory.decodeFile(file.getPath());
					}
					if (bitmap != null) {
						return bitmap;
					}
					// 否则，下载之
					try {
						bitmap = BitmapFactory.decodeStream(new URL(urls[0])
								.openConnection().getInputStream());
						synchronized ((ImageUtil.class)) {
							writeFile(bitmap, file);
						}
						return bitmap;
					} catch (Exception e) {
						e.printStackTrace();
						Log.e(TAG, "getBitmap() " + e.getMessage(), e);
						return null;
					}
				}

				@Override
				protected void onPostExecute(Bitmap result) {
					if (result != null) {

						imageMap.put(url, result);
						imageView.setBackgroundDrawable(new BitmapDrawable(
								result));
					}
				}
			}.execute(url);
		}
	}

	/**
	 * 
	 * @param imageView
	 * @param url
	 */
	public void setDrawableFromUrl(final ImageView imageView, final String url) {
		if (url == null) {
			return;
		} else {

			new AsyncTask<String, Void, Bitmap>() {
				@Override
				protected Bitmap doInBackground(String... urls) {
					// String filename = String.valueOf(url.hashCode());
					// final File file = new File(cacheDir, filename);
					Bitmap bitmap;
					// synchronized (ImageUtil.class) {
					// // bitmap在缓存中？
					// bitmap = BitmapFactory.decodeFile(file.getPath());
					// }
					// if (bitmap != null) {
					// return bitmap;
					// }
					// 否则，下载之
					try {

						bitmap = BitmapFactory.decodeStream(new URL(urls[0])
								.openConnection().getInputStream());
						// synchronized ((ImageUtil.class)) {
						// writeFile(bitmap, file);
						// }
						return bitmap;
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}

				@Override
				protected void onPostExecute(Bitmap result) {
					if (result != null) {
						imageMap.put(url, result);
						Drawable drawable = new BitmapDrawable(result);
						imageView.setBackgroundDrawable(drawable);
					}
				}
			}.execute(url);
		}

	}

	// 保存bitmap到缓存
	private void writeFile(Bitmap bitmap, File file) {
		Bitmap bitmap1 = BitmapFactory.decodeFile(file.getPath());
		if (bitmap1 == null) {
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
			} catch (Exception ex) {
				ex.printStackTrace();
				Log.e(TAG, "writeFile() " + ex.getMessage(), ex);
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (Exception ex2) {
					Log.e(TAG, "writeFile() " + ex2.getMessage(), ex2);
				}
			}
		}
	}

	// 照片保存的路径
	public File bornSolePath(String chatedId, String saveType, String fromType,
			String fileName) {
		// 如果在sd卡上不能创建成功，直接创建在程序内部
		final File savaPath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) || !isExternalStorageRemovable() ? getDerectory(chatedId)
				: mContext.getFilesDir();
		// 如果不能获取到正确的文件路径
		if (savaPath == null)
			return savaPath;
		File subDirectory = new File(savaPath, saveType);
		if (!subDirectory.exists())
			subDirectory.mkdir();

		File currentFile = null;
		if (fromType.equals(Constants.MessageFromType.MINE)) {
			try {
				currentFile = createImageFile(subDirectory);
			} catch (Exception e) {

				e.printStackTrace();

			}
		} else if (fromType.equals(Constants.MessageFromType.FRIEND)) {
			currentFile = new File(subDirectory, fileName);
		} else if (fromType.equals(Constants.MessageFromType.FRIEND)) {
			currentFile = new File(subDirectory, fileName);
		}

		return currentFile;
	}

	private File createImageFile(File directory) throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = timeStamp + "_";
		File image = File.createTempFile(imageFileName, ".jpg", directory);
		return image;
	}

	// 如果sdk版本大于9
	@TargetApi(9)
	public static boolean isExternalStorageRemovable() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	/**
	 * 获取本地图片的地址
	 * 
	 * @param url
	 * @return
	 */
	private String getUserIconInfo(Context context, String userId,
			String iconUrl) {

		FriendInfo friendInfo = DataUtils.getInstance(context)
				.QueryFriendInfo(userId);
		// 判断本地数据库中当前还有是否是第一次显示
		if (friendInfo == null) {
			return null;
		}
		String localUrl = friendInfo.getmFriendUserLocalIconUrl();
		String netUrl = friendInfo.getmFriendUserIconUrl();
		// 判断是否存在本地和网络地址
		if (localUrl != null || netUrl == null) {
			return null;
		}
		// 判断本地文件是否存在
		File file = new File(localUrl);
		if (!file.exists()) {
			return null;
		}
		// 判断当前图片是否被修改
		if (!netUrl.equalsIgnoreCase(iconUrl)) {
			return null;
		}
		return localUrl;
	}

	public File getDerectory(String chatedId) {
		// 如果在sd卡上不能创建成功，直接创建在程序内部
		File fileDirectory = mContext.getExternalFilesDir(chatedId);
		if (!fileDirectory.exists()) {
			fileDirectory = mContext.getFilesDir();
		}
		return fileDirectory;
	}

	/**
	 * 获取用户图像，会在本地和数据库保存。
	 * 
	 * @param imageView
	 * @param url
	 * @param id
	 */
	public void setUserIconImage(final View imageView, final String url,
			final String userId) {
      
		if (url == null || "".equals(url)) {
			imageView.setBackgroundResource(R.drawable.img_user_defualt_icon);
		} else if (imageMap.containsKey(url)) {
			imageView.setBackgroundDrawable(new BitmapDrawable(imageMap
					.get(url)));
		} else {

			new AsyncTask<String, Void, Bitmap>() {
				@Override
				protected Bitmap doInBackground(String... urls) {

					String filename = String.valueOf(url.substring(url
							.lastIndexOf("/") + 1));
					String localUrl = getUserIconInfo(mContext, userId, urls[0]);
					File iconFile = null;
					if (localUrl == null) {
						iconFile = new File(cacheDir, filename);
					} else {
						iconFile = new File(localUrl);
					}
					Bitmap bitmap = null;

					if (iconFile.exists()) {
						synchronized (ImageUtil.class) {
							// bitmap在缓存中？
							try {
								bitmap = BitmapFactory
										.decodeStream(new FileInputStream(
												iconFile));
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if (bitmap != null && localUrl == null) {
							FriendInfo friendInfo = new FriendInfo();
							friendInfo.setmFriendId(userId);
							friendInfo.setmFriendUserIconUrl(urls[0]);
							friendInfo.setmFriendUserLocalIconUrl(iconFile
									.getPath());
							DataUtils.getInstance(mContext).insertFriendInfo(
									friendInfo);
							return bitmap;
						}
					}

					// 否则，下载之

					try {
						bitmap = BitmapFactory.decodeStream(new URL(urls[0])
								.openConnection().getInputStream());
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (bitmap != null) {
						synchronized ((ImageUtil.class)) {
							writeFile(bitmap, iconFile);
						}
						// 添加到本地图像文件数据库
						FriendInfo friendInfo = new FriendInfo();
						friendInfo.setmFriendId(userId);
						friendInfo.setmFriendUserIconUrl(urls[0]);
						friendInfo.setmFriendUserLocalIconUrl(iconFile
								.getPath());
						DataUtils.getInstance(mContext).insertFriendInfo(
								friendInfo);
						return bitmap;
					}
					return null;

				}

				@Override
				protected void onPostExecute(Bitmap result) {
					if (result != null) {

						imageMap.put(url, result);
						imageView.setBackgroundDrawable(new BitmapDrawable(
								result));
					} else {
						imageView
								.setBackgroundResource(R.drawable.img_user_defualt_icon);
					}
				}
			}.execute(url);
		}
	}

	/**
	 * 
	 * @ClassName: OBAsyncTask
	 * @Description: TODO
	 * @author zhangchunzhe
	 * @date 2013-12-11 下午7:25:18
	 * 
	 */
	private class OBAsyncTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
