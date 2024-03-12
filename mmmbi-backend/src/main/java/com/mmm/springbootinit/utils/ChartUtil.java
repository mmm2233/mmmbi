package com.mmm.springbootinit.utils;

import com.mmm.springbootinit.model.entity.Chart;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.utils
 * @Project：mmmbi-backend
 * @name：ChartUtil
 * @Date：2024/3/12 15:08
 * @Filename：ChartUtil
 */
public class ChartUtil {

    /**
     * 压缩json
     *
     * @param data 数据
     * @return {@link String}
     */
    public static String compressJson(String data) {
        data = data.replaceAll("\t+", "");
        data = data.replaceAll(" +", "");
        data = data.replaceAll("\n+", "");
        return data;
    }

    /**
     * 建立用户输入 (单条消息)
     *
     * @param chart 图表
     * @return {@link String}
     */
    public static String buildUserInput(Chart chart) {
        // 获取CSV
        // 构造用户输入
        StringBuilder userInput = new StringBuilder("");
        // 拼接图表类型;
        String userGoal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += ", 请使用 " + chartType;
        }
        userInput.append("分析需求: ").append('\n');
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        userInput.append(csvData).append("\n");
        return userInput.toString();
    }
}
