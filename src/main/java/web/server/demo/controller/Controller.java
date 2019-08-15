package web.server.demo.controller;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import web.server.demo.client.DemoSpi;
import web.server.demo.repository.FileData;
import web.server.demo.service.RepoService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static web.server.demo.client.Constants.*;

@RestController
@Api(value="Web server API")
@RequestMapping(BASE_API_PATH)
public class Controller implements DemoSpi<MultipartFile> {

    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private RepoService repoService;

    @PostMapping(ADD_FILE_PATH)
    @ApiOperation("Add a file into the repository")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File added successfully"),
            @ApiResponse(code = 500, message = "File <file> could not be saved")
    })
    public ResponseEntity<String> add(
            @ApiParam(value = "The file to be saved", required = true)
            @RequestParam(FILE_PARAMETER) MultipartFile file) {
        try {
            repoService.save(file);
            return ResponseEntity.ok("File added successfully");
        } catch (IOException e) {
            LOG.error("File {} could not be saved", file.getOriginalFilename());
            String body = "File " + file.getOriginalFilename() + "could not be saved";
            return new ResponseEntity<>(body, INTERNAL_SERVER_ERROR);
        }
    }

    /*
     * This method doesn't work with swagger as it doesn't supports an array of files in a multipart request:
     * https://github.com/swagger-api/swagger-ui/issues/4600
     *
     * For testing purposes please use Postman or cURL:
     * https://learning.getpostman.com/docs/postman/sending_api_requests/requests/#form-data
     *  - KEY: "files" of type File
     *  - VALUE: select multiple files from the dialog
     *
     * curl -X POST "http://localhost:8080/add/files" -F "files=@/path/to/file1.png" -F "files=@/path/to/file2.png"
     */
    @PostMapping(ADD_FILES_PATH)
    @ApiOperation("Add an array of files to the repository")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Files added successfully"),
            @ApiResponse(code = 500, message = "Files could not be saved")
    })
    public ResponseEntity<String> add(
            @ApiParam(value = "The files to be saved", required = true)
            @RequestParam(FILES_PARAMETER) MultipartFile[] files) {
        try {
            repoService.save(files);
            return ResponseEntity.ok("Files added successfully");
        } catch (InterruptedException e) {
            LOG.error("Files could not be saved");
            return new ResponseEntity<>("Files could not be saved", INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(RETRIEVE_BY_ID_PATH)
    @ApiOperation("Retrieve an element from the repository by its id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "File could not be retrieved")
    })
    public ResponseEntity<?> retrieve(
            @ApiParam(value = "The id associated with the file to be retrieved", required = true)
            @RequestParam(ID_PARAMETER) long id) {
        try {
            FileData fileData = repoService.getById(id).get();
            return ResponseEntity
                    .ok()
                    .header(CONTENT_DISPOSITION, "attachment; filename=\"" + fileData.getName() + "\"")
                    .body(new ByteArrayResource(fileData.getData()));
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("File could not be sent. Error: {}", e.getMessage());
            return new ResponseEntity<>("File could not be retrieved", INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(RETRIEVE_BY_PREFIX_PATH)
    @ApiOperation("Retrieve a list of elements names from the repository")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "File names could not be retrieved")
    })
    public ResponseEntity<String> retrieve(
            @ApiParam(value = "The prefix of the files' names to be retrieved", required = true)
            @RequestParam(PREFIX_PARAMETER) String prefix) {
        try {
            return ResponseEntity.ok(repoService.getByNameStartingWith(prefix).get().toString());
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("File names could not be sent. Error: {}", e.getMessage());
            return new ResponseEntity<>("File names could not be retrieved", INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(RETRIEVE_ALL_PATH)
    @ApiOperation("Retrieve a list of all file names from the repository")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "File names could not be retrieved")
    })
    public ResponseEntity<String> retrieve() {
        try {
            return ResponseEntity.ok(repoService.getAll().get().toString());
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("File names could not be sent. Error: {}", e.getMessage());
            return new ResponseEntity<>("File names could not be retrieved", INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(FILE_RENAME_PATH)
    @ApiOperation("Rename a file in the repository")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File updated successfully")
    })
    public ResponseEntity<String> rename(
            @ApiParam(value = "The id associated with the file to be renamed", required = true)
            @RequestParam(ID_PARAMETER) long id,
            @ApiParam(value = "The new name of the file", required = true)
            @RequestParam(NAME_PARAMETER) String name) {
        repoService.rename(id, name);
        return ResponseEntity.ok("File updated successfully");
    }

    @PostMapping(FILE_REPLACE_PATH)
    @ApiOperation("Replace a file in the repository")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File replaced successfully"),
            @ApiResponse(code = 500, message = "File could not be replaced")
    })
    public ResponseEntity<String> replace(
            @ApiParam(value = "The id associated with the file to be replaced", required = true)
            @RequestParam(ID_PARAMETER) long id,
            @ApiParam(value = "The new file associated with the id", required = true)
            @RequestParam(FILE_PARAMETER) MultipartFile file) {
        try {
            repoService.replace(id, file);
            return ResponseEntity.ok("File replaced successfully");
        } catch (IOException e) {
            LOG.error("File {} could not be replaced . Error: {}", file.getOriginalFilename(), e.getMessage());
            String body = "File " + file.getOriginalFilename() + " could not be replaced";
            return new ResponseEntity<>(body, INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(REMOVE_FILE_PATH)
    @ApiOperation("Remove a file from the repository")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "File removed successfully")
    })
    public ResponseEntity<String> remove(
            @ApiParam(value = "The id associated with the file to be removed", required = true)
            @RequestParam(ID_PARAMETER) long id) {
        repoService.deleteById(id);
        return ResponseEntity.ok("File removed successfully");
    }

    @DeleteMapping(REMOVE_FILES_PATH)
    @ApiOperation("Remove a list of files from the repository")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Files removed successfully")
    })
    public ResponseEntity<String> remove(
            @ApiParam(value = "The ids associated with the files to be removed", required = true)
            @RequestParam(IDS_PARAMETER) long[] ids) {
        repoService.deleteAllByIds(ids);
        return ResponseEntity.ok("Files removed successfully");
    }
}