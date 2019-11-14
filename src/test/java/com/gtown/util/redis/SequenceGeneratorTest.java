package com.gtown.util.redis;

import com.gtown.util.redis.sequence.SequenceGenerator;
import com.gtown.util.redis.sequence.TimebaseSequenceGenerator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = SequenceGeneratorApplication.class
)
public class SequenceGeneratorTest {
    @Resource
    private SequenceGenerator         sequenceGenerator;
    @Resource
    private TimebaseSequenceGenerator timebaseSequenceGenerator;

    @Test
    public void testSequenceGenerator() {
        String key = "test:sequence";
        long   n   = sequenceGenerator.next(key);
        Assert.assertTrue(n >= 0);

        List<Long> l = sequenceGenerator.next(key, 10);
        Assert.assertEquals(l.size(), 10);

        String seq = sequenceGenerator.next(key, "%04d");
        Assert.assertTrue(seq.matches("0{0,3}\\d+"));
        List<String> ll = sequenceGenerator.next(key, "%05d", 10);
        ll.forEach(s -> Assert.assertTrue(s.matches("0{0,4}\\d+")));
    }

    @Test
    public void testTimebaseSequenceGenerator() {
        String key     = "test:timebaseSequence";
        String timeReg = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229))([0-1]?[0-9]|2[0-3])([0-5][0-9])([0-5][0-9])";
        String seq     = timebaseSequenceGenerator.YYYYMMDDHHMMSS.next(key);
        Assert.assertTrue(seq.matches(timeReg + "(0{0,3}\\d+)"));

        List<String> l = timebaseSequenceGenerator.YYYYMMDDHHMMSS.next(key, 10);
        l.forEach(s -> Assert.assertTrue(s.matches(timeReg + "(0{0,3}\\d+)")));

        seq = timebaseSequenceGenerator.YYYYMMDDHHMMSS.next("test:timebaseSequence", "%010d");
        Assert.assertTrue(seq.matches(timeReg + "(0{0,9}\\d+)"));

        l = timebaseSequenceGenerator.YYYYMMDDHHMMSS.next(key, 10, "%010d");
        l.forEach(s -> Assert.assertTrue(s.matches(timeReg + "(0{0,9}\\d+)")));
    }

    @Test
    public void testSequenceInit() {
        String key = "fms:channel:code";
        sequenceGenerator.init(key, 19L);
    }
}
