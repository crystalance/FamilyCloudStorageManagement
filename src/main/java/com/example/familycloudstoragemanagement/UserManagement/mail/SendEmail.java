package com.example.familycloudstoragemanagement.UserManagement.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Component
public class SendEmail {
    //引入邮件接口
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public  void sendEmail(String toEmail){
        String from = "455051809@qq.com";
        //创建邮件
        SimpleMailMessage message = new SimpleMailMessage();
        //设置发件人信息
        message.setFrom(from);
        //发给谁
        message.setTo(toEmail);
        message.setSubject("您本次的验证码是");
        //生成六位随机验证码
        String verCode = VerCodeGenerateUtil.generateVerCode();
        //TimeAndVerCode.verCodeMap.put(toEmail,verCode);
        stringRedisTemplate.opsForValue().set(toEmail,verCode,60*5, TimeUnit.SECONDS);


        // System.out.println(verCode);
        //获得当前时间
        //TimeAndVerCode.currentTime = new Date();
        //TimeAndVerCode.currentTimeMap.put(toEmail,new Date());

        message.setText("尊敬的用户,您好:\n"
                + "\n本次请求的邮件验证码为:" + verCode + ",本验证码 5 分钟内效，请及时输入。（请勿泄露此验证码）\n"
                + "\n如非本人操作，请忽略该邮件。\n(这是一封通过自动发送的邮件，请不要直接回复）");

        mailSender.send(message);
    }

    /**
     *
     * @param toEmail 要发送的邮箱
     * @param requestEmail
     */
    public void sendInviteMail(String toEmail, String requestEmail){
        String from = "455051809@qq.com";
        //创建邮件
        SimpleMailMessage message = new SimpleMailMessage();
        //设置发件人信息
        message.setFrom(from);
        //发给谁
        message.setTo(toEmail);
        message.setSubject("[加入新的家庭共享空间]您本次的验证码是");
        //生成六位随机验证码
        String verCode = VerCodeGenerateUtil.generateVerCode();
        //TimeAndVerCode.verCodeMap.put(toEmail,verCode);
        stringRedisTemplate.opsForValue().set(toEmail,verCode,60*5, TimeUnit.SECONDS);


        // System.out.println(verCode);
        //获得当前时间
        //TimeAndVerCode.currentTime = new Date();
        //TimeAndVerCode.currentTimeMap.put(toEmail,new Date());

        message.setText("尊敬的用户,您好:\n" + "用户："+requestEmail+" 正在邀请您加入家庭共享空间"
                + "\n本次请求的邮件验证码为:" + verCode + ",本验证码 5 分钟内效，请及时输入。（请勿泄露此验证码）\n"
                + "\n如非本人操作，请忽略该邮件。\n(这是一封通过自动发送的邮件，请不要直接回复）");

        mailSender.send(message);
    }

    //后端验证邮箱格式
    public  boolean isEmail(String email) {
        if (email == null || email.length() < 1 || email.length() > 256) {
            return false;
        }
        //pattern, java自带的正则表达式
        Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        return pattern.matcher(email).matches();
    }
}
