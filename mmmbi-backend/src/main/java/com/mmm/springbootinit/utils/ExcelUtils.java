package com.mmm.springbootinit.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel工具类
 * @Author：mmm
 * @Package：com.mmm.springbootinit.utils
 * @Project：mmmbi-backend
 * @name：ExcelUtils
 * @Date：2024/3/5 11:45
 * @Filename：ExcelUtils
 */
@Slf4j
public class ExcelUtils {

    public static String excelToCsv(MultipartFile multipartFile)  {

        List<Map<Integer, String>> list = null;
        try {
            list = EasyExcel.read(multipartFile.getInputStream())
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet()
                    .headRowNumber(0)
                    .doReadSync();
        } catch (IOException e) {
            log.error("csv转换失败");
            e.printStackTrace();
        }
        if (CollUtil.isEmpty(list)){
            return "";
        }
        //to csv
        StringBuilder stringBuilder =new StringBuilder();
        // 1.表头
        LinkedHashMap<Integer, String> headerMap = (LinkedHashMap)list.get(0);
        headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
        stringBuilder.append(StringUtils.join(headerMap.values(),","));
        stringBuilder.append(",");
        // 2.从第一行读取数据
        for(int i =1;i < list.size();i++){
            LinkedHashMap<Integer, String> dataMap = (LinkedHashMap)list.get(i);
            List<String> datalist = dataMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
            stringBuilder.append(StringUtils.join(datalist,","));
            stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }

    /**
     * 检查文件
     *
     * @param file 文件
     */
    public static void checkExcelFile(MultipartFile file) {
        long size = file.getSize();
        String originalFilename = file.getOriginalFilename();
        // 校验文件大小
        final long ONE_MB = 1024 * 1024L;
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR, "文件超过 1M");
        // 校验文件后缀 aaa.png
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xlsx", "xls");
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix), ErrorCode.PARAMS_ERROR, "文件后缀非法");
    }
}
