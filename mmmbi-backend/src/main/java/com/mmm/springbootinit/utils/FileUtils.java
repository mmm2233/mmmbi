package com.mmm.springbootinit.utils;

import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.utils
 * @Project：mmmbi-backend
 * @name：FileUtils
 * @Date：2024/3/9 18:53
 * @Filename：FileUtils
 */
public class FileUtils {

    // 分割文件
    public static List<String> readFile(MultipartFile file) {
        List<String> list = new ArrayList<>();
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (stringBuilder.length() + line.length() < 990){
                    stringBuilder.append(line);
                }else {
                    list.add(stringBuilder.toString());
                    stringBuilder.delete(0,stringBuilder.length());
                    continue;
                }
                stringBuilder.append(line);
            }
            if (stringBuilder.length() > 0) {
                list.add(stringBuilder.toString());
            }
            reader.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return list;
    }
}
