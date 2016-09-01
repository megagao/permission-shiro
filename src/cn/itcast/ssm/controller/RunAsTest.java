package cn.itcast.ssm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("runas")
public class RunAsTest {

    
    @RequestMapping("/grant/{toUserId}")
    public String grant(){
	
	return "";
    }
}
