package com.mmm.springbootinit.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.mmm.springbootinit.model.entity.Chart;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *@Date：2024/3/2
 *@Author：mmm
 *@return：
 * 图表视图
 */
@Data
public class ChartVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表数据
     */
    private String chartData;

    /**
     * 图表类型
     */
    private String chartType;

    /**
     * 生成分析图表
     */
    private String genChart;

    /**
     * 生成分析结果
     */
    private String genResult;

    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 创建人信息
     */
    private UserVO user;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 包装类转对象
     *
     * @param chartVO
     * @return
     */
    public static Chart voToObj(ChartVO chartVO) {
        if (chartVO == null) {
            return null;
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartVO, chart);
        return chart;
    }

    /**
     * 对象转包装类
     *
     * @param chart
     * @return
     */
    public static ChartVO objToVo(Chart chart) {
        if (chart == null) {
            return null;
        }
        ChartVO chartVO = new ChartVO();
        BeanUtils.copyProperties(chart, chartVO);
        return chartVO;
    }
}
