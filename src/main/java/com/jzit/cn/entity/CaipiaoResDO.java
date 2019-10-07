package com.jzit.cn.entity;

import java.util.List;
import lombok.Data;

/**
 * FileName: CaipiaoResDO Description:
 *
 * @author caozhongyu
 * @create 2019/10/7
 */
@Data
public class CaipiaoResDO {

  /**红球备选号*/
  private String redList;

  /**蓝球备选号*/
  private String blueList;

  /**上一期开奖号码*/
  private String preNum;

  /**推荐蓝球*/
  private String recommBlueNum;

  /**备选号中随机红球*/
  private String randomRedNum;

  /**前五期蓝球号*/
  private String preBlueNumList;

  /**上一期期号*/
  private String preNo;

}