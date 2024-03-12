package com.mmm.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmm.springbootinit.model.entity.Image;
import com.mmm.springbootinit.service.ImageService;
import com.mmm.springbootinit.mapper.ImageMapper;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image>
    implements ImageService{

}




