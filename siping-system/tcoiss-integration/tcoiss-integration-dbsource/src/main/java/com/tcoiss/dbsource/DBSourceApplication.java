package com.tcoiss.dbsource;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import com.tcoiss.common.security.annotation.EnableCustomConfig;
import com.tcoiss.common.security.annotation.EnableRyFeignClients;
import com.tcoiss.common.swagger.annotation.EnableCustomSwagger2;

/**
 * 数据引擎模块
 * 
 * @author tcoiss
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringCloudApplication
public class DBSourceApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(DBSourceApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  数据引擎模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              ");
    }
}
