package finalLab.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseFileService<T> implements IFileService<T> {

    protected final String fileName;

    protected BaseFileService(String fileName) {
        this.fileName = fileName;
        initializeFile();
    }

    @Override
    public final boolean save(T data) {
        try (FileWriter fw = new FileWriter(fileName, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {

            out.println(convertToString(data));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public final List<T> loadAll() {
        List<T> items = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    T item = parseFromString(line);
                    if (item != null) {
                        items.add(item);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }

    @Override
    public final boolean update(String id, T newData) {
        List<T> allData = loadAll();
        boolean found = false;

        for (int i = 0; i < allData.size(); i++) {
            if (getEntityId(allData.get(i)).equals(id)) {
                allData.set(i, newData);
                found = true;
                break;
            }
        }

        if (found) {
            return rewriteFile(allData);
        }
        return false;
    }

    protected abstract T parseFromString(String line);

    protected abstract String convertToString(T data);

    protected abstract String getEntityId(T data);

    protected final boolean rewriteFile(List<T> allData) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (T data : allData) {
                pw.println(convertToString(data));
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void initializeFile() {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}