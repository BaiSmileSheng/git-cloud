package com.cloud.system.controller;

import com.cloud.common.auth.annotation.HasPermissions;
import com.cloud.common.core.controller.BaseController;
import com.cloud.common.core.domain.R;
import com.cloud.common.core.page.TableDataInfo;
import com.cloud.common.easyexcel.EasyExcelUtil;
import com.cloud.common.easyexcel.SheetExcelData;
import com.cloud.common.log.annotation.OperLog;
import com.cloud.common.log.enums.BusinessType;
import com.cloud.system.domain.entity.SysDept;
import com.cloud.system.domain.entity.SysDictType;
import com.cloud.system.domain.entity.SysOperLog;
import com.cloud.system.domain.entity.SysUser;
import com.cloud.system.mail.MailService;
import com.cloud.system.service.ISysDeptService;
import com.cloud.system.service.ISysDictTypeService;
import com.cloud.system.service.ISysOperLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志记录 提供者  Swagger注解示例。。
 *
 * @author zmr
 * @date 2019-05-20
 */
@RestController
@RequestMapping("testSwagger")
@Api(tags = "测试")
public class SysTestLogController extends BaseController {
    @Autowired
    private ISysOperLogService sysOperLogService;
    @Autowired
    private ISysDeptService sysDeptService;
    @Autowired
    private ISysDictTypeService sysDictTypeService;
    @Autowired
    private MailService mailService;


    /**
     * 测试导出一个sheet页
     * @param operLog
     * @return
     */
    @OperLog(title = "操作日志", businessType = BusinessType.EXPORT)
    @HasPermissions("monitor:operlog:export")
    @PostMapping("/export")
    @ApiOperation(value = "测试导出一个sheet页", response = SysDictType.class)
    public R export(SysOperLog operLog) {
        List<SysOperLog> list = sysOperLogService.selectOperLogList(operLog);
         return EasyExcelUtil.writeExcel(list,"操作日志.xlsx","sheet",new SysOperLog());
    }

    /**
     * 测试导出多Sheet
     * @param operLog
     * @return
     */
    @OperLog(title = "操作日志", businessType = BusinessType.EXPORT)
    @HasPermissions("monitor:operlog:export")
    @PostMapping("/exportMulSheet")
    @ApiOperation(value = "测试导出多Sheet", response = SysDictType.class)
    public R exportMulSheet(SysOperLog operLog) {
        List<SysUser> listUser = new ArrayList<>();
        SysUser sysUser = new SysUser();
        sysUser.setUserId(11111111L);
        sysUser.setUserName("cccccsssss");
        sysUser.setSex("1");
        listUser.add(sysUser);
        SheetExcelData<SysUser> userPOSheetExcelData = new SheetExcelData<>();
        userPOSheetExcelData.setSheetName("用户记录");
        userPOSheetExcelData.setTClass(SysUser.class);
        userPOSheetExcelData.setDataList(listUser);

        List<SysOperLog> list = sysOperLogService.selectOperLogList(operLog);
        SheetExcelData<SysOperLog> logPOSheetExcelData = new SheetExcelData<>();
        logPOSheetExcelData.setSheetName("操作记录");
        logPOSheetExcelData.setTClass(SysOperLog.class);
        logPOSheetExcelData.setDataList(list);


        List<SheetExcelData> sheetExcelDataList = new ArrayList<>(2);
        sheetExcelDataList.add(logPOSheetExcelData);
        sheetExcelDataList.add(userPOSheetExcelData);
        return EasyExcelUtil.writeMultiExcel("操作日志.xlsx",sheetExcelDataList);
    }

    /**
     * 单sheet文件导入
     * @param file
     * @return
     */
    @PostMapping("/importSingle")
    @ResponseBody
    @ApiOperation(value = "单sheet文件导入")
    public R singleImport(MultipartFile file){
        List<SysOperLog> userPOList = (List<SysOperLog>) EasyExcelUtil.readSingleExcel(file,new SysOperLog(),1);
        return R.ok("success");
    }

    /**
     * 多sheet文件导入
     * @param file
     * @return
     */
    @PostMapping("/importMul")
    @ResponseBody
    @ApiOperation(value = "多sheet文件导入")
    public R mulImport(MultipartFile file) {
        List<SysOperLog> userPOList1 = (List<SysOperLog>) EasyExcelUtil.readMulExcel(file,new SysOperLog());
        return R.ok("success");
    }

    /**
     * 查询部门列表
     */
    @GetMapping("deptList")
    @ApiOperation(value = "测试部门分页", notes = "欢迎",response = SysDept.class)
    public TableDataInfo list(SysDept sysDept) {
        startPage();
        List<SysDept> sysDepts = sysDeptService.selectDeptList(sysDept);
        return getDataTable(sysDepts);
    }

    /**
     * 查询字典类型列表
     */
    @GetMapping("dictTypeList")
    @HasPermissions("system:dict:list")
    @ApiOperation(value = "测试字段类型分页", notes = "欢迎来到德莱联盟",response = SysDictType.class)
    public R list(SysDictType sysDictType) {
        startPage();
        return result(sysDictTypeService.selectDictTypeList(sysDictType));
    }

    /**
     * 测试发送文本邮件
     * @return
     */
    @GetMapping("sendMailText")
    @ApiOperation(value = "测试发送文本邮件")
    public R sendMailText() {
        try {
            mailService.sendTextMail("721666450@qq.com", "发送文本邮件", "hello，这是Spring Boot发送的一封文本邮件!");
        } catch (Exception e) {
            return R.error("发送邮件失败");
        }
        return R.ok();
    }

    /**
     * 测试发送html邮件
     * @return
     */
    @GetMapping("sendMailHtml")
    @ApiOperation(value = "测试发送html邮件", response = SysDictType.class)
    public R sendMailHtml() {
        try {
            String content = "<html>" +
                    "<body>" +
                    "<h1 style=\"" + "color:red;" + "\">hello，这是Spring Boot发送的一封HTML邮件</h1>" +
                    "</body></html>";
            mailService.sendHtmlMail("721666450@qq.com", "发送HTML邮件", content);
        } catch (Exception e) {
            return R.error("发送邮件失败");
        }
        return R.ok();
    }

    /**
     * 测试发送带附件邮件
     * @return
     */
    @GetMapping("sendMailFile")
    @ApiOperation(value = "测试发送File邮件", response = SysDictType.class)
    public R sendMailFile() {
        try {
            String[] filePathList = new String[1];
            filePathList[0] = "README.md";
            mailService.sendAttachmentMail("721666450@qq.com", "发送附件邮件", "hello，这是Spring Boot发送的一封附件邮件!", filePathList);
        } catch (Exception e) {
            return R.error("发送邮件失败");
        }
        return R.ok();
    }
}
