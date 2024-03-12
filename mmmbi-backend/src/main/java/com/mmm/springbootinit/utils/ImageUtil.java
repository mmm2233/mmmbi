package com.mmm.springbootinit.utils;

import java.io.*;
import java.util.Base64;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.utils
 * @Project：mmmbi-backend
 * @name：ImageUtil
 * @Date：2024/3/10 20:27
 * @Filename：ImageUtil
 */
public class ImageUtil {
    /**
     * 通过文件路径读取图片
     * @param filePath 文件路径
     * @return
     * @throws IOException
     */
    public static byte[] readByFilePath(String filePath) throws IOException {
        InputStream in = new FileInputStream(filePath);
        byte[] data = inputStream2ByteArray(in);
        in.close();
        return data;
    }

    public static String convertImageToBase64(String imagePath) {
        File imageFile = new File(imagePath);
        try (FileInputStream fileInputStream = new FileInputStream(imageFile)) {
            byte[] imageBytes = new byte[(int) imageFile.length()];
            fileInputStream.read(imageBytes);
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] inputStream2ByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    /**
     * 通过文件解析
     * @param file 文件
     * @return
     * @throws IOException
     */
    public static byte[] readByFile(File file) throws IOException {
        InputStream in = new FileInputStream(file);
        byte[] data = inputStream2ByteArray(in);
        in.close();
        return data;
    }
}
