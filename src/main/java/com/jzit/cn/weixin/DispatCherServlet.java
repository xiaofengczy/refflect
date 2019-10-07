package com.jzit.cn.weixin;

import com.jzit.cn.entity.CaipiaoResDO;
import com.jzit.cn.entity.TextMessage;
import com.jzit.cn.service.ReffectService;
import com.jzit.cn.weixin.utils.CheckUtil;
import com.jzit.cn.weixin.utils.XmlUtils;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.cloud.function.json.JsonMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * 微信事件通知
 */

@RestController
@Slf4j
public class DispatCherServlet {

  @Resource
  private ReffectService reffectService;

  @Resource
  private JsonMapper jsonMapper;

  /**
   * 微信验证
   */
  @RequestMapping(value = "/caipiao", method = RequestMethod.GET)
  public String getDispatCherServlet(String signature, String timestamp, String nonce,
      String echostr) {
    boolean checkSignature = CheckUtil.checkSignature(signature, timestamp, nonce);
    if (!checkSignature) {
      return null;
    }
    return echostr;
  }

  /**
   * 功能说明:微信事件通知
   */
  @RequestMapping(value = "/caipiao", method = RequestMethod.POST)
  public void postdispatCherServlet(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    Map<String, String> mapResult = XmlUtils.parseXml(request);
    if (mapResult == null) {
      return;
    }
    String msgType = mapResult.get("MsgType");
    PrintWriter out = response.getWriter();
    String reqContent = mapResult.get("Content");

    String content;
    switch (msgType) {
      case "text":
        CaipiaoResDO caipiaoResDO = reffectService.calculateNum();
        String toUserName = mapResult.get("ToUserName");
        String fromUserName = mapResult.get("FromUserName");
        content = getMsg(reqContent, caipiaoResDO);
        String textMessage = setTextMessage(content, toUserName, fromUserName);
        log.info("postdispatCherServlet() info:{}", textMessage);
        out.print(textMessage);
        break;
      default:
        break;
    }
    out.close();
  }

  protected String getMsg(String reqContent, CaipiaoResDO caipiaoResDO) {
    String content;
    switch (reqContent){
      //查询上一期开奖号码
      case "1":
        content = "上一期("+caipiaoResDO.getPreNo()+")开奖号码："+caipiaoResDO.getPreNum();
        break;
      case "2":
        content = "前五期蓝号:"+caipiaoResDO.getPreBlueNumList().replace(" ",",");
        break;
      case "3":
        content = "红球备选号:"+caipiaoResDO.getRedList().replace(" ", ",");
        break;
      case "4":
        content = "蓝球备选号:"+caipiaoResDO.getBlueList().replace(" ", ",");
        break;
      case "5":
        content = "推荐蓝球:"+caipiaoResDO.getRecommBlueNum().replace(" ", ",");
        break;
      case "6":
        content = "随机号码:"+caipiaoResDO.getRandomRedNum().replace(" ", ",")+"+"+caipiaoResDO.getRecommBlueNum();
        break;
      default:
        content= "输入功能号有误，请重新输入";
        break;
    }
    return content;
  }

  public String setTextMessage(String content, String toUserName, String fromUserName) {
    TextMessage textMessage = new TextMessage();
    textMessage.setCreateTime(new Date().getTime());
    textMessage.setFromUserName(toUserName);
    textMessage.setToUserName(fromUserName);
    textMessage.setContent(content);
    textMessage.setMsgType("text");
    String messageToXml = XmlUtils.messageToXml(textMessage);
    return messageToXml;
  }

}
