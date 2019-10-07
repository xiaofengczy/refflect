package com.jzit.cn.service.impl;

import com.jzit.cn.entity.CaipiaoData;
import com.jzit.cn.entity.CaipiaoEntity;
import com.jzit.cn.entity.CaipiaoResDO;
import com.jzit.cn.entity.ResultBo;
import com.jzit.cn.service.ReffectService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.function.json.JsonMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

/**
 * FileName: ReffectServiceImpl Description:
 *
 * @author caozhongyu
 * @create 2019/10/6
 */
@Service
public class ReffectServiceImpl implements ReffectService {

  @Resource
  private RestTemplate restTemplate;

  @Resource
  private JsonMapper jsonMapper;

  public static final String URL = "http://f.apiplus.net/newly.do?code=ssq&format=json&rows=5";
  private static Random random = new Random();

  @Override
  public CaipiaoResDO calculateNum() {
    CaipiaoResDO caipiaoResDO = new CaipiaoResDO();
    //获取上期彩票号码
    List<CaipiaoData> resultBo = getPreNum();
    if (CollectionUtils.isEmpty(resultBo)) {
      return caipiaoResDO;
    }
    //计算红球
    CaipiaoData curData = resultBo.stream().findFirst().get();
    //处理数据
    String opencodeStr = curData.getOpencode();
    List<String> opencodeList = Arrays.asList(opencodeStr.split("\\+"));
    calculateRedNumber(opencodeList.get(0), caipiaoResDO);
    //计算蓝球
    calculateBlueNumber(resultBo, caipiaoResDO);
    //上一期开奖号码
    caipiaoResDO.setPreNum(opencodeStr);
    return caipiaoResDO;
  }

  private void calculateBlueNumber(List<CaipiaoData> caipiaoDataList, CaipiaoResDO caipiaoResDO) {
    List<String> openCodeList = caipiaoDataList.stream().map(CaipiaoData::getOpencode)
        .collect(Collectors.toList());
    List<Integer> blueNumList = new ArrayList<>();
    openCodeList.stream().forEach(o -> {
      String blueNum = o.split("\\+")[1];
      blueNumList.add(Integer.valueOf(blueNum));
    });
    int first = blueNumList.get(blueNumList.size() - 1);
    int second = blueNumList.get(blueNumList.size() - 2);
    int third = blueNumList.get(blueNumList.size() - 3);

    List<Integer> list = new ArrayList<>();

    Integer r1 = blueNumList.stream().reduce(Integer::sum).get();
    Integer r2 = first - second + third;
    Integer r3 = first + second - third;
    Integer r4 = second - (first - third);
    list.add(r1);
    list.add(r2);
    list.add(r3);
    list.add(r4);
    List<Integer> disList = list.stream().collect(Collectors.toList());
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < disList.size(); i++) {
      int abs = Math.abs(list.get(i));
      if (abs <= 16) {
        result.add(abs);
      } else {
        result.add(abs % 10);
      }
    }
    //计算特别推荐号
    Integer min = blueNumList.stream().reduce(Integer::min).get();
    Integer max = blueNumList.stream().reduce(Integer::max).get();
    Integer re = (min + max) / 2;
    if (result.contains(re)) {
      caipiaoResDO.setRecommBlueNum(String.valueOf(re));
    } else {
      Integer a = re + 1;
      Integer b = re - 1;
      caipiaoResDO.setRecommBlueNum(a + " " + b);
    }
    String blueResultStr = result.stream().distinct().map(m -> String.valueOf(m))
        .collect(Collectors.joining(" "));

    String blueNumStr = blueNumList.stream().map(m -> String.valueOf(m))
        .collect(Collectors.joining(" "));

    caipiaoResDO.setBlueList(blueResultStr);
    caipiaoResDO.setPreBlueNumList(blueNumStr);
  }

  private void calculateRedNumber(String redNumber,
      CaipiaoResDO caipiaoResDO) {
    List<String> redList = Arrays.asList(redNumber.trim().split(","));
    List<Integer> redListInt = redList.stream().map(n -> Integer.valueOf(n))
        .collect(Collectors.toList());
    Integer total = redListInt.stream().reduce((item, next) -> item + next).get();
    List<Integer> resultMol = new ArrayList<>();
    redListInt.stream().forEach(r1 -> {
      Integer residue = (total - r1) / r1;
      //找出尾数相同数据
      int mol = residue % 10;
      while (mol <= 33) {
        resultMol.add(mol);
        mol += 10;
      }
    });

    List<String> randomList = new ArrayList<>();
    while (randomList.size() < 6) {
      String randomNum = String.valueOf(resultMol.get(random.nextInt(resultMol.size())));
      if (randomList.contains(randomNum)) {
        continue;
      } else {
        randomList.add(randomNum);
      }
    }
    String randomRedStr = randomList.stream().sorted().collect(Collectors.joining(" "));
    String resultMolStr = resultMol.stream().distinct().sorted().map(m -> String.valueOf(m))
        .collect(Collectors.joining(" "));
    caipiaoResDO.setRedList(resultMolStr);
    caipiaoResDO.setRandomRedNum(randomRedStr);
  }

  private List<CaipiaoData> getPreNum() {
    ResponseEntity<String> result = restTemplate.getForEntity(URL, String.class);
    if (!Objects.equals(result.getStatusCode().value(), 200)) {
      return null;
    }
    String json = result.getBody();
    CaipiaoEntity CaipiaoEntity = jsonMapper.toObject(json, CaipiaoEntity.class);
    return CaipiaoEntity.getData();
  }
}


























