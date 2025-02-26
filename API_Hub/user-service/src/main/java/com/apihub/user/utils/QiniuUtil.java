package com.apihub.user.utils;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.model.BatchStatus;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class QiniuUtil {


    public static String getToken(String key) {
        Auth auth = Auth.create(QiniuConstant.ACCESS_KEY, QiniuConstant.SECRET_KEY);
        return auth.uploadToken(QiniuConstant.BUCKET, key);
    }

    public static void deleteFile(List<String> files) {
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region0());
        Auth auth = Auth.create(QiniuConstant.ACCESS_KEY, QiniuConstant.SECRET_KEY);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            //单次批量请求的文件数量不得超过1000
            String[] keyList = files.toArray(new String[0]);
            BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
            batchOperations.addDeleteOp(QiniuConstant.BUCKET, keyList);
            Response response = bucketManager.batch(batchOperations);
            BatchStatus[] batchStatusList = response.jsonToObject(BatchStatus[].class);
            for (int i = 0; i < keyList.length; i++) {
                BatchStatus status = batchStatusList[i];
                String key = keyList[i];
                if (status.code == 200) {
                    log.info(key + "：删除成功！");
                } else {
                    log.error(key + "：" + status.data.error);
                }
            }
        } catch (QiniuException ex) {
            log.error(ex.response.toString());
        }
    }

    public static Map<String, Map<String, String>> getFileInfo(List<String> files) {
        Map<String, Map<String, String>> result = new HashMap<>();

        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region0());
        Auth auth = Auth.create(QiniuConstant.ACCESS_KEY, QiniuConstant.SECRET_KEY);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            //单次批量请求的文件数量不得超过1000
            String[] keyList = files.toArray(new String[0]);
            BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
            batchOperations.addStatOps(QiniuConstant.BUCKET, keyList);
            Response response = bucketManager.batch(batchOperations);
            BatchStatus[] batchStatusList = response.jsonToObject(BatchStatus[].class);
            for (int i = 0; i < keyList.length; i++) {
                BatchStatus status = batchStatusList[i];
                String key = keyList[i];
                if (status.code == 200) {
                    //文件存在
                    Map<String, String> info = new HashMap<>();
                    info.put("size", String.valueOf(status.data.fsize));
                    info.put("mimeType", status.data.mimeType);
                    result.put(key, info);
                } else {
                    log.error(key + "：" + status.data.error);
                }
            }
        } catch (QiniuException ex) {
            log.error(ex.response.toString());
        }

        return result;
    }
}
