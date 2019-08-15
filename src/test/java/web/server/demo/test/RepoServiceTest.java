package web.server.demo.test;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import web.server.demo.data.TestData;
import web.server.demo.repository.FileDataRepository;
import web.server.demo.service.RepoService;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static web.server.demo.data.TestData.*;


public class RepoServiceTest {

    @Mock
    private FileDataRepository fileDataRepository;

    @InjectMocks
    private RepoService repoService;

    private MockMultipartFile mockMultipartFile = dummyMultipartFile();
    private MockMultipartFile[] mockMultipartFiles = dummyMultipartFiles();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(fileDataRepository.findById(ID)).thenReturn(Optional.of(TestData.dummyFileData()));
        when(fileDataRepository.findByNameStartingWith(PREFIX)).thenReturn(TestData.dummyFileDataCollectionForPrefix());
        when(fileDataRepository.findAll()).thenReturn(TestData.dummyFileDataCollection());
    }

    @Test
    public void testSaveMultipartFile() throws IOException {
        assertTrue(repoService.save(mockMultipartFile));
    }

    @Test
    public void testSaveMultipartFiles() throws InterruptedException {
        assertTrue(repoService.save(mockMultipartFiles));
    }

    @Test
    public void testGetFileDataById() throws ExecutionException, InterruptedException {
        assertEquals(repoService.getById(ID).get(), TestData.dummyFileData());
    }

    @Test
    public void testGetFileNamesByPrefix() throws ExecutionException, InterruptedException {
        assertEquals(repoService.getByNameStartingWith(PREFIX).get(), TestData.dummyFileNamesForPrefix());
    }

    @Test
    public void testGetAllFileNames() throws ExecutionException, InterruptedException {
        assertEquals(repoService.getAll().get(), TestData.dummyFileNames());
    }

    @Test
    public void testRenameFile() {
        assertTrue(repoService.rename(ID, "newName"));
    }

    @Test
    public void testReplaceFile() throws IOException {
        assertTrue(repoService.replace(ID, dummyMultipartFile()));
    }

    @Test
    public void testDeleteById() {
        assertTrue(repoService.deleteById(ID));
    }

    @Test
    public void testDeleteAllByIds() {
        assertTrue(repoService.deleteAllByIds(IDS));
    }
}
