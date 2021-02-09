package cloud.chenh.emiew.api;

import cloud.chenh.emiew.crawl.DownloadManager;
import cloud.chenh.emiew.data.service.DownloadService;
import cloud.chenh.emiew.exception.IpBannedException;
import cloud.chenh.emiew.model.OperationResult;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("download")
public class DownloadApi {

    @Autowired
    private DownloadManager downloadManager;

    @Autowired
    private DownloadService downloadService;

    @PostMapping
    public OperationResult<?> add(@RequestParam String url) {
        try {
            downloadManager.add(url);
            return OperationResult.ok("已加入下载队列");
        } catch (IpBannedException e) {
            return OperationResult.no("本IP已被暂时封禁");
        } catch (IOException e) {
            e.printStackTrace();
            return OperationResult.no("网络请求失败");
        }
    }

    @GetMapping
    public OperationResult<?> get() {
        return OperationResult.ok(downloadService.findAll());
    }

    @PostMapping("pause")
    public OperationResult<?> pause(@RequestParam(required = false) List<Long> ids) {
        if (ids == null || ids.size() == 0) {
            downloadManager.pauseAll();
        } else {
            downloadManager.pauseById(ids);
        }
        return OperationResult.ok("已暂停");
    }

    @PostMapping("resume")
    public OperationResult<?> resume(@RequestParam(required = false) List<Long> ids) {
        if (ids == null || ids.size() == 0) {
            downloadManager.resumeAll();
        } else {
            downloadManager.resumeById(ids);
        }
        return OperationResult.ok("已恢复");
    }

    @PostMapping("remove")
    public OperationResult<?> remove(@RequestParam(required = false) List<Long> ids) {
        downloadManager.remove(ids);
        return OperationResult.ok("已删除");
    }

}
