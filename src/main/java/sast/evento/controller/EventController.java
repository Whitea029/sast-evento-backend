package sast.evento.controller;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import sast.evento.annotation.DefaultActionState;
import sast.evento.annotation.EventId;
import sast.evento.annotation.OperateLog;
import sast.evento.common.enums.ActionState;
import sast.evento.common.enums.ErrorEnum;
import sast.evento.entitiy.Event;
import sast.evento.exception.LocalRunTimeException;
import sast.evento.interceptor.HttpInterceptor;
import sast.evento.model.EventModel;
import sast.evento.model.UserProFile;
import sast.evento.service.EventService;
import sast.evento.service.PermissionService;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/event")
public class EventController {

    @Resource
    private EventService eventService;

    @Resource
    private PermissionService permissionService;

    /* 由后端生成部分信息置于二维码，userId需要前端填充 */
    @OperateLog("签到")
    @DefaultActionState(ActionState.LOGIN)/* 这里为public,eventId注解没什么用 */
    @GetMapping("/checkIn")
    public String CheckIn(@RequestParam @EventId Integer eventId,
                          @RequestParam String userId,
                          @RequestParam String code) {
        return null;
    }

    @OperateLog("获取活动签到二维码")
    @DefaultActionState(ActionState.ADMIN)/* 这里为admin,eventId注解没什么用 */
    @GetMapping("/qrcode")
    public BufferedImage eventQrcodeGet(@RequestParam @EventId Integer eventId) {
        return null;
    }

    /**
     */
    @OperateLog("查看所有正在进行的活动列表")
    @DefaultActionState(ActionState.PUBLIC)
    @GetMapping("/conducting")
    public List<EventModel> getConducting() {
        return eventService.getConducting();
    }

    /**
     */
    @OperateLog("查看最新活动列表（按开始时间正序排列未开始的活动）")
    @DefaultActionState(ActionState.PUBLIC)
    @GetMapping("/newest")
    public List<EventModel> getNewest() {
        return eventService.getNewest();
    }

    /**
     */
    @OperateLog("查看用户历史活动列表（参加过已结束）")
    @DefaultActionState(ActionState.LOGIN)
    @GetMapping("/history")
    public List<EventModel> getHistory() {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        if (userProFile == null) {
            return null;
        }
        String userIdStr = userProFile.getUserId();
        Integer userIdInt = Integer.valueOf(userIdStr);
        return eventService.getHistory(userIdInt);
    }

    @OperateLog("删除活动")
    @DefaultActionState(ActionState.MANAGER)
    @DeleteMapping("/info")
    public String deleteEvent(@RequestParam @EventId Integer eventId) {
        return null;
    }

    /**
     */
    @OperateLog("获取活动详情")
    @DefaultActionState(ActionState.PUBLIC)/* 这里为public,eventId注解没什么用 */
    @GetMapping("/info")
    public EventModel getEvent(@RequestParam @EventId Integer eventId) {
        return eventService.getEvent(eventId);
    }

    @OperateLog("取消活动（部分修改活动信息）")
    @DefaultActionState(ActionState.MANAGER)
    @PatchMapping("/info")
    public String patchEvent(@RequestParam @EventId Integer eventId,
                             @RequestBody Event event) {
        if (!event.getId().equals(eventId)) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid id.");
        return null;
    }

    @OperateLog("发起活动（添加活动）")
    @DefaultActionState(ActionState.ADMIN)
    @PostMapping("/info")
    public String addEvent(@RequestBody Event event) {
        if (event.getId() != null) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "id should be null.");
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        /* 记得给自己加活动权限鸭喵 */
        /* 检测内容不为null的部分添加 */
        /* 留空不予添加 */
        if (
                (event.getTitle() == null) ||
                (event.getGmtEventStart() == null) ||
                (event.getGmtEventEnd() == null) ||
                (event.getGmtRegistrationStart() == null) ||
                (event.getGmtRegistrationEnd() == null)) {
            /* 检测必需参数是否存在 */
            throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "id should be null.");
        }
        Integer eventId = eventService.addEvent(event);
        // TODO permissionService.addManager 参数修改
        permissionService.addManager(eventId, null, userProFile.getUserId(), null);
        return "success";
    }

    @OperateLog("修改活动")
    @DefaultActionState(ActionState.MANAGER)
    @PutMapping("/info")
    public String putEvent(@RequestParam @EventId Integer eventId,
                           @RequestBody Event event) {
        if (!event.getId().equals(eventId)) throw new LocalRunTimeException(ErrorEnum.PARAM_ERROR, "invalid id.");
        return null;
    }

    /**
     */
    @OperateLog("获取活动列表")
    @DefaultActionState(ActionState.PUBLIC)
    @GetMapping("/list")
    public List<EventModel> getEvents(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                      @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        return eventService.getEvents(page, size);
    }

    @OperateLog("获取活动列表(筛选)")
    @DefaultActionState(ActionState.PUBLIC)
    @PostMapping("/list")
    public List<EventModel> postForEvents(@RequestParam(required = false) Boolean filterByUser,
                                          @RequestParam(required = false) List<Integer> typeId,
                                          @RequestParam(required = false) List<Integer> departmentId,
                                          @RequestParam(required = false) Date time) {
        UserProFile userProFile = HttpInterceptor.userProFileHolder.get();
        return null;
    }

}
