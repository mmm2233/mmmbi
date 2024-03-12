package com.mmm.springbootinit.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;

/**
 * 反向压力
 * @Author：mmm
 * @Package：com.mmm.springbootinit.utils
 * @Project：mmmbi-backend
 * @name：ServerMetricsUtil
 * @Date：2024/3/11 0:37
 * @Filename：ServerMetricsUtil
 */
// 这里每次执行都去查询负载，此方法执行很慢，还考虑不到网络IO与磁盘IO的影响，仅仅通过CPU负载及内存来判断
public class ServerMetricsUtil {
    private static OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    private static MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    //private static ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    /*
        获取当前服务器CPU的占用比
     */
    public static double getCpuUsagePercentage() {
        return osBean.getProcessCpuLoad()*100;
    }

    /*
        获取服务器内存使用比
     */
    public static double getMemoryUsagePercentage() {
        long used = memoryMXBean.getHeapMemoryUsage().getUsed();
        long max = memoryMXBean.getHeapMemoryUsage().getMax();
        return ((double) used / max) * 100;
    }

    /*
        判断服务使用同步还是异步
     */
    /*public static boolean shouldProvideSync() {
        //double cpuUsagePercentage = getCpuUsagePercentage();
        double memoryUsagePercentage = getMemoryUsagePercentage();

        double cpuThreshould = 70.0;
        double memoryThreshold = 80.0;

        if (cpuThreshould < memoryThreshold && memoryUsagePercentage < memoryThreshold){
            return true;
        }

        return false;
    }*/

    public static ServerLoadInfo getLoadInfo() {
        double cpuUsagePercentage = getCpuUsagePercentage();
        double memoryUsagePercentage = getMemoryUsagePercentage();
        return new ServerLoadInfo(cpuUsagePercentage,memoryUsagePercentage);
    }
}
