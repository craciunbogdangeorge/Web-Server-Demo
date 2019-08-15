package web.server.demo.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import web.server.demo.controller.Controller;
import web.server.demo.data.TestData;
import web.server.demo.repository.FileData;
import web.server.demo.service.RepoService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import static java.lang.String.valueOf;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static web.server.demo.client.Constants.*;
import static web.server.demo.data.TestData.*;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTest {

    @Mock
    private RepoService repoService;

    @InjectMocks
    private Controller controller;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testControllerMethods() throws Exception {
        testAddFile();
        testAddFiles();
        testRetrieveFileById();
        testRetrieveFileByPrefix();
        testRenameFile();
        testReplaceFile();
        testRemoveFile();
        testRemoveFiles();
    }

    public void testAddFile() throws Exception {
        MockMultipartFile goodFile = dummyMultipartFile();
        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_API_PATH + ADD_FILE_PATH)
                .file(goodFile))
                .andExpect(status().isOk())
                .andExpect(content().string("File added successfully"));

        MockMultipartFile badFile = dummyBadMultipartFile();
        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_API_PATH + ADD_FILE_PATH)
                .file(badFile))
                .andExpect(status().is4xxClientError());
    }

    public void testAddFiles() throws Exception {
        MockMultipartFile goodFile1 = dummyMultipartFile();
        MockMultipartFile goodFile2 = dummyMultipartFile();

        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_API_PATH + ADD_FILES_PATH)
                .file(goodFile1)
                .file(goodFile2))
                .andExpect(status().isOk())
                .andExpect(content().string("Files added successfully"));


        MockMultipartFile badFile1 = dummyBadMultipartFile();
        MockMultipartFile badFile2 = dummyBadMultipartFile();

        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_API_PATH + ADD_FILE_PATH)
                .file(badFile1)
                .file(badFile2))
                .andExpect(status().is4xxClientError());
    }

    public void testRetrieveFileById() throws Exception {
        FileData fileData = new FileData(TXT_FILE, FILE_CONTENT.getBytes());
        Future<FileData> fileDataFuture = new AsyncResult<>(fileData);
        when(repoService.getById(ID)).thenReturn(fileDataFuture);

        mockMvc.perform(get(BASE_API_PATH + RETRIEVE_BY_ID_PATH)
                .param(ID_PARAMETER, valueOf(ID)))
                .andExpect(status().isOk())
                .andExpect(content().bytes(FILE_CONTENT.getBytes()));

        mockMvc.perform(get(BASE_API_PATH + RETRIEVE_BY_ID_PATH)).andExpect(status().is4xxClientError());
    }

    public void testRetrieveFileByPrefix() throws Exception {
        List<String> fileNames = TestData.dummyFileNamesForPrefix();
        Future<Collection<String>> fileNamesFuture = new AsyncResult<>(fileNames);
        when(repoService.getByNameStartingWith(PREFIX)).thenReturn(fileNamesFuture);

        mockMvc.perform(get(BASE_API_PATH + RETRIEVE_BY_PREFIX_PATH)
                .param(PREFIX_PARAMETER, PREFIX))
                .andExpect(status().isOk())
                .andExpect(content().string(fileNames.toString()));

        mockMvc.perform(get(BASE_API_PATH + RETRIEVE_BY_PREFIX_PATH)).andExpect(status().is4xxClientError());
    }

    public void testRetrieveAll() throws Exception {
        List<String> fileNames = TestData.dummyFileNames();
        Future<Collection<String>> fileNamesFuture = new AsyncResult<>(fileNames);
        when(repoService.getAll()).thenReturn(fileNamesFuture);

        mockMvc.perform(get(BASE_API_PATH + RETRIEVE_ALL_PATH))
                .andExpect(status().isOk())
                .andExpect(content().string(fileNames.toString()));
    }

    public void testRenameFile() throws Exception {
        mockMvc.perform(put(BASE_API_PATH + FILE_RENAME_PATH)
                .param(ID_PARAMETER, valueOf(ID))
                .param(NAME_PARAMETER, TXT_FILE))
                .andExpect(status().isOk())
                .andExpect(content().string("File updated successfully"));

        mockMvc.perform(get(BASE_API_PATH + FILE_RENAME_PATH)).andExpect(status().is4xxClientError());
    }

    public void testReplaceFile() throws Exception {
        MockMultipartFile goodFile = dummyMultipartFile();
        MockMultipartFile badFile = dummyBadMultipartFile();

        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_API_PATH + FILE_REPLACE_PATH)
                .file(goodFile)
                .param(ID_PARAMETER, valueOf(ID)))
                .andExpect(status().isOk())
                .andExpect(content().string("File replaced successfully"));

        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_API_PATH + FILE_REPLACE_PATH)
                .file(badFile)
                .param(ID_PARAMETER, valueOf(ID)))
                .andExpect(status().is4xxClientError());
        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_API_PATH + FILE_REPLACE_PATH)
                .param(ID_PARAMETER, valueOf(ID)))
                .andExpect(status().is4xxClientError());
        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_API_PATH + FILE_REPLACE_PATH)
                .file(goodFile))
                .andExpect(status().is4xxClientError());
    }

    public void testRemoveFile() throws Exception {
        mockMvc.perform(delete(BASE_API_PATH + REMOVE_FILE_PATH)
                .param(ID_PARAMETER, valueOf(ID)))
                .andExpect(status().isOk())
                .andExpect(content().string("File removed successfully"));

        mockMvc.perform(delete(REMOVE_FILE_PATH)).andExpect(status().is4xxClientError());
    }

    public void testRemoveFiles() throws Exception {
        mockMvc.perform(delete(BASE_API_PATH + REMOVE_FILES_PATH)
                .param(IDS_PARAMETER, valueOf(IDS[0]))
                .param(IDS_PARAMETER, valueOf(IDS[1])))
                .andExpect(status().isOk())
                .andExpect(content().string("Files removed successfully"));

        mockMvc.perform(delete(BASE_API_PATH + REMOVE_FILES_PATH)).andExpect(status().is4xxClientError());
    }
}
