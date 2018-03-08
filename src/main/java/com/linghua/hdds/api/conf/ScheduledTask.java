package com.linghua.hdds.api.conf;

import com.linghua.hdds.common.DailyTaskBuilder;
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
        logger.info("重新加载推荐文章数据"+ System.currentTimeMillis());
        BaseTagWithLabelRecommendModel.reloadAll();
        BaseTagWithLabelRecommendModel.getInstance(TableUtil.getEndKey(1, Calendar.MONTH));
        logger.info("重新加载数据成功，加载耗时为:"+(System.currentTimeMillis() - begin));
    }

    @Scheduled(cron = "0 0 4/1 * * ?")
    public void retrainHistoryData(){
        long begin = System.currentTimeMillis();
        logger.info("清洗转换历史记录："+ System.currentTimeMillis());
        DailyTaskBuilder retrain = new DailyTaskBuilder();
        retrain.reComputeUserCatalogPrefs(1/24);
        logger.info("转换成功，加载耗时为:"+(System.currentTimeMillis() - begin));
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void rebuildUserGraph(){
        long begin = System.currentTimeMillis();
        logger.info("开始构建用户画像："+ System.currentTimeMillis());
        UserGraphAnalysis retrain = new UserGraphAnalysis();
        retrain.build("headlines");
        logger.info("转换成功，加载耗时为:"+(System.currentTimeMillis() - begin));
    }
}
