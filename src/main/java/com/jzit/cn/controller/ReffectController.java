package com.jzit.cn.controller;

import com.jzit.cn.service.ReffectService;
import com.jzit.cn.utils.R;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * FileName: ReffectController Description:
 *
 * @author caozhongyu
 * @create 2019/10/6
 */
@RestController
public class ReffectController {

  @Resource
  private ReffectService reffectService;

  /**
   * 获取下期彩票号码
   */
  @GetMapping("/currentNum")
  public R currentNum() {
    return R.ok().put("res", reffectService.calculateNum());
  }


}