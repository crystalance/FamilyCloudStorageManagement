package com.example.familycloudstoragemanagement.FileManagement.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HighlighterEncoder;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.familycloudstoragemanagement.FileManagement.Component.AsyncTaskComp;
import com.example.familycloudstoragemanagement.FileManagement.Component.FileDealComp;
import com.example.familycloudstoragemanagement.FileManagement.DTO.*;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.UserFile;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.Beans.FileBean;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.IServices.IFileService;
import com.example.familycloudstoragemanagement.FileManagement.DataAccess.IServices.IUserFileService;
import com.example.familycloudstoragemanagement.FileManagement.Utils.QiwenFileUtil;
import com.example.familycloudstoragemanagement.FileManagement.VO.file.FileListVO;
import com.example.familycloudstoragemanagement.FileManagement.VO.file.SearchFileVO;
import com.example.familycloudstoragemanagement.FileManagement.config.es.FileSearch;
import com.example.familycloudstoragemanagement.FileManagement.io.QiwenFile;
import com.qiwenshare.common.anno.MyLog;
import com.qiwenshare.common.result.RestResult;
import com.qiwenshare.common.util.DateUtil;
import com.qiwenshare.common.util.security.JwtUser;
import com.qiwenshare.common.util.security.SessionUtil;
import com.qiwenshare.ufop.factory.UFOPFactory;
import com.qiwenshare.ufop.operation.copy.Copier;
import com.qiwenshare.ufop.operation.copy.domain.CopyFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Tag(name = "file", description = "该接口为文件接口，主要用来做一些文件的基本操作，如创建目录，删除，移动，复制等。")
@RestController
@Slf4j
@RequestMapping("/file")
public class FileController {
    @Resource
    IFileService fileService;
    @Resource
    IUserFileService userFileService;
    @Resource
    UFOPFactory ufopFactory;
    @Resource
    FileDealComp fileDealComp;
    @Resource
    AsyncTaskComp asyncTaskComp;
    @Autowired
    private ElasticsearchClient elasticsearchClient;
    @Value("${ufop.storage-type}")
    private Integer storageType;

    public static Executor executor = Executors.newFixedThreadPool(20);

    public static final String CURRENT_MODULE = "文件接口";

    @Operation(summary = "创建文件", description = "创建文件", tags = {"file"})
    @ResponseBody
    @RequestMapping(value = "/createFile", method = RequestMethod.POST)
    public RestResult<Object> createFile(@Valid @RequestBody CreateFileDTO createFileDTO) {

        try {

            Long userId = StpUtil.getLoginIdAsLong();
            String filePath = createFileDTO.getFilePath();
            String fileName = createFileDTO.getFileName();
            String extendName = createFileDTO.getExtendName();
            List<UserFile> userFiles = userFileService.selectSameUserFile(fileName, filePath, extendName, userId);
            if (userFiles != null && !userFiles.isEmpty()) {
                return RestResult.fail().message("同名文件已存在");
            }
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");

            String templateFilePath = "";
            if ("docx".equals(extendName)) {
                templateFilePath = "template/Word.docx";
            } else if ("xlsx".equals(extendName)) {
                templateFilePath = "template/Excel.xlsx";
            } else if ("pptx".equals(extendName)) {
                templateFilePath = "template/PowerPoint.pptx";
            } else if ("txt".equals(extendName)) {
                templateFilePath = "template/Text.txt";
            } else if ("drawio".equals(extendName)) {
                templateFilePath = "template/Drawio.drawio";
            }
            String url2 = ClassUtils.getDefaultClassLoader().getResource("static/" + templateFilePath).getPath();
            url2 = URLDecoder.decode(url2, "UTF-8");
            FileInputStream fileInputStream = new FileInputStream(url2);
            Copier copier = ufopFactory.getCopier();
            CopyFile copyFile = new CopyFile();
            copyFile.setExtendName(extendName);
            String fileUrl = copier.copy(fileInputStream, copyFile);

            FileBean fileBean = new FileBean();
            fileBean.setFileId(IdUtil.getSnowflakeNextIdStr());
            fileBean.setFileSize(0L);
            fileBean.setFileUrl(fileUrl);
            fileBean.setStorageType(storageType);
            fileBean.setIdentifier(uuid);
            fileBean.setCreateTime(DateUtil.getCurrentTime());
            fileBean.setCreateUserId(StpUtil.getLoginIdAsLong());
            fileBean.setFileStatus(1);
            boolean saveFlag = fileService.save(fileBean);
            UserFile userFile = new UserFile();
            if (saveFlag) {
                userFile.setUserFileId(IdUtil.getSnowflakeNextIdStr());
                userFile.setUserId(userId);
                userFile.setFileName(fileName);
                userFile.setFilePath(filePath);
                userFile.setDeleteFlag(0);
                userFile.setIsDir(0);
                userFile.setExtendName(extendName);
                userFile.setUploadTime(DateUtil.getCurrentTime());
                userFile.setFileId(fileBean.getFileId());
                userFile.setCreateTime(DateUtil.getCurrentTime());
                userFile.setCreateUserId(StpUtil.getLoginIdAsLong());
                userFileService.save(userFile);
            }
            return RestResult.success().message("文件创建成功");
        } catch (Exception e) {
            log.error(e.getMessage());
            return RestResult.fail().message(e.getMessage());
        }
    }

    @Operation(summary = "文件搜索", description = "文件搜索", tags = {"file"})
    @GetMapping(value = "/search")
    @MyLog(operation = "文件搜索", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<SearchFileVO> searchFile(SearchFileDTO searchFileDTO) {

        int currentPage = (int)searchFileDTO.getCurrentPage() - 1;
        int pageCount = (int)(searchFileDTO.getPageCount() == 0 ? 10 : searchFileDTO.getPageCount());

        SearchResponse<FileSearch> search = null;
        try {
            search = elasticsearchClient.search(s -> s
                            .index("filesearch")
                            .query(_1 -> _1
                                    .bool(_2 -> _2
                                            .must(_3 -> _3
                                                    .bool(_4 -> _4
                                                            .should(_5 -> _5
                                                                    .match(_6 -> _6
                                                                            .field("fileName")
                                                                            .query(searchFileDTO.getFileName())))
                                                            .should(_5 -> _5
                                                                    .wildcard(_6 -> _6
                                                                            .field("fileName")
                                                                            .wildcard("*" + searchFileDTO.getFileName() + "*")))
                                                            .should(_5 -> _5
                                                                    .match(_6 -> _6
                                                                            .field("content")
                                                                            .query(searchFileDTO.getFileName())))
                                                            .should(_5 -> _5
                                                                    .wildcard(_6 -> _6
                                                                            .field("content")
                                                                            .wildcard("*" + searchFileDTO.getFileName() + "*")))
                                                    ))
                                            .must(_3 -> _3
                                                    .term(_4 -> _4
                                                            .field("userId")
                                                            .value(StpUtil.getLoginIdAsLong())))
                                    ))
                            .from(currentPage)
                            .size(pageCount)
                            .highlight(h -> h
                                    .fields("fileName", f -> f.type("plain")
                                            .preTags("<span class='keyword'>").postTags("</span>"))
                                    .encoder(HighlighterEncoder.Html))
                    ,
                    FileSearch.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<SearchFileVO> searchFileVOList = new ArrayList<>();
        for (Hit<FileSearch> hit : search.hits().hits()) { //hit是查询后的结果，即一个链表的fileSearch
            SearchFileVO searchFileVO = new SearchFileVO();
            BeanUtil.copyProperties(hit.source(), searchFileVO);
            searchFileVO.setHighLight(hit.highlight());
            searchFileVOList.add(searchFileVO);
            asyncTaskComp.checkESUserFileId(searchFileVO.getUserFileId()); //查询到的文件是否已经被删除，若是，则在es中删除当前文件
        }
        return RestResult.success().dataList(searchFileVOList, searchFileVOList.size());
    }

    @Operation(summary = "创建文件夹", description = "目录(文件夹)的创建", tags = {"file"})
    @RequestMapping(value = "/createFold", method = RequestMethod.POST)
    @MyLog(operation = "创建文件夹", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> createFold(@Valid @RequestBody CreateFoldDTO createFoldDto) {

        Long userId = StpUtil.getLoginIdAsLong();
        String filePath = createFoldDto.getFilePath();


        boolean isDirExist = fileDealComp.isDirExist(createFoldDto.getFileName(), createFoldDto.getFilePath(), userId);

        if (isDirExist) {
            return RestResult.fail().message("同名文件夹已存在");
        }

        UserFile userFile = QiwenFileUtil.getQiwenDir(userId, filePath, createFoldDto.getFileName());

        userFileService.save(userFile);
        fileDealComp.uploadESByUserFileId(userFile.getUserFileId());
        return RestResult.success();
    }


    @Operation(summary = "获取文件列表", description = "用来做前台列表展示", tags = {"file"})
    @RequestMapping(value = "/getfilelist", method = RequestMethod.GET)
    @ResponseBody
    public RestResult<FileListVO> getFileList(
            @Parameter(description = "文件类型", required = true) String fileType,
            @Parameter(description = "文件路径", required = true) String filePath,
            @Parameter(description = "当前页", required = true) long currentPage,
            @Parameter(description = "页面数量", required = true) long pageCount){
        if ("0".equals(fileType)) {
            IPage<FileListVO> fileList = userFileService.userFileList(null, filePath, currentPage, pageCount);
            return RestResult.success().dataList(fileList.getRecords(), fileList.getTotal());
        } else {
            IPage<FileListVO> fileList = userFileService.getFileByFileType(Integer.valueOf(fileType), currentPage, pageCount,StpUtil.getLoginIdAsLong());
            return RestResult.success().dataList(fileList.getRecords(), fileList.getTotal());
        }
    }


    @Operation(summary = "删除文件", description = "可以删除文件或者目录", tags = {"file"})
    @RequestMapping(value = "/deletefile", method = RequestMethod.POST)
    @MyLog(operation = "删除文件", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult deleteFile(@RequestBody DeleteFileDTO deleteFileDto) {

        userFileService.deleteUserFile(deleteFileDto.getUserFileId(), StpUtil.getLoginIdAsLong());
        fileDealComp.deleteESByUserFileId(deleteFileDto.getUserFileId());

        return RestResult.success();

    }

    @Operation(summary = "文件移动", description = "可以移动文件或者目录", tags = {"file"})
    @RequestMapping(value = "/movefile", method = RequestMethod.POST)
    @MyLog(operation = "文件移动", module = CURRENT_MODULE)
    @ResponseBody
    public RestResult<String> moveFile(@RequestBody MoveFileDTO moveFileDto) {

        Long userId = StpUtil.getLoginIdAsLong();
        UserFile userFile = userFileService.getById(moveFileDto.getUserFileId());
        String oldfilePath = userFile.getFilePath();
        String newfilePath = moveFileDto.getFilePath();
        String fileName = userFile.getFileName();
        String extendName = userFile.getExtendName();
        if (StringUtil.isEmpty(extendName)) {
            QiwenFile qiwenFile = new QiwenFile(oldfilePath, fileName, true);
            if (newfilePath.startsWith(qiwenFile.getPath() + QiwenFile.separator) || newfilePath.equals(qiwenFile.getPath())) {
                return RestResult.fail().message("原路径与目标路径冲突，不能移动");
            }
        }

        userFileService.updateFilepathByUserFileId(moveFileDto.getUserFileId(), newfilePath, userId);

        fileDealComp.deleteRepeatSubDirFile(newfilePath, userId);
        return RestResult.success();

    }






}
