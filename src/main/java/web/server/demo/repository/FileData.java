package web.server.demo.repository;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

@Entity
public class FileData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    @Lob
    private byte[] data;

    public FileData() {}

    public FileData(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, data);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof FileData) {
            FileData other = (FileData) object;
            return id == other.id && name.equals(other.name) && Arrays.equals(data, other.data);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "FileData [id=" + getId() + ", name=" + getName() + ", data=" + Arrays.toString(getData()) + "]";
    }
}
