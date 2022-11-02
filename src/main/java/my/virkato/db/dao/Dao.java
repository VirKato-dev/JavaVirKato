package my.virkato.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public abstract class Dao<T> {
    public abstract void createTable() throws SQLException;
    public abstract void create(T row);
    public abstract T get(long id);
    public abstract List<T> getAll();
    public abstract void update(T row);
    public abstract void delete(long id);
}
