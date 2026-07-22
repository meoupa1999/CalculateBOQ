package com.sonnh.elv.service;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

public interface ExcelExportService {
    ByteArrayInputStream exportProjectExcel(UUID projectId, List<UUID> towerIds);
}
