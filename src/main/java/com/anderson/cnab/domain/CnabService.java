package com.anderson.cnab.domain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class CnabService {
    private final Path fileStorageLocation;

    public CnabService(@Value("${file.upload-dir}") String dir) {
        this.fileStorageLocation = Paths.get(dir);
    }

    public void uploadCnabFile(MultipartFile file) throws IOException {
        var fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        var targetLocation =fileStorageLocation.resolve(fileName);
        file.transferTo(targetLocation);
    }
}
