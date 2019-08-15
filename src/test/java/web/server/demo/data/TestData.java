package web.server.demo.data;

import org.springframework.mock.web.MockMultipartFile;
import web.server.demo.repository.FileData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static web.server.demo.client.Constants.FILE_PARAMETER;


public class TestData {
    public static final String TXT_FILE = "multipartFile.txt";
    private static final String TEXT_PLAIN = "text/plain";
    public static final String FILE_CONTENT = "multipartFile";
    private static final String DUMMY_PARAMETER_NAME = "dummyParameterName";
    public static final String PREFIX = "pre";
    public static final long ID = 0;
    public static final long[] IDS = {1, 2, 3};

    public static FileData dummyFileData() {
        String name = "dummyName";
        byte[] data = name.getBytes();
        return new FileData(name, data);
    }

    public static MockMultipartFile dummyMultipartFile() {
        return new MockMultipartFile(FILE_PARAMETER, TXT_FILE, TEXT_PLAIN, FILE_CONTENT.getBytes());
    }

    public static MockMultipartFile dummyBadMultipartFile() {
        return new MockMultipartFile(DUMMY_PARAMETER_NAME, TXT_FILE, TEXT_PLAIN, FILE_CONTENT.getBytes());
    }

    public static MockMultipartFile[] dummyMultipartFiles() {
        List<MockMultipartFile> mockMultipartFiles = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            mockMultipartFiles.add(new MockMultipartFile(FILE_PARAMETER, getOriginalFileName(i + 1), TEXT_PLAIN, FILE_CONTENT.getBytes()));
        }
        return mockMultipartFiles.toArray(new MockMultipartFile[0]);
    }

    private static String getOriginalFileName(int number) {
        return TXT_FILE.substring(0, TXT_FILE.indexOf(".")) + number + TXT_FILE.substring(TXT_FILE.indexOf("."));
    }

    public static List<String> dummyFileNames() {
        return Arrays.asList("foo", "bar", "dummy");
    }

    public static List<String> dummyFileNamesForPrefix() {
        return Arrays.asList("preserve.pdf", "premium.pdf", "press.pdf");
    }

    public static Collection<FileData> dummyFileDataCollection() {
        List<FileData> fileData = new ArrayList<>();
        for (int i = 0; i < dummyFileNames().size(); i++) {
            fileData.add(new FileData(dummyFileNames().get(i), dummyFileNames().get(i).getBytes()));
        }
        return fileData;
    }

    public static Collection<FileData> dummyFileDataCollectionForPrefix() {
        List<FileData> fileData = new ArrayList<>();
        for (int i = 0; i < dummyFileNamesForPrefix().size(); i++) {
            fileData.add(new FileData(dummyFileNamesForPrefix().get(i), dummyFileNamesForPrefix().get(i).getBytes()));
        }
        return fileData;
    }
}
