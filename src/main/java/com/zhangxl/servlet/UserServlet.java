package com.zhangxl.servlet;

import com.alibaba.fastjson.JSON;
import com.zhangxl.model.User;
import com.zhangxl.service.UserService;
import com.zhangxl.service.impl.UserServiceImpl;
import org.apache.commons.beanutils.BeanUtils;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author zhangxl98
 * @Date 4/22/19 6:55 PM
 * @OS Ubuntu 18.04 LTS
 * @Device DELL-Inspiron-15-7559
 * @Modified By
 * @Version V1.0.0
 * @Description 接收 user 相关的请求
 */
@WebServlet("/user.do")
public class UserServlet extends BaseServlet{

    private UserService userService = new UserServiceImpl();

    /**
     * 接收 email 唯一性校验请求
     * @param req
     * @param resp
     */
    public void checkEmail(HttpServletRequest req, HttpServletResponse resp){
        // 接收请求数据
        String email = req.getParameter("email");

        // 处理数据

        // 如果 checkFlag == true -> 校验通过，数据库中没有这条数据
        boolean checkFlag = userService.checkEmail(email);

        // 响应数据
        try {
            resp.getWriter().println(checkFlag);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收注册请求
     * @param req
     * @param resp
     */
    public void register(HttpServletRequest req, HttpServletResponse resp){
        // 接收请求数据
        Map<String, String[]> parameterMap = req.getParameterMap();

        // 封装结果集的 Map
        Map<String, Object> result = new HashMap<>();

        // 处理验证码校验

        // 获取用户输入的验证码
        String[] userCode = parameterMap.get("check");
        //获取服务器生成的验证码
        String serverCode = (String) req.getSession().getAttribute("code");

        //验证码校验
        if (!serverCode.equalsIgnoreCase(String.valueOf(userCode[0]))) {
            //校验不通过：1、看到登录页面；2、页面上需要嵌入错误信息；
            result.put("msg","验证码错误");
        }else {

            // 使用 BeanUtils 工具类，把 Map 中的数据封装到实体类中
            User user = new User();

            try {
                BeanUtils.populate(user, parameterMap);

                // 处理数据：把 user 插入到数据库中

                boolean registerFlag = userService.addUser(user);

                result.put("registerFlag", registerFlag);

            } catch (Exception e) {
                e.printStackTrace();

                result.put("registerFlag", false);
                result.put("msg", "数据异常，请联系管理员！");
            }
        }

        // 响应数据：json    {name:value} -- Map<key, value>
        String str = JSON.toJSONString(result);
        try {
            resp.getWriter().println(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成验证码
     * @param req
     * @param resp
     */
    public void checkCode(HttpServletRequest req, HttpServletResponse resp){
        // gui 生成图片
        // 1 高和宽
        int height = 30;
        int width = 80;
//        String data = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
        String data = "A";
        Random random = new Random();
        // 2 创建一个图片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 3 获得画板
        Graphics g = image.getGraphics();
        // 4 填充一个矩形
        // * 设置颜色
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.WHITE);
        g.fillRect(1, 1, width - 2, height - 2);
        // * 设置字体
        g.setFont(new Font("宋体", Font.BOLD | Font.ITALIC, 25));
        StringBuffer sb = new StringBuffer();
        // 5 写随机字
        for (int i = 0; i < 4; i++) {
            // 设置颜色--随机数
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));

            // 获得随机字
            int index = random.nextInt(data.length());
            String str = data.substring(index, index + 1);
            // 写入
            g.drawString(str, width / 6 * (i + 1), 20);
            sb.append(str);//  获取验证码数据
        }
        //  验证码保存到session中
        req.getSession().setAttribute("code",sb.toString());
        // 6 干扰线
        for (int i = 0; i < 3; i++) {
            // 设置颜色--随机数
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            // 随机绘制先
            g.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
            // 随机点
            g.drawOval(random.nextInt(width), random.nextInt(height), 2, 2);
        }

        // end 将图片响应给浏览器
        ImageIO.setUseCache(false);
        try {
            ImageIO.write(image, "PNG", resp.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}