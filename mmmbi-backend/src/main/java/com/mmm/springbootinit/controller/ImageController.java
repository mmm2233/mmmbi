package com.mmm.springbootinit.controller;

import cn.hutool.core.io.FileUtil;
import com.mmm.springbootinit.common.BaseResponse;
import com.mmm.springbootinit.common.ErrorCode;
import com.mmm.springbootinit.common.ResultUtils;
import com.mmm.springbootinit.exception.BusinessException;
import com.mmm.springbootinit.model.dto.image.AddImageRequest;
import com.mmm.springbootinit.model.entity.Image;
import com.mmm.springbootinit.model.entity.User;
import com.mmm.springbootinit.model.enums.FileUploadBizEnum;
import com.mmm.springbootinit.service.ImageService;
import com.mmm.springbootinit.service.UserService;
import com.mmm.springbootinit.utils.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;

/**
 * @Author：mmm
 * @Package：com.mmm.springbootinit.controller
 * @Project：mmmbi-backend
 * @name：ImageController
 * @Date：2024/3/10 20:09
 * @Filename：ImageController
 */
@RestController
@RequestMapping("/image")
@Slf4j
public class ImageController {
    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private ImageService imageService;

    /**
     * 文件上传,并返回ai解析结果
     *
     * @param file
     * @param uploadImageRequest
     * @param request
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadImageAnalysis(@RequestPart("file") MultipartFile file,
                                                    AddImageRequest uploadImageRequest, HttpServletRequest request) {
        String biz = "user_avatar";  // 业务类型，例如用户头像
        String goal = uploadImageRequest.getGoal();  // 上传图片的目标
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);  // 根据业务类型获取枚举值
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);  // 如果业务类型不在预定义的枚举值中，抛出参数错误的异常
        }
        String fileSuffix = validFile(file, fileUploadBizEnum);  // 校验文件是否符合要求，返回文件后缀名
        User loginUser = userService.getLoginUser(request);  // 获取登录用户信息
// 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);  // 随机生成一个8位长的字符串作为文件名的一部分
        String filename = uuid + "-" + file.getOriginalFilename();  // 使用随机字符串拼接原始文件名作为最终的文件名
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);  // 根据业务类型、用户ID和文件名构建文件路径
        File newFile = null;  // 新建一个File对象用于保存上传的文件
        try {
            // 上传文件
            newFile = File.createTempFile(filepath, null);  // 在系统默认的临时目录中创建一个临时文件，文件名为文件路径，文件后缀为null

            file.transferTo(newFile);  // 将上传的文件保存到新建的临时文件中
            String imageRes = ImageUtil.convertImageToBase64(newFile.getPath());

//            Image image = new Image();  // 创建一个Image对象用于保存相关图片信息
//            image.setGoal(goal);  // 设置图片的目标
//            image.setImageType(fileSuffix);  // 设置图片的类型，即文件后缀名
//            image.setBaseString("");  // 设置图片的Base64编码字符串，此处为空字符串
//            boolean save = imageService.save(image);
//            if (!save){
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"新增图片信息失败");
//            }
//            AiImageUtils aiImageUtils = new AiImageUtils(redissonClient);
//            String ans = aiImageUtils.getAns(newFile, goal,image.getId());
//            image.setGenResult(ans);
//            image.setState("succeed");
//            boolean update = imageService.updateById(image);
//            if (!update){
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新图片信息失败");
//            }
            // 返回可访问地址
            return ResultUtils.success("");
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = newFile.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 校验文件
     *
     * @param multipartFile
     * @param fileUploadBizEnum 业务类型
     */
    private String validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 1024 * 1024L;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }
        return fileSuffix;
    }
}
