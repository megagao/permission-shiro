package cn.itcast.ssm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.itcast.ssm.shiro.CustomRealm;

/*
 * 手动调用Controller来清楚shiro的缓存
 */
@Controller
public class ClearShiroCache {

    // 注入realm
    @Autowired
    private CustomRealm customRealm;

    @RequestMapping("/clearShiroCache")
    public String clearShiroCache() {

	// 清除缓存，这个本来要写在service里完成修改之后
	customRealm.clearCached();

	return "success";
    }

}
