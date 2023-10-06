package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.codehaus.groovy.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final ItemImgService itemImgService;

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
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList)throws Exception{
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        for(int i =0; i<itemImgFileList.size(); i++){
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }
        return item.getId();
    }

    //아이템 id를 받아 존재하는 이미지가 있는지 조회한다.
    //이미지 리스트를 추가하고 dto에 추가함.
    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId){

        List<ItemImg> itemImgList =
                itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for( ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);

        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);



        return itemFormDto;

    }



}
