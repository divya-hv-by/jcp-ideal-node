package com.jcp.commit.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * @author Minesh
 */
@Service
public interface FileService {

    public boolean saveFile(String fileName, byte[] data);
    public List<String> readFile(Path path);
}
