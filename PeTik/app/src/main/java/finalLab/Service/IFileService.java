package finalLab.Service;

import java.util.List;

public interface IFileService<T> {
    boolean save(T data);

    List<T> loadAll();

    T findById(String id);

    boolean update(String id, T newData);

    boolean delete(String id);
}
