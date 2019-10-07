package com.jzit.cn.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * FileName: ZouResultBo Description:
 *
 * @author caozhongyu
 * @create 2019/10/7
 */
@Data
public class ZouResultBo {

  /**彩票id*/
  @JsonProperty("lottery_id")
  private String lotteryId;

  /**彩票名*/
  @JsonProperty("lottery_name")
  private String lotteryName;

  /**彩票开奖号*/
  @JsonProperty("lottery_res")
  private String lotteryRes;

  /**彩票期号*/
  @JsonProperty("lottery_no")
  private String lotteryNo;

  /**彩票开奖日期*/
  @JsonProperty("lottery_date")
  private String lotteryDate;
}