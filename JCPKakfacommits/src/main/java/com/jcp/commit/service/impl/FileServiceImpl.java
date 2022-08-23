package com.jcp.commit.service.impl;

import com.jcp.commit.entity.HistoricDataIdealNodeEntity;
import com.jcp.commit.repository.IdealNodeRepository;
import com.jcp.commit.service.FileService;
import com.jcp.commit.util.IdealNodeMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Minesh
 */
@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Value("${ideal.node.historic.data.file.path:}")
    private String directory;

    @Autowired
    private IdealNodeRepository idealNodeRepository;

    @Autowired
    private IdealNodeMapper idealNodeMapper;

    @PostConstruct
    public void init(){
        Path path = getPath("");
        boolean exists = Files.exists(path);
        if (!exists) {
            try {
                Files.createDirectory(path);
                log.info("Creating directory : {}", directory);
            } catch (IOException e) {
                log.error("Error creating file directory : {}" ,directory);
            }
        }
        log.info("directory check is done : {} ", directory);

    }

    @Override
    public boolean saveFile(String fileName, byte[] data) {
        Path path = getPath(fileName);
        boolean exists = Files.exists(path);
        if (exists) {
            try {
                Files.delete(path);
                log.info("deleting existing file : {}", fileName);
            } catch (IOException e) {
                log.error("Failed deleting existing file {}", path.toAbsolutePath());
            }
        }
        try {
            Path write = Files.write(path, data);
            File file = new File(String.valueOf(write));
            if(fileName.endsWith("zip")) {
                List<Path> extract = extract(file);
                long l = saveToDB(extract);
                log.info("Total lines saved in DB {}", l);
            }
            return true;
        } catch (IOException e) {
            log.error("Failed writing file {}" ,path.toAbsolutePath());
        }
        return false;
    }

    @Override
    public List<String> readFile(Path path) {
        log.info("Reading file :{}", path.toAbsolutePath());
        List<String> list = Collections.EMPTY_LIST;
        try {
            list = Files.lines(path)
                    .skip(1)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Exception while reading file  : {} ", e.getMessage());
        }
        return list;
    }

    @Async("JCPThreadPoolBean")
    public long saveToDB(List<Path> path) throws IOException {
        final long startTime = System.currentTimeMillis();
        long count = 0l;
        for (int i = 0; i < path.size(); i++) {
            count += saveToDB(path.get(i));
        }
        log.info("Read file: Time taken : {} ms", System.currentTimeMillis() - startTime);
        return count;
    }

    public long saveToDB(Path path) {
        long c = 0;
        try {
            List<String> parsedFileData = readFile(path);
            HashSet<String> strings = new HashSet<>(parsedFileData);
            log.info("Number of lines from {} : {}",path, parsedFileData.size());
            List<HistoricDataIdealNodeEntity> idealNodeEntityList = new ArrayList<>();
            parsedFileData.forEach(data -> {
                try {
                    HistoricDataIdealNodeEntity historicDataIdealNodeEntity = idealNodeMapper.getIdealNodeEntity(data);
                    idealNodeEntityList.add(historicDataIdealNodeEntity);
                } catch (Exception exception) {
                    log.error("Exception while parsing lines of file : {} ", exception.getStackTrace());
                }
            });

            List<HistoricDataIdealNodeEntity> entities = idealNodeRepository.saveAll(idealNodeEntityList);
            log.info("Stored {} entries in DB", entities.size());
            c = entities.size();
        } catch (Exception e) {
            log.error("Exception while reading file : {} ", e.getLocalizedMessage());
        }
        return c;
    }

    private Path getPath(String fileName) {
        String filePath = directory + (directory.endsWith("/") ? "" : "/") + fileName;
        return Paths.get(filePath);
    }

    private List<Path> extract(File file) throws IOException {
        List<Path> paths = new ArrayList<>();
        try (java.util.zip.ZipFile zipFile = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File entryDestination = new File(directory,  entry.getName());
                paths.add(entryDestination.toPath());
                if (entry.isDirectory()) {
                    entryDestination.mkdirs();
                } else {
                    entryDestination.getParentFile().mkdirs();
                    try (InputStream in = zipFile.getInputStream(entry);
                         OutputStream out = new FileOutputStream(entryDestination)) {
                        IOUtils.copy(in, out);
                    }
                }
                log.info("File UnZiped : {}", entry.getName());
            }
        }
        return paths;
    }
}
