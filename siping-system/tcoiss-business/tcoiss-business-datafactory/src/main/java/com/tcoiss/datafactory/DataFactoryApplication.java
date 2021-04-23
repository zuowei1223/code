package com.tcoiss.datafactory;

import com.tcoiss.common.security.annotation.EnableCustomConfig;
import com.tcoiss.common.security.annotation.EnableRyFeignClients;
import com.tcoiss.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * 数据工厂
 * 
 * @author tcoiss
 */
@EnableCustomConfig
@EnableCustomSwagger2   
@EnableRyFeignClients
@SpringCloudApplication
public class DataFactoryApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(DataFactoryApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  数据工厂模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
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
