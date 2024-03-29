package com.sibrahim.annoncify.mapper;

import com.sibrahim.annoncify.dto.ImageDto;
import com.sibrahim.annoncify.entity.Image;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class ImageMapper {

    public Image toImage(ImageDto imageDto){
        return Image.builder()
                .imageUrl(imageDto.getImageUrl())
                .build();
    }

    public ImageDto toImageDto(Image image){
        return ImageDto.builder()
                .imageUrl(image.getImageUrl())
                .build();
    }

    public List<ImageDto> toImageDtos(List<Image> images){
        if(images!=null){
            return images
                    .stream()
                    .map(this::toImageDto)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public List<Image> toImages(List<ImageDto> images){
        if(images!=null){
            return images
                    .stream()
                    .map(this::toImage)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
