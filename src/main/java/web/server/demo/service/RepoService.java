package web.server.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import web.server.demo.repository.FileData;
import web.server.demo.repository.FileDataRepository;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class RepoService {

    private static final Logger LOG = LoggerFactory.getLogger(RepoService.class);

    private FileDataRepository fileDataRepository;

    @Autowired
    public RepoService(FileDataRepository fileDataRepository) {
        this.fileDataRepository = fileDataRepository;
    }

    @Async
    public Boolean save(MultipartFile file) throws IOException {
        FileData fileData = new FileData(file.getOriginalFilename(), file.getBytes());
        fileDataRepository.save(fileData);
        LOG.info("File {} saved successfully", fileData.getName());
        return true;
    }

    public Boolean save(MultipartFile[] files) throws InterruptedException {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(files.length);
        List<Callable<MultipartFile>> callableList = Arrays.stream(files)
                .map(file -> (Callable<MultipartFile>) () -> {
                    FileData fileData = new FileData(file.getOriginalFilename(), file.getBytes());
                    fileDataRepository.save(fileData);
                    LOG.info("File {} saved successfully", fileData.getName());
                    return file;
                })
                .collect(Collectors.toList());
        fixedThreadPool.invokeAll(callableList);
        return true;
    }

    @Async
    public Future<FileData> getById(long id) {
        LOG.info("Sending file with {} id", id);
        return new AsyncResult<>(fileDataRepository.findById(id).orElseThrow(NoSuchElementException::new));
    }

    @Async
    public Future<Collection<String>> getByNameStartingWith(String prefix) {
        List<String> fileNames = new ArrayList<>();
        fileDataRepository.findByNameStartingWith(prefix).forEach(fileData -> fileNames.add(fileData.getName()));
        LOG.info("Sending files names starting with {}", prefix);
        return new AsyncResult<>(fileNames);
    }

    @Async
    public Future<Collection<String>> getAll() {
        List<String> allFileNames = new ArrayList<>();
        fileDataRepository.findAll().forEach(fileData -> allFileNames.add(fileData.getName()));
        LOG.info("Sending all file names");
        return new AsyncResult<>(allFileNames);
    }

    @Async
    public Boolean rename(long id, String name) {
        Optional<FileData> fileDataOptional = fileDataRepository.findById(id);
        if (fileDataOptional.isPresent()) {
            FileData fileData = fileDataOptional.get();
            fileData.setName(name);
            fileDataRepository.save(fileData);
            LOG.info("File was renamed successfully to {}", name);
            return true;
        } else {
            LOG.warn("File {} was not found", name);
            throw new NoSuchElementException();
        }
    }

    @Async
    public Boolean replace(long id, MultipartFile file) throws IOException {
        FileData fileData = new FileData(file.getOriginalFilename(), file.getBytes());
        fileData.setId(id);
        fileDataRepository.save(fileData);
        LOG.info("File {} replaced successfully", fileData.getName());
        return true;
    }

    @Async
    public Boolean deleteById(long id) {
        fileDataRepository.deleteById(id);
        LOG.info("File with id {} was removed", id);
        return true;
    }

    @Async
    public Boolean deleteAllByIds(long[] ids) {
        Iterable<FileData> allById = fileDataRepository.findAllById(() -> Arrays.stream(ids).boxed().iterator());
        fileDataRepository.deleteAll(allById);
        LOG.info("Files with ids {} was removed", Arrays.toString(ids));
        return true;
    }
}
