package com.zhangxl.service.impl;

import com.alibaba.fastjson.JSON;
import com.zhangxl.dao.RouteDao;
import com.zhangxl.dao.impl.RouteDaoImpl;
import com.zhangxl.model.Route;
import com.zhangxl.service.RouteService;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author zhangxl98
 * @Date 4/24/19 7:45 PM
 * @OS Ubuntu 18.04 LTS
 * @Device DELL-Inspiron-15-7559
 * @Modified By
 * @Version V1.0.0
 * @Description 处理旅游路线相关业务的实现类
 */
public class RouteServiceImpl implements RouteService {

    /**
     * 初始化 dao
     */
    private RouteDao routeDao = new RouteDaoImpl();

    /**
     * 处理黑马精选路线业务
     * <br>人气旅游、最新旅游、主题旅游
     * <pre>createTime:
     * 4/24/19 7:46 PM</pre>
     *
     * @return
     */
    @Override
    public String getRouteCareChoose() {

        // 获取人气旅游路线
        List<Route> populateRouteList = routeDao.queryOrderByCount();
        // 获取最新旅游路线
        List<Route> newestRouteList = routeDao.queryOrderByRdate();
        // 获取主题旅游路线
        List<Route> themeRouteList = routeDao.queryByIsThemeTour();

        // 对获取到的结果使用 Map 进行封装
        Map<String, List<Route>> result = new HashMap<>();
        result.put("populateRouteList", populateRouteList);
        result.put("newestRouteList", newestRouteList);
        result.put("themeRouteList", themeRouteList);

        // 转换为 JSON 字符串并返回
        return JSON.toJSONString(result);
    }

    /**
     * 处理分页查询线路业务
     * <pre>createTime:
     * 4/25/19 10:22 AM</pre>
     *
     * @param strpageNum
     * @param strpageSize
     * @return
     */
    @Override
    public String pageQuery(String strpageNum, String strpageSize) {

        /**
         * 初始化 pageNum、pageSize
         */
        int pageNum = 1;
        int pageSize = 10;

        /*
          blank：代表的是空串("")、空白符(空格""，" "，制表符"\t"，回车符"\r"，"\n"等)以及null值
          isNotBlank() ==> 判断单个字符串是否为空

          Date: 4/25/19 10:40 AM
        */

        /*
          接收前端页面传递的 strpageNum 和 strpageSize

          Date: 4/25/19 10:46 AM
        */
        if (StringUtils.isNotBlank(strpageNum)) {
            // 如果 strpageNum 不为空
            pageNum = Integer.valueOf(strpageNum);
        }

        if (StringUtils.isNotBlank(strpageSize)) {
            // 如果 strpageSize 不为空
            pageSize = Integer.valueOf(strpageSize);
        }

        /*
          计算起始记录
          LIMIT startCount,pageSize
          数据库从第几条记录开始查询

          Date: 4/25/19 10:48 AM
        */
        int startCount = (pageNum - 1) * pageSize;

        // 调用 Dao 层，获取分页数据
        List<Route> routeList = routeDao.pageQuery(startCount,pageSize);

        // 封装数据
        // 用于封装数据的 Map
        Map<String, Object> result = new HashMap<>();
        result.put("data",routeList);

        // 转换为 JSON 字符串，并返回
        return JSON.toJSONString(result);
    }
}
