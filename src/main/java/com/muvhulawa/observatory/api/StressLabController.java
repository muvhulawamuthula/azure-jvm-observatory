package com.muvhulawa.observatory.api;

import com.muvhulawa.observatory.service.StressLabService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/load")
public class StressLabController {

    private final StressLabService stressLabService;

    public StressLabController(StressLabService stressLabService) {
        this.stressLabService = stressLabService;
    }

    @PostMapping("/cpu")
    public String cpu(@RequestParam(defaultValue = "10") int seconds) {
        return stressLabService.burnCpu(seconds);
    }

    @PostMapping("/memory-leak")
    public String memoryLeak(@RequestParam(defaultValue = "50") int megabytes) {
        return stressLabService.leakMemory(megabytes);
    }

    @PostMapping("/memory-clear")
    public String memoryClear() {
        return stressLabService.clearMemoryLeak();
    }

    @PostMapping("/virtual-threads")
    public String virtualThreads(@RequestParam(defaultValue = "1000") int count) {
        return stressLabService.createVirtualThreads(count);
    }

    @PostMapping("/platform-threads")
    public String platformThreads(@RequestParam(defaultValue = "100") int count) {
        return stressLabService.blockPlatformThreads(count);
    }
}
