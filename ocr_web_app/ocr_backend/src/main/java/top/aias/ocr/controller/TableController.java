package top.aias.ocr.controller;

import ai.djl.modality.cv.Image;
import ai.djl.opencv.OpenCVImageFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import top.aias.ocr.bean.ResultBean;
import top.aias.ocr.configuration.FileProperties;
import top.aias.ocr.service.TableInferService;
import top.aias.ocr.utils.ConvertHtml2Excel;
import top.aias.ocr.utils.UUIDUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表格识别
 *
 * @author Calvin
 * @mail 179209347@qq.com
 * @website www.aias.top
 */
@Api(tags = "表格文字识别 - Table Text Recognition")
@RestController
@RequestMapping("/table")
public class TableController {
    private Logger logger = LoggerFactory.getLogger(TableController.class);

    @Autowired
    private TableInferService tableInferService;

    @Value("${server.baseUri}")
    private String baseUri;

    /**
     * file configuration
     */
    @Autowired
    private FileProperties properties;

    @ApiOperation(value = "单表格文字识别-URL - Single Table Text Recognition - URL")
    @GetMapping(value = "/tableInfoForImageUrl")
    public ResultBean tableInfoForImageUrl(@RequestParam(value = "url") String url) {
        try {
            Image image = OpenCVImageFactory.getInstance().fromUrl(url);
            String tableHtml = tableInferService.getTableHtml(image);
            // 创建一个Excel文件
            // Create an Excel file
            tableHtml = tableHtml.replace("<html><body>", "");
            tableHtml = tableHtml.replace("</body></html>", "");
            HSSFWorkbook workbook = ConvertHtml2Excel.table2Excel(tableHtml);

            FileProperties.ElPath path = properties.getPath();
            String fileRelativePath = path.getPath().replace("\\", "/") + "tables/";
            //Check & create file path
            Path filePath = Paths.get(fileRelativePath);
            File file = filePath.toFile();
            if (!file.exists() && !file.isDirectory()) {
                file.mkdir();
            }
            String fileId = UUIDUtils.getUUID();
            workbook.write(new File(fileRelativePath + fileId + ".xls"));

            try (OutputStreamWriter out = new OutputStreamWriter(
                    new FileOutputStream(fileRelativePath + fileId + ".html"), "UTF-8")) {
                out.write(tableHtml);
            }

            String excelUri = baseUri + File.separator + fileRelativePath + fileId + ".xls";
            String htmlUri = baseUri + File.separator + fileRelativePath + fileId + ".html";

            Map<String, String> map = new ConcurrentHashMap<>();
            map.put("excelUri", excelUri);
            map.put("htmlUri", htmlUri);
            map.put("html", tableHtml);
            return ResultBean.success().add("result", map);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ResultBean.failure().add("message", e.getMessage());
        }
    }

    @ApiOperation(value = "单表格文字识别-图片 -Single Table Text Recognition - Image")
    @PostMapping("/tableInfoForImageFile")
    public ResultBean tableInfoForImageFile(@RequestParam(value = "imageFile") MultipartFile imageFile) {
        try (InputStream inputStream = imageFile.getInputStream()) {
            String base64Img = Base64.encodeBase64String(imageFile.getBytes());
            Image image = OpenCVImageFactory.getInstance().fromInputStream(inputStream);
            String tableHtml = tableInferService.getTableHtml(image);
            // 创建一个Excel文件
            // Create an Excel file
            tableHtml = tableHtml.replace("<html><body>", "");
            tableHtml = tableHtml.replace("</body></html>", "");
            HSSFWorkbook workbook = ConvertHtml2Excel.table2Excel(tableHtml);

            FileProperties.ElPath path = properties.getPath();
            String fileRelativePath = path.getPath().replace("\\", "/") + "tables/";
            //Check & create file path
            Path filePath = Paths.get(fileRelativePath);
            File file = filePath.toFile();
            if (!file.exists() && !file.isDirectory()) {
                file.mkdir();
            }

            String fileId = UUIDUtils.getUUID();
            workbook.write(new File(fileRelativePath + fileId + ".xls"));

            try (OutputStreamWriter out = new OutputStreamWriter(
                    new FileOutputStream(fileRelativePath + fileId + ".html"), "UTF-8")) {
                out.write(tableHtml);
            }

            String excelUri = baseUri + File.separator + fileRelativePath + fileId + ".xls";
            String htmlUri = baseUri + File.separator + fileRelativePath + fileId + ".html";

            Map<String, String> map = new ConcurrentHashMap<>();
            map.put("excelUri", excelUri);
            map.put("htmlUri", htmlUri);
            map.put("html", tableHtml);

            return ResultBean.success().add("result", map)
                    .add("base64Img", "data:imageName/jpeg;base64," + base64Img);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ResultBean.failure().add("message", e.getMessage());
        }
    }

    @ApiOperation(value = "表单表格自动检测文字识别-URL -Auto Table Text Detection and Recognition - URL")
    @GetMapping(value = "/autoTableInfoForImageUrl")
    public ResultBean autoTableInfoForImageUrl(@RequestParam(value = "url") String url) {
        try {
            Image image = OpenCVImageFactory.getInstance().fromUrl(url);
            List<String> tableHtmlList = tableInferService.getTableHtmlList(image);
            List<HSSFWorkbook> workbookList = new ArrayList<>();

            for (String tableHtml : tableHtmlList) {
                tableHtml = tableHtml.replace("<html><body>", "");
                tableHtml = tableHtml.replace("</body></html>", "");
                // Create workbook for each table
                HSSFWorkbook workbook = ConvertHtml2Excel.table2Excel(tableHtml);
                workbookList.add(workbook);
            }

            FileProperties.ElPath path = properties.getPath();
            String fileRelativePath = path.getPath().replace("\\", "/") + "tables/";
            //Check & create file path
            Path filePath = Paths.get(fileRelativePath);
            File file = filePath.toFile();
            if (!file.exists() && !file.isDirectory()) {
                file.mkdir();
            }

            List<Map<String, String>> uriList = new ArrayList<>();
            for (int i = 0; i < tableHtmlList.size(); i++) {
                String fileId = UUIDUtils.getUUID();
                HSSFWorkbook workbook = workbookList.get(i);
                workbook.write(new File(fileRelativePath + fileId + ".xls"));
                String tableHtml = tableHtmlList.get(i);
                try (OutputStreamWriter out = new OutputStreamWriter(
                        new FileOutputStream(fileRelativePath + fileId + ".html"), "UTF-8")) {
                    out.write(tableHtml);
                }
                String excelUri = baseUri + File.separator + fileRelativePath + fileId + ".xls";
                String htmlUri = baseUri + File.separator + fileRelativePath + fileId + ".html";
                Map<String, String> map = new ConcurrentHashMap<>();
                map.put("excelUri", excelUri);
                map.put("htmlUri", htmlUri);
                map.put("html", tableHtml);
                uriList.add(map);
            }

            return ResultBean.success().add("result", uriList);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ResultBean.failure().add("message", e.getMessage());
        }
    }

    @ApiOperation(value = "表单表格自动检测文字识别-URL -Auto Table Text Detection and Recognition - Image")
    @PostMapping("/autoTableInfoForImageFile")
    public ResultBean autoTableInfoForImageFile(@RequestParam(value = "imageFile") MultipartFile imageFile) {
        try (InputStream inputStream = imageFile.getInputStream()) {
            String base64Img = Base64.encodeBase64String(imageFile.getBytes());
            Image image = OpenCVImageFactory.getInstance().fromInputStream(inputStream);

            List<String> tableHtmlList = tableInferService.getTableHtmlList(image);
            List<HSSFWorkbook> workbookList = new ArrayList<>();

            for (String tableHtml : tableHtmlList) {
                tableHtml = tableHtml.replace("<html><body>", "");
                tableHtml = tableHtml.replace("</body></html>", "");
                // Create workbook for each table
                HSSFWorkbook workbook = ConvertHtml2Excel.table2Excel(tableHtml);
                workbookList.add(workbook);
            }

            FileProperties.ElPath path = properties.getPath();
            String fileRelativePath = path.getPath().replace("\\", "/") + "tables/";
            //Check & create file path
            Path filePath = Paths.get(fileRelativePath);
            File file = filePath.toFile();
            if (!file.exists() && !file.isDirectory()) {
                file.mkdir();
            }

            List<Map<String, String>> uriList = new ArrayList<>();
            for (int i = 0; i < tableHtmlList.size(); i++) {
                String fileId = UUIDUtils.getUUID();
                HSSFWorkbook workbook = workbookList.get(i);
                workbook.write(new File(fileRelativePath + fileId + ".xls"));
                String tableHtml = tableHtmlList.get(i);


                try (OutputStreamWriter out = new OutputStreamWriter(
                        new FileOutputStream(fileRelativePath + fileId + ".html"), "UTF-8")) {
                    out.write(tableHtml);
                }
                String excelUri = baseUri + File.separator + fileRelativePath + fileId + ".xls";
                String htmlUri = baseUri + File.separator + fileRelativePath + fileId + ".html";
                Map<String, String> map = new ConcurrentHashMap<>();
                map.put("excelUri", excelUri);
                map.put("htmlUri", htmlUri);
                map.put("html", tableHtml);
                uriList.add(map);
            }

            return ResultBean.success().add("result", uriList).add("base64Img", "data:imageName/jpeg;base64," + base64Img);
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return ResultBean.failure().add("message", e.getMessage());
        }
    }
}
