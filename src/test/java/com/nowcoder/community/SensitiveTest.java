package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class )
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text = "傻*逼，想赌-博的，想开票的，想嫖个娼的，速速来找我！傻";
        String postText = sensitiveFilter.filter(text);
        System.out.println(postText);
    }

}
