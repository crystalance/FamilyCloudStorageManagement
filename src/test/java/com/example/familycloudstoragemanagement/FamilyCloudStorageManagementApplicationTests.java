package com.example.familycloudstoragemanagement;

import cn.hutool.core.bean.BeanUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.UserFile;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Mappers.UserFileMapper;
import com.example.familycloudstoragemanagement.FileManagement.config.es.FileSearch;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


@Slf4j
@SpringBootTest
class FamilyCloudStorageManagementApplicationTests {

    @Resource
    UserFileMapper userFileMapper;

    public static Executor exec = Executors.newFixedThreadPool(20);

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Test
    void contextLoads() {

       String userFileId = "1768300578196598784";
//        Map<String, Object> param = new HashMap<>();
//        param.put("userFileId", userFileId);
//        List<UserFile> userfileResult = userFileMapper.selectByMap(param);
//        System.out.println(userfileResult.size());


        exec.execute(()->{
            try {
                log.info("userfileid: "+ userFileId );
                Map<String, Object> param = new HashMap<>();
                param.put("userFileId", userFileId);

                List<UserFile> userfileResult = userFileMapper.selectByMap(param);
                log.info("更新到这, size:"+ userfileResult.size() );
                if (!userfileResult.isEmpty()) {
                    log.info("es更新完毕");
                    FileSearch fileSearch = new FileSearch();
                    BeanUtil.copyProperties(userfileResult.get(0), fileSearch);
                /*if (fileSearch.getIsDir() == 0) {

                    Reader reader = ufopFactory.getReader(fileSearch.getStorageType());
                    ReadFile readFile = new ReadFile();
                    readFile.setFileUrl(fileSearch.getFileUrl());
                    String content = reader.read(readFile);
                    //全文搜索
                    fileSearch.setContent(content);

                }*/
                    // 写入数据到filesearch索引中， 写入id为getUserFileId， 文档为fileSearch
                    elasticsearchClient.index(i -> i.index("filesearch").id(fileSearch.getUserFileId()).document(fileSearch));
                    log.info("es更新完毕-2");
                }
                log.info("更新到这-2");
            } catch (Exception e) {
                log.debug("ES更新操作失败，请检查配置");
            }
        });

    }

}
