package com.zhangxl.dao.impl;

import com.zhangxl.dao.FavoriteDao;
import com.zhangxl.model.Favorite;
import com.zhangxl.utils.C3p0Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author zhangxl98
 * @Date 4/26/19 9:20 AM
 * @OS Ubuntu 18.04 LTS
 * @Device DELL-Inspiron-15-7559
 * @Modified By
 * @Version V1.0.0
 * @Description 实现 Favorite 相关的 CRUD 操作的方法
 */
public class FavoriteDaoImpl implements FavoriteDao {

    /**
     * 初始化 JdbcTemplate
     */
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(C3p0Util.getDataSource());

    /**
     * 根据传入的 strRid 和 uid 查询出对应的 Favorite
     * <pre>createTime:
     * 4/26/19 9:22 AM</pre>
     *
     * @param strRid
     * @param uid
     * @return
     */
    @Override
    public Favorite queryByRidUid(String strRid, Integer uid) {

        if (StringUtils.isNotBlank(strRid)) {
            // strRid 不为 空串、空白符、null
            Integer rid = Integer.valueOf(strRid);

            // 从数据库中查询
            String sql = "SELECT rid, date, uid FROM tab_favorite WHERE rid=? AND uid=?";
            try {
                return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<Favorite>(Favorite.class), rid, uid);
            } catch (DataAccessException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 对传入的 rid 和 uid 进行数据持久化操作
     * <pre>createTime:
     * 4/26/19 10:41 AM</pre>
     *
     * @param rid
     * @param uid
     * @return
     */
    @Override
    public int save(Integer rid, Integer uid) {

        String sql = "INSERT INTO tab_favorite (rid, date, uid) VALUES (?,?,?)";
        return jdbcTemplate.update(sql, rid, new Date(), uid);
    }

    /**
     * 根据传入的参数进行分页查询
     * <pre>createTime:
     * 4/27/19 8:38 AM</pre>
     *
     * @param uid
     * @param startCount
     * @param pageSize
     * @return
     */
    @Override
    public List<Map<String, Object>> pageQuery(Integer uid, int startCount, int pageSize) {

        String sql = "SELECT f.rid, date, uid, rname, price, routeIntroduce, rflag, rdate, isThemeTour, count, cid, rimage, sid, sourceId FROM tab_favorite f JOIN tab_route r ON f.rid = r.rid WHERE f.uid=? LIMIT ?,?";
        return jdbcTemplate.queryForList(sql, uid, startCount, pageSize);
    }

    /**
     * 查询 uid 对应的 user 的 favorite 记录条数
     * <pre>createTime:
     * 4/27/19 10:06 AM</pre>
     *
     * @param uid
     * @return
     */
    @Override
    public int queryTotalCount(Integer uid) {

        String sql = "SELECT count(*) FROM tab_favorite WHERE uid=?";
        try {
            return jdbcTemplate.queryForObject(sql,Integer.class,uid);
        } catch (DataAccessException e) {
            return 0;
        }
    }
}
