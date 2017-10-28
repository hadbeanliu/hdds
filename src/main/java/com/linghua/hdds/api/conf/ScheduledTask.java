package com.linghua.hdds.api.conf;

import com.linghua.hdds.common.TableUtil;
import com.linghua.hdds.preference.model.BaseTagWithLabelRecommendModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class ScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    private String stopRow;

    @Scheduled(cron = "0 0 3 * * ?")
    public void loadRecommendArticle(){
        long begin = System.currentTimeMillis();
        logger.info("重新加载推荐文章数据，"+ System.currentTimeMillis());
        BaseTagWithLabelRecommendModel.reload();
        BaseTagWithLabelRecommendModel.getInstance(TableUtil.getEndKey(1, Calendar.MONTH));
        logger.info("重新加载数据成功，加载耗时为:"+(System.currentTimeMillis() - begin));
    }
}
