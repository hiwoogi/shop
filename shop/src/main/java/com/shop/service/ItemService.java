package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private  final ItemImgService itemImgService;

    //컨트롤러에서 itemFormDto와 itemImgFileList를 받는다.
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        Item item = itemFormDto.createItem(); //dto를 mapper써서 item으로 바로 바꿔버림.
        itemRepository.save(item);

        for(int i=0; i<itemImgFileList.size(); i++)
        {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if(i ==0) {
                itemImg.setRepimgYn("Y");
            }
            else
                itemImg.setRepimgYn("N");
            itemImgService.saveItemImg(itemImg,itemImgFileList.get(i));
        }
        return item.getId();
    }

}
