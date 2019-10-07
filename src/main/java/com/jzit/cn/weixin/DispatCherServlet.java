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

    switch (msgType) {
      case "text":
        CaipiaoResDO caipiaoResDO = reffectService.calculateNum();
        String content = jsonMapper.toString(caipiaoResDO);
        String toUserName = mapResult.get("ToUserName");
        String fromUserName = mapResult.get("FromUserName");
        String textMessage = setTextMessage(content, toUserName, fromUserName);
        log.info("postdispatCherServlet() info:{}", textMessage);
        out.print(textMessage);
        break;
      default:
        break;
    }
    out.close();
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
