package com.tcoiss.business.bill;

import com.tcoiss.common.security.annotation.EnableCustomConfig;
import com.tcoiss.common.security.annotation.EnableRyFeignClients;
import com.tcoiss.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * @author zw
 */

@EnableCustomConfig
@EnableCustomSwagger2
@EnableRyFeignClients
@SpringCloudApplication
public class BillBusApplication {

	public static void main(String[] args) {
        SpringApplication.run(BillBusApplication.class, args);
	}

}