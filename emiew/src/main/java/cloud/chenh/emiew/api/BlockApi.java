package cloud.chenh.emiew.api;

import cloud.chenh.emiew.data.service.BlockService;
import cloud.chenh.emiew.model.OperationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("block")
public class BlockApi {

    @Autowired
    private BlockService blockService;

    @PostMapping
    public OperationResult<?> add(@RequestParam String type, @RequestParam String value) {
        blockService.add(type, value);
        return OperationResult.ok("已新增屏蔽内容");
    }

    @GetMapping
    public OperationResult<?> get() {
        return OperationResult.ok(blockService.findAll());
    }

    @DeleteMapping
    public OperationResult<?> delete(@RequestParam List<Long> ids) {
        blockService.deleteById(ids);
        return OperationResult.ok(blockService.findAll());
    }

}