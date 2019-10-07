package com.jzit.cn.weixin;

import com.alibaba.fastjson.JSONObject;
import com.jzit.cn.entity.CaipiaoResDO;
import com.jzit.cn.entity.TextMessage;
import com.jzit.cn.service.ReffectService;
import com.jzit.cn.weixin.utils.CheckUtil;
import com.jzit.cn.weixin.utils.HttpClientUtil;
import com.jzit.cn.weixin.utils.XmlUtils;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

/**
 * 微信事件通知
 */

@RestController
@Slf4j
public class DispatCherServlet {

  @Resource
  private ReffectService reffectService;

  private static final String REQEST_HTTP = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=";

  private static final String MOLI_HTTP = "http://i.itpk.cn/api.php?question=";

  @Resource
  private RestTemplate restTemplate;

//  private static final String MOLI_HTTP = "http://i.itpk.cn/api.php?question=123&api_key=238587feb54094503430860db15fbf52&api_secret=nx3xlevpjghi";

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
        String toUserName = mapResult.get("ToUserName");
        String fromUserName = mapResult.get("FromUserName");
        content = getMsg(reqContent);
        String textMessage = setTextMessage(content, toUserName, fromUserName);
        log.info("postdispatCherServlet() info:{}", textMessage);
        out.print(textMessage);
        break;
      default:
        break;
    }
    out.close();
  }

  protected String getMsg(String reqContent) {
    String content;
    List<String> reqList = Arrays.asList(new String[]{"1", "2", "3", "4", "5", "6"});
    CaipiaoResDO caipiaoResDO = new CaipiaoResDO();
    if (reqList.contains(reqContent)) {
      caipiaoResDO = reffectService.calculateNum();
    }
    switch (reqContent) {
      //查询上一期开奖号码
      case "1":
        content = "上一期[" + caipiaoResDO.getPreNo() + "]开奖号码：" + caipiaoResDO.getPreNum();
        break;
      case "2":
        content = "前五期蓝号:" + caipiaoResDO.getPreBlueNumList().replace(" ", ",");
        break;
      case "3":
        content = "红球备选号:" + caipiaoResDO.getRedList().replace(" ", ",");
        break;
      case "4":
        content = "蓝球备选号:" + caipiaoResDO.getBlueList().replace(" ", ",");
        break;
      case "5":
        content = "推荐蓝球:" + caipiaoResDO.getRecommBlueNum().replace(" ", ",");
        break;
      case "6":
        content = "随机号码:" + caipiaoResDO.getRandomRedNum().replace(" ", ",") + "+" + caipiaoResDO
            .getRecommBlueNum();
        break;
      case "双色球":
        content = "感谢您关注微信公从号【机智IT】\n"
            + "====================\n"
            + "- 回复【1】: 获取上一期开奖号码\n"
            + "- 回复【2】: 获取前五期蓝球号码\n"
            + "- 回复【3】: 获取本期备选红球号\n"
            + "- 回复【4】: 获取本期备选蓝球号\n"
            + "- 回复【5】: 获取推荐蓝球号\n"
            + "- 回复【6】: 随机一注";
        break;
      default:
        // 調用智能机器人接口
        String url = MOLI_HTTP + reqContent
            + "&api_key=238587feb54094503430860db15fbf52&api_secret=nx3xlevpjghi";
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
        content = forEntity.getBody();
//        String contentResult = HttpClientUtil.doGet(REQEST_HTTP + reqContent);
//        JSONObject jsonObject = new JSONObject().parseObject(contentResult);
//        content = jsonObject.getString("content");
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
