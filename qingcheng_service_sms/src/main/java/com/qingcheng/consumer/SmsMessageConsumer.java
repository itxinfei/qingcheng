package com.qingcheng.consumer;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.exceptions.ClientException;
import com.qingcheng.util.SMSUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.util.Map;

/**
 * @author 戴金华
 * @date 2019-11-29 14:56
 */
public class SmsMessageConsumer implements MessageListener {




    //从消息队列中取出 手机号和验证码 执行发动短信的服务
    @Override
    public void onMessage(Message message) {
        String json = new String(message.getBody());
        Map<String,String> map = JSON.parseObject(json, Map.class);
        String phone = map.get("phone");
        String code = map.get("code");
        System.out.println(phone+" "+code);

        //将取出的验证码发出去
        try {
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,phone,code);
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
