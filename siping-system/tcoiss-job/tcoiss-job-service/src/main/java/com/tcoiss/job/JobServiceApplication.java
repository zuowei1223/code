package com.tcoiss.job;

import com.tcoiss.common.security.annotation.EnableCustomConfig;
import com.tcoiss.common.security.annotation.EnableRyFeignClients;
import com.tcoiss.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * 任务调度服务
 */
@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringCloudApplication
public class JobServiceApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(JobServiceApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  调度执行服务启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
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
