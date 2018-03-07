package com.linghua.hdds.api.resource;

import com.linghua.hdds.api.conf.ItemRecorder;
import com.linghua.hdds.api.service.BehaviorService;
import com.linghua.hdds.api.service.ItemService;
import com.linghua.hdds.api.service.UserService;
import com.linghua.hdds.common.TableUtil;
import com.linghua.hdds.api.conf.TemporaryRecorder;
import com.linghua.hdds.meta.ActionMeta;
import com.linghua.hdds.meta.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.BadRequestException;

@RestController
@RequestMapping("/bvlg")
public class BehaviorController {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BehaviorService bhvService;

    @Autowired
    private ItemRecorder itemRecorder;
    @Autowired
    private TemporaryRecorder temporaryRecorder;

    @RequestMapping("/show/{biz}/{uid}/{iid}")
    public boolean hasDone(@PathVariable(value="biz") String biz,@PathVariable(value="uid") String uid,@PathVariable(value="iid") String iid,@PathVariable(value="type")int type){

//        bhvService.hasExist
        String id = TableUtil.idReverseAndBuildWithoutTime(uid,iid,"_");

//        return bhvService.exist(biz,id, ActionType.COLLECT.getName());
        return false;

    }

    @RequestMapping("/bhv/clt/{biz}/{uid}/{iid}/{type}")
    public boolean collect(@PathVariable(value="biz") String biz, @PathVariable(value="uid") String uid, @PathVariable(value="iid") String iid, @PathVariable(value="type")ActionType type){
        biz="headlines";
        if(uid ==null || uid.equals("undefined") || iid == null||type ==null){
            return false;
        }
        String user_act_item =TableUtil.idReverseAndBuild(uid,type.getName(),iid);

        if(type ==null)
            return false;
        switch (type){
            case COLLECT:
            case DISCOLLECT:{
                itemRecorder.add(TableUtil.IdReverse(iid),type);
                if(type.getIndex() == -1){
                    bhvService.delete(biz,new String[]{user_act_item});
                }else {
                    bhvService.put(biz,user_act_item,ActionMeta.DEFAULT_COL,1l);
                }
                break;
            }
            case DISLIKE:
            case LIKE:
            case SHARE:
            case FOCUS:
            case VIEW:{itemRecorder.add(TableUtil.IdReverse(iid),type);temporaryRecorder.add(user_act_item,type);break;}
            default: throw new BadRequestException("未知日志类型"+type);
        }
        return true;
    }
}
