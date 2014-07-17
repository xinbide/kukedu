package com.xbd.kuk.datastart;

import java.util.ArrayList;
import java.util.List;

import com.xbd.kuk.bean.FriendInfo;
import com.xbd.kuk.bean.FriendMessage;
import com.xbd.kuk.model.User;
import com.xbd.kuk.model.UserProfessionItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class DataUtils {
	private static DataUtils mDataUtils;
	private DataManager mOBDataManager;

	public static DataUtils getInstance(Context context) {
		if (mDataUtils == null) {
			mDataUtils = new DataUtils(context);
		}
		return mDataUtils;

	}

	private DataUtils(Context context) {
		mOBDataManager = new DataManager(context);
	}

	public String getQueryString(String key, String value) {
		return new StringBuffer(key).append("='").append(value).append("'")
				.toString();
	}

	public String getQueryInt(String key, int value) {
		return new StringBuffer(key).append("=").append(value).toString();
	}

	public String getQueryLong(String key, int value) {
		return new StringBuffer(key).append("=").append(value).toString();
	}

	public String getArrayString(String... strings) {
		StringBuffer arrayBuffer = new StringBuffer();
		for (int i = 0; i < strings.length; i++) {
			arrayBuffer.append(strings[i]);
			if (i == strings.length - 1) {
				return arrayBuffer.toString();
			} else {
				arrayBuffer.append(" and ");
			}
		}
		return arrayBuffer.toString();

	}

	// 用户帐户信息的保存
	public String mStrAccoutName = "";
	public String mStrAccoutCode = "";
	public String mIsSaveInfo = "";

	// 检验用户信息表里面是否有数据
	public boolean isCheckSaveAccout()

	{
		boolean temp = false;
		Cursor cursor = mOBDataManager.qurey(
				DataManager.LoginInfo.TABLE_NAME, null, null, null, null,
				null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				mStrAccoutName = cursor.getString(cursor
						.getColumnIndex(DataManager.LoginInfo.USER_NAME));
				mStrAccoutCode = cursor.getString(cursor
						.getColumnIndex(DataManager.LoginInfo.USER_PWD));
				mIsSaveInfo = cursor.getString(cursor
						.getColumnIndex(DataManager.LoginInfo.USER_ISSAVED));
				temp = true;
			}
			cursor.close();
		}
		return temp;
	}

	// 检验当前用户是否在数据库
//	public boolean isUserInfoExist(OBBarUser user) {
//		boolean isExist = false;
//		String whereArg = getQueryString(OBDataManager.LoginInfo.USER_NAME,
//				user.userName);
//		Cursor cursor = mOBDataManager.qurey(
//				OBDataManager.LoginInfo.TABLE_NAME, null, whereArg, null, null,
//				null, null);
//		if (cursor != null) {
//			if (cursor.moveToFirst()) {
//				isExist = true;
//			}
//			cursor.close();
//
//		}
//		return isExist;
//	}

	// 检验当前用户是否在数据库
	public User getLoginUserInfo() {
		boolean isExist = false;

		Cursor cursor = mOBDataManager.qurey(
				DataManager.LoginInfo.TABLE_NAME, null, null, null, null,
				null, null);
		User barUser = null;
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				barUser = new User();
				barUser.userName = cursor.getString(cursor
						.getColumnIndex(DataManager.LoginInfo.USER_NAME));
				barUser.password = cursor.getString(cursor
						.getColumnIndex(DataManager.LoginInfo.USER_PWD));
				barUser.userId = cursor.getString(cursor
						.getColumnIndex(DataManager.LoginInfo.USER_ID));
				barUser.userBaseID = cursor.getString(cursor
						.getColumnIndex(DataManager.LoginInfo.USER_BASE_ID));
				barUser.userRoleType = cursor.getString(cursor
						.getColumnIndex(DataManager.LoginInfo.USER_ROLE));
				barUser.faceUrl = cursor.getString(cursor
						.getColumnIndex(DataManager.LoginInfo.USER_ICON));
				barUser.nickname = cursor.getString(cursor
						.getColumnIndex(DataManager.LoginInfo.USER_NICENAME));
				barUser.token = cursor.getString(cursor
						.getColumnIndex(DataManager.LoginInfo.USER_TOKEN));
				UserProfessionItem item = new UserProfessionItem();
				item.jProfessionName = cursor
						.getString(cursor
								.getColumnIndex(DataManager.LoginInfo.USER_PROFESSION_NAME));
				item.jProfessionStatus = cursor
						.getString(cursor
								.getColumnIndex(DataManager.LoginInfo.USER_PROFESSION_STATE));

				item.jStudentCode = cursor
						.getString(cursor
								.getColumnIndex(DataManager.LoginInfo.USER_STUDENT_CODE));
				if (item.jStudentCode != null) {
					barUser.setProfession(item);
				}

			}
			cursor.close();
		}
		return barUser;
	}

	// 删除用户信息表
	public void deleteObUserInfo() {
		mOBDataManager.delete(DataManager.LoginInfo.TABLE_NAME, "", null);
	}

	// 数据写入用户信息表
//	public void insertObUserItem(OBBarUser userInfo, boolean isSave) {
//		ContentValues contentValue = new ContentValues();
//		contentValue.put(OBDataManager.LoginInfo.USER_NAME, userInfo.userName);
//		contentValue.put(OBDataManager.LoginInfo.USER_PWD, userInfo.password);
//		contentValue.put(OBDataManager.LoginInfo.USER_ID, userInfo.userId);
//
//		contentValue.put(OBDataManager.LoginInfo.USER_ROLE,
//				userInfo.userRoleType);
//
//		contentValue.put(OBDataManager.LoginInfo.USER_BASE_ID,
//				userInfo.userBaseID);
//		contentValue.put(OBDataManager.LoginInfo.USER_ICON, userInfo.faceUrl);
//
//		contentValue.put(OBDataManager.LoginInfo.USER_NICENAME,
//				userInfo.nickname);
//		contentValue.put(OBDataManager.LoginInfo.USER_TOKEN, userInfo.token);
//		if (Constants.USER_ROLE_STUDENT.equals(userInfo.userRoleType)
//				&& userInfo.getmProfession() != null) {
//
//			contentValue.put(OBDataManager.LoginInfo.USER_STUDENT_CODE,
//					userInfo.getmProfession().jStudentCode);
//			contentValue.put(OBDataManager.LoginInfo.USER_PROFESSION_NAME,
//					userInfo.getmProfession().jProfessionName);
//			contentValue.put(OBDataManager.LoginInfo.USER_PROFESSION_STATE,
//					userInfo.getmProfession().jProfessionStatus);
//		}
//		
//
//		contentValue.put(OBDataManager.LoginInfo.USER_ISSAVED,
//				isSave == true ? "1" : "0");
//		mOBDataManager.insert(OBDataManager.LoginInfo.TABLE_NAME, null,
//				contentValue);
//	}
//
//	// 更新用户信息表
//
//	/**
//	 * 
//	 * @param userInfo
//	 * @param isSave
//	 */
//	public void updateObUserItem(OBBarUser userInfo, boolean isSave) {
//		String whereArgs = getQueryString(OBDataManager.LoginInfo.USER_NAME,
//				userInfo.userName);
//		ContentValues contentValue = new ContentValues();
//		contentValue.put(OBDataManager.LoginInfo.USER_ISSAVED,
//				isSave == true ? "1" : "0");
//		mOBDataManager.update(OBDataManager.LoginInfo.TABLE_NAME, contentValue,
//				whereArgs, null);
//	}

	/**
	 * 
	 * @param token
	 * @param userName
	 */
	public void updateUserToken(String token, String userName) {
		String whereArgs = getQueryString(DataManager.LoginInfo.USER_NAME,
				userName);
		ContentValues contentValue = new ContentValues();
		contentValue.put(DataManager.LoginInfo.USER_TOKEN, token);
		mOBDataManager.update(DataManager.LoginInfo.TABLE_NAME, contentValue,
				whereArgs, null);
	}

	// 下载资源的管理
	/**
	 * 获取不同的状态的下载列表
	 * 
	 * @param secondStatus
	 * @param userId
	 * @return list<OBDownloadFile>
	 */

//	public List<OBDownloadFile> queryDownloadByUserid(int secondStatus,
//			String userId) {
//		List<OBDownloadFile> listRecord = new ArrayList<OBDownloadFile>();
//		Cursor statusRecord = queryDownloadByStatus(secondStatus, userId);
//		if (statusRecord != null) {
//			if (statusRecord.moveToFirst()) {
//				do {
//					listRecord.add(putDownloadDBData(statusRecord));
//				} while (statusRecord.moveToNext());
//			}
//			statusRecord.close();
//		}
//
//		return listRecord;
//	}
//
//	/**
//	 * 获取不同的状态的下载列表
//	 * 
//	 * @param secondStatus
//	 * @param userId
//	 * @return list<OBDownloadFile>
//	 */
//
//	public List<OBDownloadFile> querySuccessByUserid(int secondStatus,
//			String userId) {
//		List<OBDownloadFile> listRecord = new ArrayList<OBDownloadFile>();
//		String whereArgs = getArrayString(
//				(getQueryInt(OBDataManager.DownloadManager.FILE_SECOND_STATUS,
//						secondStatus)), currentUserIdWhereArgs(userId));
//		String orderArgs = OBDataManager.DownloadManager.UPLOAD_DATE + " DESC";
//		Cursor statusRecord = mOBDataManager.qurey(
//				OBDataManager.DownloadManager.TABLE_NAME, null, whereArgs,
//				null, null, null, orderArgs);
//		if (statusRecord != null) {
//			if (statusRecord.moveToFirst()) {
//				do {
//					listRecord.add(putDownloadDBData(statusRecord));
//				} while (statusRecord.moveToNext());
//			}
//			statusRecord.close();
//		}
//
//		return listRecord;
//	}

	/**
	 * 数据库查询的操作
	 * 
	 * @param secondStatus
	 * @param userId
	 * @return Cursor
	 */
	private Cursor queryDownloadByStatus(int secondStatus, String userId) {
		String whereArgs = getArrayString(
				(getQueryInt(DataManager.DownloadManager.FILE_SECOND_STATUS,
						secondStatus)), currentUserIdWhereArgs(userId));
		return mOBDataManager.qurey(DataManager.DownloadManager.TABLE_NAME,
				null, whereArgs, null, null, null, null);
	}

	/**
	 * 获取当前用户的所有下载资源数目
	 * 
	 * @param userId
	 * @return
	 */
	public int queryDownloadAudioAccount(String userId) {
		String whereArgs = currentUserIdWhereArgs(userId);
		Cursor statusRecord = mOBDataManager.qurey(
				DataManager.DownloadManager.TABLE_NAME, null, whereArgs,
				null, null, null, null);
		int appCount = 0;
		if (statusRecord != null) {
			appCount = statusRecord.getCount();
			statusRecord.close();
		}
		return appCount;
	}

	/**
	 * 获取符合当前第一状态和第2状态资源数量
	 * 
	 * @param firstStatus
	 * @param secondStatus
	 * @param userId
	 * @return
	 */
	public int queryFirstAndSecondNum(int firstStatus, int secondStatus,
			String userId) {
		String whereArgs = getArrayString(
				getQueryInt(DataManager.DownloadManager.FILE_STATUS,
						firstStatus),
				getQueryInt(DataManager.DownloadManager.FILE_SECOND_STATUS,
						secondStatus), currentUserIdWhereArgs(userId));
		Cursor statusRecord = mOBDataManager.qurey(
				DataManager.DownloadManager.TABLE_NAME, null, whereArgs,
				null, null, null, null);
		int appCount = 0;
		if (statusRecord != null) {
			appCount = statusRecord.getCount();
			statusRecord.close();
		}
		return appCount;
	}

	/**
	 * 获取符合当前第一状态和第2状态的当前用户的详情
	 * 
	 * @param firstStatus
	 * @param secondStatus
	 * @param userId
	 * @return
	 */
//	public List<OBDownloadFile> queryFirstAndSecondItem(int firstStatus,
//			int secondStatus, String userId) {
//		List<OBDownloadFile> OBDownloadFile = new ArrayList<OBDownloadFile>();
//		String whereArgs = getArrayString(
//				getQueryInt(OBDataManager.DownloadManager.FILE_STATUS,
//						firstStatus),
//				getQueryInt(OBDataManager.DownloadManager.FILE_SECOND_STATUS,
//						secondStatus), currentUserIdWhereArgs(userId));
//		String orderArgs = OBDataManager.DownloadManager.UPLOAD_DATE + " ASC";
//		Cursor statusRecord = mOBDataManager.qurey(
//				OBDataManager.DownloadManager.TABLE_NAME, null, whereArgs,
//				null, null, null, orderArgs);
//		if (statusRecord != null) {
//			if (statusRecord.moveToFirst()) {
//				do {
//					OBDownloadFile.add(putDownloadDBData(statusRecord));
//				} while (statusRecord.moveToNext());
//			}
//			statusRecord.close();
//		}
//		return OBDownloadFile;
//	}
//
//	/**
//	 * 根据具体文件的Url获取详细信息
//	 * 
//	 * @param Url
//	 * @param userId
//	 * @return OBDownloadFile
//	 */
//	public OBDownloadFile queryDownloadByFileUrl(String Url, String userId,
//			String studyCourseId, String fileId) {
//		OBDownloadFile OBDownloadFile = new OBDownloadFile();
//		String whereArgs = getArrayString(
//				(getQueryString(OBDataManager.DownloadManager.FILE_URL, Url)),
//				currentUserIdWhereArgs(userId),
//				(getQueryString(OBDataManager.DownloadManager.STUDENT_CODE,
//						studyCourseId)),
//				(getQueryString(OBDataManager.DownloadManager.FILE_ID, fileId)));
//		Cursor statusRecord = mOBDataManager.qurey(
//				OBDataManager.DownloadManager.TABLE_NAME, null, whereArgs,
//				null, null, null, null);
//		if (statusRecord != null) {
//			if (statusRecord.moveToFirst()) {
//				do {
//					OBDownloadFile = putDownloadDBData(statusRecord);
//					break;
//				} while (statusRecord.moveToNext());
//			}
//			statusRecord.close();
//		}
//
//		return OBDownloadFile;
//	}
//
//	/**
//	 * 根据具体文档文件的Url详细信息
//	 * 
//	 * @param Url
//	 * @param userId
//	 * @param studyCourseId
//	 * @param fileId
//	 * @param fileName
//	 * @return OBDownloadFile
//	 */
//	public OBDownloadFile queryDownloadDocFileUrl(String Url, String userId,
//			String studyCourseId, String fileId, String fileName) {
//		OBDownloadFile OBDownloadFile = new OBDownloadFile();
//		String whereArgs = getArrayString(
//				(getQueryString(OBDataManager.DownloadManager.FILE_URL, Url)),
//				currentUserIdWhereArgs(userId),
//				(getQueryString(OBDataManager.DownloadManager.STUDENT_CODE,
//						studyCourseId)),
//				(getQueryString(OBDataManager.DownloadManager.FILE_ID, fileId)),
//				(getQueryString(OBDataManager.DownloadManager.FILE_NAME,
//						fileName)));
//		Cursor statusRecord = mOBDataManager.qurey(
//				OBDataManager.DownloadManager.TABLE_NAME, null, whereArgs,
//				null, null, null, null);
//		if (statusRecord != null) {
//			if (statusRecord.moveToFirst()) {
//				do {
//					OBDownloadFile = putDownloadDBData(statusRecord);
//					break;
//				} while (statusRecord.moveToNext());
//			}
//			statusRecord.close();
//		}
//
//		return OBDownloadFile;
//	}
//
//	/**
//	 * 根据具体文件的Url获取详细信息
//	 * 
//	 * @param Url
//	 * @param userId
//	 * @return OBDownloadFile
//	 */
//	public OBDownloadFile queryDownloadByCoursewareID(String fileId,
//			String userId) {
//		OBDownloadFile OBDownloadFile = new OBDownloadFile();
//		String whereArgs = getArrayString(
//				(getQueryString(OBDataManager.DownloadManager.FILE_ID, fileId)),
//				currentUserIdWhereArgs(userId));
//		Cursor statusRecord = mOBDataManager.qurey(
//				OBDataManager.DownloadManager.TABLE_NAME, null, whereArgs,
//				null, null, null, null);
//		if (statusRecord != null) {
//			if (statusRecord.moveToFirst()) {
//				do {
//					OBDownloadFile = putDownloadDBData(statusRecord);
//					break;
//				} while (statusRecord.moveToNext());
//			}
//			statusRecord.close();
//		}
//
//		return OBDownloadFile;
//	}

	/**
	 * 获取当前文件的下载进度
	 * 
	 * @param Url
	 * @param userId
	 * @return OBDownloadFile
	 */
	public int queryDownloadByPos(String Url, String userId,
			String studyCourseId, String fileId) {
		int startPos = 0;
		String whereArgs = getArrayString(
				(getQueryString(DataManager.DownloadManager.FILE_URL, Url)),
				currentUserIdWhereArgs(userId),
				(getQueryString(DataManager.DownloadManager.STUDENT_CODE,
						studyCourseId)),
				(getQueryString(DataManager.DownloadManager.FILE_ID, fileId)));
		Cursor statusRecord = mOBDataManager.qurey(
				DataManager.DownloadManager.TABLE_NAME, null, whereArgs,
				null, null, null, null);
		if (statusRecord != null) {
			if (statusRecord.moveToFirst()) {
				do {
					startPos = statusRecord
							.getInt(statusRecord
									.getColumnIndex(DataManager.DownloadManager.FILE_START_POS));
					break;
				} while (statusRecord.moveToNext());
			}
			statusRecord.close();
		}

		return startPos;
	}

	/**
	 * 获取具体文件在数据库中的Id
	 * 
	 * @param Url
	 * @param userId
	 * @return int
	 */
	public int queryDownloadByFileUrluserId(String Url, String userId,
			String studyCourseId, String fileId) {
		int identifyId = 0;
		String whereArgs = getArrayString(
				(getQueryString(DataManager.DownloadManager.FILE_URL, Url)),
				currentUserIdWhereArgs(userId),
				(getQueryString(DataManager.DownloadManager.STUDENT_CODE,
						studyCourseId)),
				(getQueryString(DataManager.DownloadManager.FILE_ID, fileId)));
		Cursor statusRecord = mOBDataManager.qurey(
				DataManager.DownloadManager.TABLE_NAME, null, whereArgs,
				null, null, null, null);
		if (statusRecord != null) {
			if (statusRecord.moveToFirst()) {
				do {
					identifyId = statusRecord.getInt(statusRecord
							.getColumnIndex(DataManager.DownloadManager._ID));
					break;
				} while (statusRecord.moveToNext());
			}
			statusRecord.close();
		}

		return identifyId;
	}

	/**
	 * 获取具体文件的第一数据状态
	 * 
	 * @param Url
	 * @param userId
	 * @return int
	 */
	public int queryDownloadByFileIdUserId(int FileId, String userId) {
		int fileStatus = 0;
		String whereArgs = getArrayString(
				getQueryInt(DataManager.DownloadManager._ID, FileId),
				currentUserIdWhereArgs(userId));
		Cursor statusRecord = mOBDataManager.qurey(
				DataManager.DownloadManager.TABLE_NAME, null, whereArgs,
				null, null, null, null);
		if (statusRecord != null) {
			if (statusRecord.moveToFirst()) {
				do {
					fileStatus = statusRecord
							.getInt(statusRecord
									.getColumnIndex(DataManager.DownloadManager.FILE_STATUS));
					break;
				} while (statusRecord.moveToNext());
			}
			statusRecord.close();
		}

		return fileStatus;
	}

	/**
	 * 获取符合第2文件状态的下载文件数量
	 * 
	 * @param secondStatus
	 * @param userId
	 * @return int
	 */
	public int querySecondStatusNum(int secondStatus, String userId) {
		int totalNum = 0;
		String whereArgs = getArrayString(
				(getQueryInt(DataManager.DownloadManager.FILE_SECOND_STATUS,
						secondStatus)), currentUserIdWhereArgs(userId));
		Cursor statusRecord = mOBDataManager.qurey(
				DataManager.DownloadManager.TABLE_NAME, null, whereArgs,
				null, null, null, null);
		if (statusRecord != null) {
			totalNum = statusRecord.getCount();
			statusRecord.close();
		}
		return totalNum;
	}

	/**
	 * 获取符合第2文件状态详细文件
	 * 
	 * @param secondStatus
	 * @param userId
	 * @return List<OBDownloadFile>
	 */
//	public List<OBDownloadFile> querySecondStatusList(int secondStatus,
//			String userId) {
//		List<OBDownloadFile> listItem = new ArrayList<OBDownloadFile>();
//		String whereArgs = getArrayString(
//				(getQueryInt(OBDataManager.DownloadManager.FILE_SECOND_STATUS,
//						secondStatus)), currentUserIdWhereArgs(userId));
//		Cursor statusRecord = mOBDataManager.qurey(
//				OBDataManager.DownloadManager.TABLE_NAME, null, whereArgs,
//				null, null, null, null);
//		if (statusRecord != null) {
//			if (statusRecord.moveToFirst()) {
//				do {
//
//					listItem.add(putDownloadDBData(statusRecord));
//				} while (statusRecord.moveToNext());
//			}
//			statusRecord.close();
//		}
//		return listItem;
//	}
//
//	/**
//	 * @param cursor
//	 * @return
//	 */
//
//	public OBDownloadFile putDownloadDBData(Cursor cursor) {
//		OBDownloadFile OBDownloadFile = new OBDownloadFile();
//		int identifyId = cursor.getInt(cursor
//				.getColumnIndex(OBDataManager.DownloadManager._ID));
//		String courseId = cursor.getString(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.COURSER_ID));
//		String courseName = cursor.getString(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.COURSER_NAME));
//		String userId = cursor.getString(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.USER_ID));
//		String studentCode = cursor.getString(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.STUDENT_CODE));
//		String fileId = cursor.getString(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.FILE_ID));
//		String fileUrl = cursor.getString(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.FILE_URL));
//		String fileName = cursor.getString(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.FILE_NAME));
//		int fileType = cursor.getInt(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.FILE_TYPE));
//		int fileSize = cursor.getInt(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.FILE_SIZE));
//		int fileStartPos = cursor.getInt(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.FILE_START_POS));
//		int fileEndPos = cursor.getInt(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.FILE_ENd_POS));
//		int fileStatus = cursor.getInt(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.FILE_STATUS));
//
//		int fileSecondStatus = cursor
//				.getInt(cursor
//						.getColumnIndex(OBDataManager.DownloadManager.FILE_SECOND_STATUS));
//
//		long uploadDate = cursor.getLong(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.UPLOAD_DATE));
//		int progressPar = cursor.getInt(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.PROGRESS_PAR));
//		int remainSize = cursor.getInt(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.REMAIN_SIZE));
//		int remainTime = cursor.getInt(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.REMAIN_TIME));
//		int remainRate = cursor.getInt(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.REMAIN_RATE));
//		int dataSize = cursor.getInt(cursor
//				.getColumnIndex(OBDataManager.DownloadManager.DATA_SIZE));
//		OBDownloadFile.setId(Integer.toString(identifyId));
//		OBDownloadFile.userId = userId;
//		OBDownloadFile.courseId = courseId;
//		OBDownloadFile.courseName = courseName;
//		OBDownloadFile.studentCode = studentCode;
//		OBDownloadFile.fileId = fileId;
//		OBDownloadFile.fileUrl = fileUrl;
//		OBDownloadFile.fileName = fileName;
//		OBDownloadFile.fileType = fileType;
//		OBDownloadFile.fileSize = fileSize;
//		OBDownloadFile.fileStartPos = fileStartPos;
//		OBDownloadFile.fileEndPos = fileEndPos;
//		OBDownloadFile.fileStatus = fileStatus;
//		OBDownloadFile.fileSecondStatus = fileSecondStatus;
//		OBDownloadFile.uploadData = uploadDate;
//		OBDownloadFile.PrgogressRate = progressPar;
//		OBDownloadFile.RemainSize = remainSize;
//		OBDownloadFile.RemainTime = remainTime;
//		OBDownloadFile.RemainRate = remainRate;
//		OBDownloadFile.DataSize = dataSize;
//		return OBDownloadFile;
//	}

	/**
	 * 判定某一下载地址是否存在数据库
	 * 
	 * @param downloadUrl
	 * @param userId
	 * @return
	 */
	public boolean isExitsOBDownloadFile(String downloadUrl, String userId,
			String studyCourseId, String fileId) {

		String whereArgs = getArrayString(
				getQueryString(DataManager.DownloadManager.FILE_URL,
						downloadUrl),
				currentUserIdWhereArgs(userId),
				getQueryString(DataManager.DownloadManager.STUDENT_CODE,
						studyCourseId),
				getQueryString(DataManager.DownloadManager.FILE_ID, fileId));
		Cursor cursor = mOBDataManager.qurey(
				DataManager.DownloadManager.TABLE_NAME, null, whereArgs,
				null, null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				cursor.close();
				return true;
			}
		}
		cursor.close();
		return false;
	}

	/**
	 * 判定某一文本文档下载地址是否存在数据库
	 * 
	 * @param downloadUrl
	 * @param userId
	 * @param studyCourseId
	 * @param fileId
	 * @param fileName
	 * @return
	 */
	public boolean isExitsDocDownloadFile(String downloadUrl, String userId,
			String studyCourseId, String fileId, String fileName) {

		String whereArgs = getArrayString(
				getQueryString(DataManager.DownloadManager.FILE_URL,
						downloadUrl),
				currentUserIdWhereArgs(userId),
				getQueryString(DataManager.DownloadManager.STUDENT_CODE,
						studyCourseId),
				getQueryString(DataManager.DownloadManager.FILE_ID, fileId),
				getQueryString(DataManager.DownloadManager.FILE_NAME,
						fileName));
		Cursor cursor = mOBDataManager.qurey(
				DataManager.DownloadManager.TABLE_NAME, null, whereArgs,
				null, null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				cursor.close();
				return true;
			}
		}
		cursor.close();
		return false;
	}

	/**
	 * 下载初始化的时候更新实际文件的大小
	 * 
	 * @param downloadUrl
	 * @param userId
	 * @param filesize
	 * @return int
	 */
	public synchronized int updateFileSize(int filesize, int remainSize,
			String downloadUrl, String userId, String studyCourseId,
			String fileId)

	{
		String whereArgs = getArrayString(
				(getQueryString(DataManager.DownloadManager.FILE_URL,
						downloadUrl)),
				currentUserIdWhereArgs(userId),
				(getQueryString(DataManager.DownloadManager.STUDENT_CODE,
						studyCourseId)),
				getQueryString(DataManager.DownloadManager.FILE_ID, fileId));
		ContentValues contentValues = new ContentValues();
		contentValues.put(DataManager.DownloadManager.FILE_SIZE, filesize);
		contentValues
				.put(DataManager.DownloadManager.REMAIN_SIZE, remainSize);
		// Log.d("DBUtils", "The filesize is VALUE "+filesize);
		// Log.d("DBUtils", "The data is inserting");
		return mOBDataManager.update(DataManager.DownloadManager.TABLE_NAME,
				contentValues, whereArgs, null);
	}

	/**
	 * 实施更新资源的当前下载进度
	 * 
	 * @param downloadUrl
	 * @param userId
	 * @param startPos
	 * @return int
	 */
	public synchronized int updateFileCurrentPos(int startPos, int fileStatus,
			String downloadUrl, String userId, String studyCourseId,
			String fileId) {
		String whereArgs = getArrayString(
				getQueryString(DataManager.DownloadManager.FILE_URL,
						downloadUrl),
				currentUserIdWhereArgs(userId),
				getQueryString(DataManager.DownloadManager.STUDENT_CODE,
						studyCourseId),
				getQueryString(DataManager.DownloadManager.FILE_ID, fileId));
		ContentValues contentValues = new ContentValues();
		contentValues.put(DataManager.DownloadManager.FILE_START_POS,
				startPos);
		contentValues
				.put(DataManager.DownloadManager.FILE_STATUS, fileStatus);
		return mOBDataManager.update(DataManager.DownloadManager.TABLE_NAME,
				contentValues, whereArgs, null);
	}

	/**
	 * 实施更新资源的当前下载进度
	 * 
	 * @param downloadUrl
	 * @param userId
	 * @param startPos
	 * @return int
	 */
	public synchronized int updateFileCurrentPosUsual(int startPos,
			int remainSize, int progressRate, int remainRate, int remainTime,
			String downloadUrl, String userId, String studyCourseId,
			String fileId) {
		String whereArgs = getArrayString(
				getQueryString(DataManager.DownloadManager.FILE_URL,
						downloadUrl),
				currentUserIdWhereArgs(userId),
				getQueryString(DataManager.DownloadManager.STUDENT_CODE,
						studyCourseId),
				getQueryString(DataManager.DownloadManager.FILE_ID, fileId));
		ContentValues contentValues = new ContentValues();
		contentValues.put(DataManager.DownloadManager.FILE_START_POS,
				startPos);
		contentValues
				.put(DataManager.DownloadManager.REMAIN_SIZE, remainSize);
		contentValues.put(DataManager.DownloadManager.PROGRESS_PAR,
				progressRate);
		contentValues
				.put(DataManager.DownloadManager.REMAIN_RATE, remainRate);
		contentValues
				.put(DataManager.DownloadManager.REMAIN_TIME, remainTime);
		return mOBDataManager.update(DataManager.DownloadManager.TABLE_NAME,
				contentValues, whereArgs, null);
	}

	/**
	 * 文件停止下载的时候保存下载进度
	 * 
	 * @param progressRate
	 * @param remainRate
	 * @param remainTime
	 * @param downloadUrl
	 * @param userId
	 * @return int
	 */
	public synchronized int updateSaveFileProgress(int startPos,
			int stateStatus, int remainSize, int progressRate, int remainRate,
			int remainTime, String downloadUrl, String userId,
			String studyCourseId, String fileId) {
		String whereArgs = getArrayString(
				getQueryString(DataManager.DownloadManager.FILE_URL,
						downloadUrl),
				currentUserIdWhereArgs(userId),
				getQueryString(DataManager.DownloadManager.STUDENT_CODE,
						studyCourseId),
				getQueryString(DataManager.DownloadManager.FILE_ID, fileId));
		ContentValues contentValues = new ContentValues();
		contentValues.put(DataManager.DownloadManager.FILE_START_POS,
				startPos);
		contentValues.put(DataManager.DownloadManager.FILE_STATUS,
				stateStatus);
		contentValues
				.put(DataManager.DownloadManager.REMAIN_SIZE, remainSize);
		contentValues.put(DataManager.DownloadManager.PROGRESS_PAR,
				progressRate);
		contentValues
				.put(DataManager.DownloadManager.REMAIN_RATE, remainRate);
		contentValues
				.put(DataManager.DownloadManager.REMAIN_TIME, remainTime);
		return mOBDataManager.update(DataManager.DownloadManager.TABLE_NAME,
				contentValues, whereArgs, null);
	}

	/**
	 * 更新符合资源文件地址和用户这两个条件的资源文件的下载进度、第一状态、第2状态
	 * 
	 * @param downloadUrl
	 * @param userId
	 * @param startPos
	 * @return int
	 */
	public synchronized int updateFileFinish(int startPos, int fileStatus,
			int fileSecondStatus, String downloadUrl, String userId,
			String studyCourseId, String fileId) {
		String whereArgs = getArrayString(
				getQueryString(DataManager.DownloadManager.FILE_URL,
						downloadUrl),
				currentUserIdWhereArgs(userId),
				getQueryString(DataManager.DownloadManager.STUDENT_CODE,
						studyCourseId),
				getQueryString(DataManager.DownloadManager.FILE_ID, fileId));
		ContentValues contentValues = new ContentValues();
		contentValues.put(DataManager.DownloadManager.FILE_START_POS,
				startPos);
		contentValues
				.put(DataManager.DownloadManager.FILE_STATUS, fileStatus);
		contentValues.put(DataManager.DownloadManager.FILE_SECOND_STATUS,
				fileSecondStatus);
		return mOBDataManager.update(DataManager.DownloadManager.TABLE_NAME,
				contentValues, whereArgs, null);
	}

	/**
	 * 更新符合资源文件地址和用户这两个条件的资源文件的第一状态、更新时间、第2状态
	 * 
	 * @param fileStatus
	 * @param startPos
	 * @param updateTime
	 * @param downloadUrl
	 * @param userId
	 * @return int
	 */
	public synchronized int updateFileWait(int fileStatus,
			int fileSecondStatus, long updateTime, String downloadUrl,
			String userId, String studyCourseId, String fileId) {
		String whereArgs = getArrayString(
				getQueryString(DataManager.DownloadManager.FILE_URL,
						downloadUrl),
				currentUserIdWhereArgs(userId),
				getQueryString(DataManager.DownloadManager.STUDENT_CODE,
						studyCourseId),
				getQueryString(DataManager.DownloadManager.FILE_ID, fileId));
		ContentValues contentValues = new ContentValues();
		contentValues
				.put(DataManager.DownloadManager.FILE_STATUS, fileStatus);
		contentValues
				.put(DataManager.DownloadManager.UPLOAD_DATE, updateTime);
		contentValues.put(DataManager.DownloadManager.FILE_SECOND_STATUS,
				fileSecondStatus);
		return mOBDataManager.update(DataManager.DownloadManager.TABLE_NAME,
				contentValues, whereArgs, null);
	}

	/**
	 * 更新符合条件的文件状态
	 * 
	 * @param firstStatusNew
	 * @param firstStatusOld
	 * @param secondStatusOld
	 * @param userId
	 * @return int
	 */
	public synchronized int updateFileNewStatus(int firstStatusNew,
			int firstStatusOld, int secondStatusOld, String userId) {
		String whereArgs = getArrayString(
				getQueryInt(DataManager.DownloadManager.FILE_STATUS,
						firstStatusOld),
				getQueryInt(DataManager.DownloadManager.FILE_SECOND_STATUS,
						secondStatusOld), currentUserIdWhereArgs(userId));
		ContentValues contentValues = new ContentValues();
		contentValues.put(DataManager.DownloadManager.FILE_STATUS,
				firstStatusNew);
		return mOBDataManager.update(DataManager.DownloadManager.TABLE_NAME,
				contentValues, whereArgs, null);
	}

	/**
	 * 更新符合资源文件地址和用户这两个条件的资源文件的第一状态、更新时间、开始进度
	 * 
	 * @param fileStatus
	 * @param startPos
	 * @param updateTime
	 * @param downloadUrl
	 * @param userId
	 * @return int
	 */
	public synchronized int updateFileWaitThread(int fileStatus, int startPos,
			long updateTime, String downloadUrl, String userId,
			String studyCourseId, String fileId) {
		String whereArgs = getArrayString(
				getQueryString(DataManager.DownloadManager.FILE_URL,
						downloadUrl),
				currentUserIdWhereArgs(userId),
				getQueryString(DataManager.DownloadManager.STUDENT_CODE,
						studyCourseId),
				getQueryString(DataManager.DownloadManager.FILE_ID, fileId));
		ContentValues contentValues = new ContentValues();
		contentValues
				.put(DataManager.DownloadManager.FILE_STATUS, fileStatus);
		contentValues
				.put(DataManager.DownloadManager.UPLOAD_DATE, updateTime);
		contentValues.put(DataManager.DownloadManager.FILE_START_POS,
				startPos);
		return mOBDataManager.update(DataManager.DownloadManager.TABLE_NAME,
				contentValues, whereArgs, null);
	}

	/**
	 * 更新符合资源文件地址和用户这两个条件的资源文件的第一状态和更新时间
	 * 
	 * @param fileStatus
	 * @param updateTime
	 * @param downloadUrl
	 * @param userId
	 * @return int
	 */
	public synchronized int updateFileStatus(int fileStatus, long updateTime,
			String downloadUrl, String userId, String studyCourseId,
			String fileId) {
		String whereArgs = getArrayString(
				getQueryString(DataManager.DownloadManager.FILE_URL,
						downloadUrl),
				currentUserIdWhereArgs(userId),
				getQueryString(DataManager.DownloadManager.STUDENT_CODE,
						studyCourseId),
				getQueryString(DataManager.DownloadManager.FILE_ID, fileId));
		ContentValues contentValues = new ContentValues();
		contentValues
				.put(DataManager.DownloadManager.FILE_STATUS, fileStatus);
		contentValues
				.put(DataManager.DownloadManager.UPLOAD_DATE, updateTime);
		return mOBDataManager.update(DataManager.DownloadManager.TABLE_NAME,
				contentValues, whereArgs, null);
	}

	/**
	 * 更新符合资源文件地址和用户这两个条件的资源文件的第一状态和更新时间
	 * 
	 * @param fileStatus
	 * @param updateTime
	 * @param downloadUrl
	 * @param userId
	 * @return int
	 */
	public synchronized int updateFileDocStatus(int fileStatus,
			long updateTime, String downloadUrl, String userId,
			String studyCourseId, String fileId, String fileName) {
		String whereArgs = getArrayString(
				(getQueryString(DataManager.DownloadManager.FILE_URL,
						downloadUrl)),
				currentUserIdWhereArgs(userId),
				(getQueryString(DataManager.DownloadManager.STUDENT_CODE,
						studyCourseId)),
				(getQueryString(DataManager.DownloadManager.FILE_ID, fileId)),
				(getQueryString(DataManager.DownloadManager.FILE_NAME,
						fileName)));
		ContentValues contentValues = new ContentValues();
		contentValues
				.put(DataManager.DownloadManager.FILE_STATUS, fileStatus);
		contentValues
				.put(DataManager.DownloadManager.UPLOAD_DATE, updateTime);
		return mOBDataManager.update(DataManager.DownloadManager.TABLE_NAME,
				contentValues, whereArgs, null);
	}

	/**
	 * 更新符合资源文件文档文件地址和用户这两个条件的资源文件的第一状态和更新时间
	 * 
	 * @param fileStatus
	 * @param updateTime
	 * @param downloadUrl
	 * @param userId
	 * @param fileId
	 * @param fileName
	 * @return int
	 */
	public synchronized int updateDocFileStatus(int fileStatus,
			long updateTime, String downloadUrl, String userId,
			String studyCourseId, String fileId, String fileName) {
		String whereArgs = getArrayString(
				getQueryString(DataManager.DownloadManager.FILE_URL,
						downloadUrl),
				currentUserIdWhereArgs(userId),
				getQueryString(DataManager.DownloadManager.STUDENT_CODE,
						studyCourseId),
				getQueryString(DataManager.DownloadManager.FILE_ID, fileId),
				getQueryString(DataManager.DownloadManager.FILE_NAME,
						fileName));
		ContentValues contentValues = new ContentValues();
		contentValues
				.put(DataManager.DownloadManager.FILE_STATUS, fileStatus);
		contentValues
				.put(DataManager.DownloadManager.UPLOAD_DATE, updateTime);
		return mOBDataManager.update(DataManager.DownloadManager.TABLE_NAME,
				contentValues, whereArgs, null);
	}

	/**
	 * 添加新的资源文件到数据库
	 * 
	 * @param OBDownloadFile
	 * @return long
	 */
//	public synchronized long addOBDownloadFile(OBDownloadFile fileItem) {
//		ContentValues values = pushOBDownloadFileData(fileItem);
//		long id = mOBDataManager.insert(
//				OBDataManager.DownloadManager.TABLE_NAME, null, values);
//		return id;
//	}
//
//	/**
//	 * @param OBDownloadFile
//	 * @return ContentValues
//	 */
//	public ContentValues pushOBDownloadFileData(OBDownloadFile fileItem) {
//		ContentValues values = new ContentValues();
//		values.put(OBDataManager.DownloadManager.USER_ID, fileItem.userId);
//		values.put(OBDataManager.DownloadManager.COURSER_ID, fileItem.courseId);
//		values.put(OBDataManager.DownloadManager.COURSER_NAME,
//				fileItem.courseName);
//		values.put(OBDataManager.DownloadManager.STUDENT_CODE,
//				fileItem.studentCode);
//		values.put(OBDataManager.DownloadManager.FILE_ID, fileItem.fileId);
//		values.put(OBDataManager.DownloadManager.FILE_URL, fileItem.fileUrl);
//		values.put(OBDataManager.DownloadManager.FILE_SIZE, fileItem.fileSize);
//		values.put(OBDataManager.DownloadManager.FILE_NAME, fileItem.fileName);
//		values.put(OBDataManager.DownloadManager.FILE_TYPE, fileItem.fileType);
//		values.put(OBDataManager.DownloadManager.FILE_START_POS,
//				fileItem.fileStartPos);
//		values.put(OBDataManager.DownloadManager.FILE_ENd_POS,
//				fileItem.fileEndPos);
//		values.put(OBDataManager.DownloadManager.FILE_STATUS,
//				fileItem.fileStatus);
//		values.put(OBDataManager.DownloadManager.FILE_SECOND_STATUS,
//				fileItem.fileSecondStatus);
//		values.put(OBDataManager.DownloadManager.UPLOAD_DATE,
//				fileItem.uploadData);
//		values.put(OBDataManager.DownloadManager.PROGRESS_PAR,
//				fileItem.PrgogressRate);
//		values.put(OBDataManager.DownloadManager.REMAIN_SIZE,
//				fileItem.RemainSize);
//		values.put(OBDataManager.DownloadManager.REMAIN_TIME,
//				fileItem.RemainTime);
//		values.put(OBDataManager.DownloadManager.REMAIN_RATE,
//				fileItem.RemainRate);
//		values.put(OBDataManager.DownloadManager.DATA_SIZE, fileItem.DataSize);
//		return values;
//	}

	/**
	 * 删除资源文件数据库表里面所有的条目
	 * 
	 * @return
	 */
	public synchronized void deleteAllOBDownloadFile() {
		mOBDataManager.delete(DataManager.DownloadManager.TABLE_NAME, "",
				null);
	}

	/**
	 * 根据用户Id和资源文件地址删除该资源
	 * 
	 * @param url
	 * @param userId
	 * @return int
	 */
	public synchronized int deleteDownloadByUrl(String url, String userId,
			String studyCourseId, String fileId) {
		String where = getArrayString(
				getQueryString(DataManager.DownloadManager.FILE_URL, url),
				currentUserIdWhereArgs(userId),
				getQueryString(DataManager.DownloadManager.STUDENT_CODE,
						studyCourseId),
				getQueryString(DataManager.DownloadManager.FILE_ID, fileId));
		return mOBDataManager.delete(DataManager.DownloadManager.TABLE_NAME,
				where, null);
	}

	public String currentUserIdWhereArgs(String user_Id) {
		return new StringBuffer("").append(
				getQueryString(DataManager.DownloadManager.USER_ID, user_Id))
				.toString();
	}

	// 数据库来纪录积分状态，从而获取争取的计算积分的体系
	/**
	 * 插入新的纪录条目
	 * 
	 * @param CountScore
	 * @return long
	 */
//	public synchronized long insertCountScore(CountScore item) {
//		ContentValues valueItem = new ContentValues();
//		valueItem.put(OBDataManager.SpicificCourse.USER_ID, item.UserId);
//		valueItem.put(OBDataManager.SpicificCourse.STUDENT_CODE,
//				item.StudentCode);
//		valueItem.put(OBDataManager.SpicificCourse.COURESE_ID, item.CourseCode);
//		valueItem.put(OBDataManager.SpicificCourse.INCOME_COUNT,
//				item.TotalCount);
//		valueItem.put(OBDataManager.SpicificCourse.TAG_TYPE, item.CountType);
//		valueItem.put(OBDataManager.SpicificCourse.START_TIME, item.StartTime);
//		valueItem.put(OBDataManager.SpicificCourse.LOCK_TIME,
//				item.lockScreenTime);
//		valueItem.put(OBDataManager.SpicificCourse.NEAR_CLICK, item.nearClick);
//		valueItem.put(OBDataManager.SpicificCourse.VIDEO_ID, item.VideoId);
//		return mOBDataManager.insert(OBDataManager.SpicificCourse.TABLE_NAME,
//				null, valueItem);
//	}
//
//	/**
//	 * 获取符合条件的条目
//	 * 
//	 * @param String
//	 * @param String
//	 * @param String
//	 * @param String
//	 * @return CountScore
//	 */
//
//	public CountScore queryDesirableItem(String userId, String studentCode,
//			String courseCode, int tagType, String videoId) {
//		CountScore desireItem = null;
//		String whereArg = "";
//		if (tagType == CountCourseScore.FIRST_TYPE) {
//			whereArg = getArrayString(
//					getQueryString(OBDataManager.SpicificCourse.USER_ID, userId),
//					getQueryString(OBDataManager.SpicificCourse.STUDENT_CODE,
//							studentCode),
//					getQueryString(OBDataManager.SpicificCourse.COURESE_ID,
//							courseCode),
//					getQueryInt(OBDataManager.SpicificCourse.TAG_TYPE, tagType));
//		} else if (tagType == CountCourseScore.SECOND_TYPE) {
//			whereArg = getArrayString(
//					getQueryString(OBDataManager.SpicificCourse.USER_ID, userId),
//					getQueryString(OBDataManager.SpicificCourse.STUDENT_CODE,
//							studentCode),
//					getQueryString(OBDataManager.SpicificCourse.COURESE_ID,
//							courseCode),
//					getQueryInt(OBDataManager.SpicificCourse.TAG_TYPE, tagType),
//					getQueryString(OBDataManager.SpicificCourse.VIDEO_ID,
//							videoId));
//		}
//
//		Cursor cursor = mOBDataManager.qurey(
//				OBDataManager.SpicificCourse.TABLE_NAME, null, whereArg, null,
//				null, null, null);
//		if (cursor != null) {
//			if (cursor.moveToFirst()) {
//				desireItem = new CountScore();
//				desireItem.UserId = cursor.getString(cursor
//						.getColumnIndex(OBDataManager.SpicificCourse.USER_ID));
//				desireItem.StudentCode = cursor
//						.getString(cursor
//								.getColumnIndex(OBDataManager.SpicificCourse.STUDENT_CODE));
//				desireItem.CourseCode = cursor
//						.getString(cursor
//								.getColumnIndex(OBDataManager.SpicificCourse.COURESE_ID));
//				desireItem.TotalCount = cursor
//						.getInt(cursor
//								.getColumnIndex(OBDataManager.SpicificCourse.INCOME_COUNT));
//				desireItem.CountType = cursor.getInt(cursor
//						.getColumnIndex(OBDataManager.SpicificCourse.TAG_TYPE));
//				desireItem.StartTime = cursor
//						.getLong(cursor
//								.getColumnIndex(OBDataManager.SpicificCourse.START_TIME));
//				desireItem.lockScreenTime = cursor
//						.getInt(cursor
//								.getColumnIndex(OBDataManager.SpicificCourse.LOCK_TIME));
//				desireItem.nearClick = cursor
//						.getLong(cursor
//								.getColumnIndex(OBDataManager.SpicificCourse.NEAR_CLICK));
//				desireItem.VideoId = cursor.getString(cursor
//						.getColumnIndex(OBDataManager.SpicificCourse.VIDEO_ID));
//			}
//			cursor.close();
//		}
//		return desireItem;
//	}
//
//	/**
//	 * 更新积分次数或下载开始时间，或者二者都更新
//	 * 
//	 * @param int
//	 * @param long
//	 * @param long
//	 * @param String
//	 * @param String
//	 * @param String
//	 * @param String
//	 * @return int
//	 */
//	public synchronized int updateCurrentStatus(int incomeCount,
//			long startTime, long nearClick, String userId, String studentCode,
//			String courseCode, int tagType, String videoId) {
//		String whereArg = "";
//		if (tagType == CountCourseScore.FIRST_TYPE) {
//			whereArg = getArrayString(
//					getQueryString(OBDataManager.SpicificCourse.USER_ID, userId),
//					getQueryString(OBDataManager.SpicificCourse.STUDENT_CODE,
//							studentCode),
//					getQueryString(OBDataManager.SpicificCourse.COURESE_ID,
//							courseCode),
//					getQueryInt(OBDataManager.SpicificCourse.TAG_TYPE, tagType));
//		} else if (tagType == CountCourseScore.SECOND_TYPE) {
//			whereArg = getArrayString(
//					getQueryString(OBDataManager.SpicificCourse.USER_ID, userId),
//					getQueryString(OBDataManager.SpicificCourse.STUDENT_CODE,
//							studentCode),
//					getQueryString(OBDataManager.SpicificCourse.COURESE_ID,
//							courseCode),
//					getQueryInt(OBDataManager.SpicificCourse.TAG_TYPE, tagType),
//					getQueryString(OBDataManager.SpicificCourse.VIDEO_ID,
//							videoId));
//		}
//
//		ContentValues valueItem = new ContentValues();
//		if (incomeCount >= 0) {
//			if (tagType == CountCourseScore.FIRST_TYPE) {
//				valueItem.put(OBDataManager.SpicificCourse.LOCK_TIME,
//						incomeCount);
//			} else if (tagType == CountCourseScore.SECOND_TYPE) {
//				valueItem.put(OBDataManager.SpicificCourse.INCOME_COUNT,
//						incomeCount);
//			}
//
//		}
//		if (startTime > 0) {
//			valueItem.put(OBDataManager.SpicificCourse.START_TIME, startTime);
//		}
//		if (nearClick > 0) {
//			valueItem.put(OBDataManager.SpicificCourse.NEAR_CLICK, nearClick);
//		}
//		return mOBDataManager.update(OBDataManager.SpicificCourse.TABLE_NAME,
//				valueItem, whereArg, null);
//	}

	/**
	 * 删除当前表
	 * 
	 */
	public synchronized void deleteCounScoreTable() {
		mOBDataManager
				.delete(DataManager.SpicificCourse.TABLE_NAME, "", null);
	}


	/**
	 * 添加聊天消息到数据库
	 * 
	 * @param OBDownloadFile
	 * @return long
	 */
	public synchronized long addFriendMessage(FriendMessage message) {
		ContentValues values = pushFriendMessageData(message);
		long id = mOBDataManager.insert(
				DataManager.ChatMessageRecord.TABLE_NAME, null, values);
		return id;
	}

	/**
	 * 获取消息记录
	 * 
	 * @param secondStatus
	 * @param sessionID
	 * @return list<OBLFriendMessage>
	 */

	public List<FriendMessage> queryMsgHistoryBySessionid(String sessionID) {
		List<FriendMessage> listRecord = new ArrayList<FriendMessage>();
		Cursor statusRecord = queryMessageHistory(sessionID);
		if (statusRecord != null) {
			if (statusRecord.moveToFirst()) {
				do {
					listRecord.add(putMessageData(statusRecord));
				} while (statusRecord.moveToNext());
			}
			statusRecord.close();
		}

		return listRecord;
	}

	/**
	 * 获取消息记录
	 * 
	 * @param sessionID
	 *            具体的会话的id
	 * @param lastTime
	 *            上次加载的第一条数据的储存时间
	 * @param limit
	 *            一次最多获取的数据条数
	 * @return list<OBLFriendMessage>
	 */

	public List<FriendMessage> queryMsgHistoryBySessionidlimit(
			String sessionID, int firstId, int limit) {
		List<FriendMessage> listRecord = new ArrayList<FriendMessage>();
		Cursor statusRecord = queryMeCursorHistory(sessionID, firstId, limit);
		if (statusRecord != null) {
			if (statusRecord.moveToFirst()) {
				do {
					listRecord.add(putMessageData(statusRecord));
				} while (statusRecord.moveToNext());
			}
			statusRecord.close();
		}

		return listRecord;
	}

	/**
	 * 查询是否存在有大图在数据库
	 * 
	 * @param sessionID
	 * @param bigUrl
	 * @return
	 */

	public int queryMsgHistoryBySessionidNum(String sessionID, String bigUrl) {

		int queryNum = 0;
		String whereArgs = DataManager.ChatMessageRecord.SESSION_ID + "='"
				+ sessionID + "'" + " and "
				+ DataManager.ChatMessageRecord.IMAGEURL + "='" + bigUrl
				+ "'";
		String orderArgs = DataManager.ChatMessageRecord.LOCALTIME + " Desc";
		Cursor statusRecord = mOBDataManager.qureyAndSort(
				DataManager.ChatMessageRecord.TABLE_NAME, null, whereArgs,
				null, null, null, orderArgs, null);
		if (statusRecord != null) {
			queryNum = statusRecord.getCount();
			statusRecord.close();
		}

		return queryNum;
	}

	/**
	 * 查询当前符合ID的数据
	 * 
	 * @param ID
	 * @return
	 */
	public FriendMessage queryByID(String ID) {
		FriendMessage message = null;
		String whereArgs = DataManager.ChatMessageRecord._ID + "='" + ID
				+ "'";
		Cursor queryResult = mOBDataManager.qurey(
				DataManager.ChatMessageRecord.TABLE_NAME, null, whereArgs,
				null, null, null, null);
		if (queryResult != null) {
			if (queryResult.moveToFirst()) {
				message = putMessageData(queryResult);
			}
			queryResult.close();
		}
		return message;
	}

	public long replaceOrInsert(FriendMessage msg) {

		// 先查询对方的验证信息数据库中有无记录
		String whereArgs = DataManager.ChatMessageRecord.SESSION_ID + "='"
				+ msg.getmMsgSessionID() + "'" + " and "
				+ DataManager.ChatMessageRecord.CODE + "='"
				+ msg.getmMsgCode() + "'";
		Cursor qurey = mOBDataManager.qurey(
				DataManager.ChatMessageRecord.TABLE_NAME, null, whereArgs,
				null, null, null, null);
		if (qurey.getCount() == 0) {
			addFriendMessage(msg);
		} else {
			ContentValues values = pushFriendMessageData(msg);
			mOBDataManager.update(DataManager.ChatMessageRecord.TABLE_NAME,
					values, whereArgs, null);
		}

		return 0;
	}

	/**
	 * 查询存在大图或小图的消息
	 * 
	 * @param sessionID
	 * @param bigUrl
	 * @param smallUrl
	 * @return
	 */

	public FriendMessage queryspeMessage(String sessionID,
			String fromStatus, long time) {
		FriendMessage message = null;
		String whereArgs = DataManager.ChatMessageRecord.SESSION_ID + "='"
				+ sessionID + "'" + " and "
				+ DataManager.ChatMessageRecord.WHERECOME + "='" + fromStatus
				+ "' and " + DataManager.ChatMessageRecord.RECEIVETIME + "="
				+ time;
		Cursor statusRecord = mOBDataManager.qurey(
				DataManager.ChatMessageRecord.TABLE_NAME, null, whereArgs,
				null, null, null, null);
		if (statusRecord != null) {
			if (statusRecord.moveToFirst()) {
				message = putMessageData(statusRecord);
			}
			statusRecord.close();
		}

		return message;
	}

	/**
	 * 增加符合条件的大图到数据库
	 * 
	 * @param sessionID
	 * @param bigUrl
	 * @param smallUrl
	 * @return
	 */

	public int insertBigBySessionidNum(String sessionID, String smallUrl,
			String bigUrl) {
		String whereArgs = DataManager.ChatMessageRecord.SESSION_ID + "='"
				+ sessionID + "'" + " and "
				+ DataManager.ChatMessageRecord.IMAGEPATH + "='" + smallUrl
				+ "'";
		ContentValues contentValues = new ContentValues();
		contentValues.put(DataManager.ChatMessageRecord.IMAGEURL, bigUrl);
		return mOBDataManager.update(
				DataManager.ChatMessageRecord.TABLE_NAME, contentValues,
				whereArgs, null);
	}

	/**
	 * 增加符合条件的大图到数据库
	 * 
	 * @param sessionID
	 * @param bigUrl
	 * @param smallUrl
	 * @return
	 */

	public int insertMsgHistoryBySessionidNum(String ID, String smallUrl) {
		String whereArgs = DataManager.ChatMessageRecord._ID + "='" + ID
				+ "'";
		ContentValues contentValues = new ContentValues();
		contentValues.put(DataManager.ChatMessageRecord.IMAGEPATH, smallUrl);
		return mOBDataManager.update(
				DataManager.ChatMessageRecord.TABLE_NAME, contentValues,
				whereArgs, null);
	}

	/**
	 * 更新具体每条信息的发送状态
	 * 
	 * @param sessionID
	 * @param receiveTime
	 * @param sendStatus
	 * @return
	 */
	public int updateSpecityItem(String ID, int sendStatus) {
		String whereArgs = DataManager.ChatMessageRecord._ID + "='" + ID
				+ "'";
		ContentValues contentValues = new ContentValues();
		contentValues.put(DataManager.ChatMessageRecord.SEND_STATUS,
				sendStatus);
		return mOBDataManager.update(
				DataManager.ChatMessageRecord.TABLE_NAME, contentValues,
				whereArgs, null);
	}

	/**
	 * 通过好友验证的话进行好友验证信息的归属人的改变
	 * 
	 * @param sessionID
	 * @param receiveTime
	 * @param sendStatus
	 * @return
	 */
	public int updateSpecityItemCode(String sessionID, String messageCode,
			String messageFrom, String newMessageCode, String newMessageFrom,
			String newMessage) {
		String whereArgs = DataManager.ChatMessageRecord.SESSION_ID + "='"
				+ sessionID + "'" + " and "
				+ DataManager.ChatMessageRecord.CODE + "='" + messageCode
				+ "'" + " and " + DataManager.ChatMessageRecord.WHERECOME
				+ "='" + messageFrom + "'";
		ContentValues contentValues = new ContentValues();
		contentValues.put(DataManager.ChatMessageRecord.CODE, newMessageCode);
		contentValues.put(DataManager.ChatMessageRecord.WHERECOME,
				newMessageFrom);
		contentValues.put(DataManager.ChatMessageRecord.CONTENT, newMessage);
		return mOBDataManager.update(
				DataManager.ChatMessageRecord.TABLE_NAME, contentValues,
				whereArgs, null);
	}

	/**
	 * 查询最近消息
	 * 
	 * @param secondStatus
	 * @param READ_STATUS
	 * @return list<OBLFriendMessage>
	 */

	public ArrayList<FriendMessage> queryRecentMsg() {
		ArrayList<FriendMessage> listRecord = new ArrayList<FriendMessage>();
		Cursor statusRecord = queryMessage();
		if (statusRecord != null) {
			if (statusRecord.moveToFirst()) {
				do {
					listRecord.add(putMessageData(statusRecord));
				} while (statusRecord.moveToNext());
			}
			statusRecord.close();
		}

		return listRecord;
	}

	/**
	 * 
	 * 查询最近消息，分组并且排序
	 * 
	 * @return list<OBLFriendMessage>
	 */

	public ArrayList<FriendMessage> queryRecentMsgSort(String toUser) {
		ArrayList<FriendMessage> listRecord = new ArrayList<FriendMessage>();
		Cursor statusRecord = queryMsgSort(toUser);
		if (statusRecord != null) {
			if (statusRecord.moveToFirst()) {
				do {
					listRecord.add(putMessageData(statusRecord));
				} while (statusRecord.moveToNext());
			}
			statusRecord.close();
		}

		return listRecord;
	}

	/**
	 * 
	 * 模糊查询最近消息，分组并且排序
	 * 
	 * @return list<OBLFriendMessage>
	 */

	public ArrayList<FriendMessage> queryRecentMsgSort(String toUser,
			String likeName) {
		ArrayList<FriendMessage> listRecord = new ArrayList<FriendMessage>();
		Cursor statusRecord = queryMsgSort(toUser, likeName);
		if (statusRecord != null) {
			if (statusRecord.moveToFirst()) {
				do {
					listRecord.add(putMessageData(statusRecord));
				} while (statusRecord.moveToNext());
			}
			statusRecord.close();
		}
		
		return listRecord;
	}

	/**
	 * 根据sessionID查询未读消息数量
	 * 
	 * @param secondStatus
	 * @param status
	 * @return list<OBLFriendMessage>
	 */

	public synchronized int queryUnReadMsgBySessionId(String sessionID) {

		Cursor cousor = mOBDataManager.queryUnReadMsgBySessionId(sessionID);
		int unReadNum = cousor.getCount();
		cousor.close();
		
		return unReadNum;
	}

	/**
	 * 查询未读消息数量
	 * 
	 * @param secondStatus
	 * @param status
	 * @return list<OBLFriendMessage>
	 */

	public synchronized int queryUnReadMsgCount(String status, String toUser) {
		if (toUser != null && status !=null && !toUser.equals("")&&!status.equals("")){
			Cursor cursor = mOBDataManager.qureyUnReadMessageCount(status, toUser);
			int count = cursor.getCount();
			cursor.close();
			return count;
		}
			
		else
			return 0;
	}

	/**
	 * 根据sessionID删除消息记录
	 * 
	 * @param sessionID
	 * @return int
	 */
	public synchronized int deleteMessageBySessionid(String sessionID) {
		String where = DataManager.ChatMessageRecord.SESSION_ID + "='"
				+ sessionID + "'";
		return mOBDataManager.delete(
				DataManager.ChatMessageRecord.TABLE_NAME, where, null);
	}

	/**
	 * 根据sessionID删除消息记录
	 * 
	 * @param sessionID
	 * @return int
	 */
	public synchronized int deleteMessageBySessionid(String[] args) {
		// String where = OBDataManager.ChatMessageRecord.SESSION_ID + "='"+
		// sessionID+"'";
		return mOBDataManager.deleteMsgBatch(args);
	}

	/**
	 * 根据sessionID更新消息记录为已读
	 * 
	 * @param sessionID
	 * @return int
	 */
	public synchronized void updateMessageBySessionid(String sessionID) {
		String where = DataManager.ChatMessageRecord.SESSION_ID + "='"
				+ sessionID + "'";
		ContentValues contentValues = new ContentValues();
		contentValues.put(DataManager.ChatMessageRecord.READ_STATUS, "2");

		// return mOBDataManager.update(
		// OBDataManager.ChatMessageRecord.TABLE_NAME, contentValues,
		// where, null);
		mOBDataManager.updateReadStatus(sessionID);
	}

	private Cursor queryMsgSort(String toUser, String likeName) {

		return mOBDataManager.qureyRecentMsgLikeSort(toUser, likeName);
	}

	private Cursor queryMsgSort(String toUser) {

		return mOBDataManager.qureyRecentMsgSort(toUser);
	}

	private Cursor queryMessage() {

		return mOBDataManager.qurey(DataManager.ChatMessageRecord.TABLE_NAME,
				null, null, null, null, null, null);
	}

	private FriendMessage putMessageData(Cursor coursor) {
		FriendMessage msg = new FriendMessage();
		msg.setId(coursor.getString(coursor
				.getColumnIndex(DataManager.ChatMessageRecord._ID)));
		msg.setmMsgAhthorID(coursor.getString(coursor
				.getColumnIndex(DataManager.ChatMessageRecord.FROM_USER)));
		msg.setmMsgAhthorName(coursor.getString(coursor
				.getColumnIndex(DataManager.ChatMessageRecord.FROM_USER_NAME)));
		msg.setmMsgAhthorIcon(coursor.getString(coursor
				.getColumnIndex(DataManager.ChatMessageRecord.FROM_USER_FACE)));
		msg.setmMsgAcceptID(coursor.getString(coursor
				.getColumnIndex(DataManager.ChatMessageRecord.TO_USER)));
		msg.setmMsgContent(coursor.getString(coursor
				.getColumnIndex(DataManager.ChatMessageRecord.CONTENT)));
		msg.setReceiveTime(coursor.getString(coursor
				.getColumnIndex(DataManager.ChatMessageRecord.RECEIVETIME)));
		msg.setReceiveLocalTime(coursor.getString(coursor
				.getColumnIndex(DataManager.ChatMessageRecord.LOCALTIME)));
		msg.setmMsgCode(coursor.getString(coursor
				.getColumnIndex(DataManager.ChatMessageRecord.CODE)));
		msg.setmMsgType(coursor.getString(coursor
				.getColumnIndex(DataManager.ChatMessageRecord.TYPE)));
		msg.setmMsgStatus(coursor.getString(coursor
				.getColumnIndex(DataManager.ChatMessageRecord.READ_STATUS)));
		msg.setmMsgSendStatus(coursor.getString(coursor
				.getColumnIndex(DataManager.ChatMessageRecord.SEND_STATUS)));
		msg.setmMsgImgPath(coursor.getString(coursor
				.getColumnIndex(DataManager.ChatMessageRecord.IMAGEPATH)));
		msg.setmMsgImgUrl(coursor.getString(coursor
				.getColumnIndex(DataManager.ChatMessageRecord.IMAGEURL)));
		msg.setmMsgAudioUrl(coursor.getString(coursor
				.getColumnIndex(DataManager.ChatMessageRecord.AUDIOURL)));
		msg.setmMsgSessionID(coursor.getString(coursor
				.getColumnIndex(DataManager.ChatMessageRecord.SESSION_ID)));
		msg.setFromStatus(coursor.getString(coursor
				.getColumnIndex(DataManager.ChatMessageRecord.WHERECOME)));

		return msg;
	}

	private Cursor queryMessageHistory(String sessionID) {
		String whereArgs = DataManager.ChatMessageRecord.SESSION_ID + "="
				+ sessionID;
		return mOBDataManager.qurey(DataManager.ChatMessageRecord.TABLE_NAME,
				null, whereArgs, null, null, null, null);
	}

	/**
	 * 按过滤条件查询数据
	 * 
	 * @param sessionID
	 *            用户的会话id
	 * @param lastTime
	 *            上次请求的最原始的会话的时间
	 * @param limit
	 *            当前获取数据的条目数
	 * @return
	 */
	private Cursor queryMeCursorHistory(String sessionID, int firstId, int limit) {
		String whereArgs = DataManager.ChatMessageRecord.SESSION_ID + "='"
				+ sessionID + "'" + " and "
				+ DataManager.ChatMessageRecord._ID + "<" + firstId;
		String orderArgs = DataManager.ChatMessageRecord._ID + " Desc";
		return mOBDataManager.qureyAndSort(
				DataManager.ChatMessageRecord.TABLE_NAME, null, whereArgs,
				null, null, null, orderArgs, String.valueOf(limit));
	}

	private ContentValues pushFriendMessageData(FriendMessage message) {
		ContentValues values = new ContentValues();
		values.put(DataManager.ChatMessageRecord.SESSION_ID,
				message.getmMsgSessionID());
		values.put(DataManager.ChatMessageRecord.TO_USER,
				message.getmMsgAcceptID());
		values.put(DataManager.ChatMessageRecord.FROM_USER,
				message.getmMsgAhthorID());
		values.put(DataManager.ChatMessageRecord.FROM_USER_NAME,
				message.getmMsgAhthorName());
		values.put(DataManager.ChatMessageRecord.FROM_USER_FACE,
				message.getmMsgAhthorIcon());
		values.put(DataManager.ChatMessageRecord.READ_STATUS,
				message.getmMsgStatus());
		values.put(DataManager.ChatMessageRecord.SEND_STATUS,
				message.getmMsgSendStatus());
		values.put(DataManager.ChatMessageRecord.CODE, message.getmMsgCode());
		values.put(DataManager.ChatMessageRecord.TYPE, message.getmMsgType());
		values.put(DataManager.ChatMessageRecord.AUDIOURL,
				message.getmMsgAudioUrl());
		values.put(DataManager.ChatMessageRecord.IMAGEURL,
				message.getmMsgImgUrl());
		values.put(DataManager.ChatMessageRecord.IMAGEPATH,
				message.getmMsgImgPath());
		values.put(DataManager.ChatMessageRecord.RECEIVETIME,
				message.getReceiveTime());
		values.put(DataManager.ChatMessageRecord.LOCALTIME,
				message.getReceiveLocalTime());
		values.put(DataManager.ChatMessageRecord.WHERECOME,
				message.getFromStatus());
		values.put(DataManager.ChatMessageRecord.CONTENT,
				message.getmMsgContent());
		return values;
	}

	/**
	 * 添加用户图像信息
	 * 
	 * @param friendInfo
	 */
	public long insertFriendInfo(FriendInfo friendInfo) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DataManager.FriendInfo.FRIEND_USER_ID,
				friendInfo.getmFriendId());
		contentValues.put(DataManager.FriendInfo.FRIEND_USER_NAME,
				friendInfo.getmFriendUserName());
		contentValues.put(DataManager.FriendInfo.FRIEND_SESSION_ID,
				friendInfo.getmFriendSessionID());
		contentValues.put(DataManager.FriendInfo.FRIEND_USER_LOCAL_URL,
				friendInfo.getmFriendUserLocalIconUrl());
		contentValues.put(DataManager.FriendInfo.FRIEND_USER_URL,
				friendInfo.getmFriendUserIconUrl());
		return mOBDataManager.insert(DataManager.FriendInfo.TABLE_NAME, null,
				contentValues);

	}

	/**
	 * 获取用户图像信息
	 * 
	 * @param userId
	 * @return
	 */
	public FriendInfo QueryFriendInfo(String userId) {
		FriendInfo info = null;
		String whereArgs = DataManager.FriendInfo.FRIEND_USER_ID + "="
				+ userId;
		Cursor cursor = mOBDataManager.qurey(
				DataManager.FriendInfo.TABLE_NAME, null, whereArgs, null,
				null, null, null);
		if (cursor.moveToFirst()) {
			info = new FriendInfo();
			info.setId(cursor.getColumnName(cursor
					.getColumnIndex(DataManager.FriendInfo._ID)));
			info.setmFriendId(cursor.getColumnName(cursor
					.getColumnIndex(DataManager.FriendInfo.FRIEND_USER_ID)));
			info.setmFriendSessionID(cursor.getColumnName(cursor
					.getColumnIndex(DataManager.FriendInfo.FRIEND_SESSION_ID)));
			info.setmFriendUserIconUrl(cursor.getColumnName(cursor
					.getColumnIndex(DataManager.FriendInfo.FRIEND_USER_URL)));
			info.setmFriendUserLocalIconUrl(cursor.getColumnName(cursor
					.getColumnIndex(DataManager.FriendInfo.FRIEND_USER_LOCAL_URL)));
			info.setmFriendUserName(cursor.getColumnName(cursor
					.getColumnIndex(DataManager.FriendInfo.FRIEND_USER_NAME)));
			cursor.close();
		}

		return info;
	}
}
