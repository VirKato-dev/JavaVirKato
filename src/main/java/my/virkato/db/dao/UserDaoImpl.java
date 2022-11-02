package my.virkato.db.dao;

import my.virkato.db.ConnectionUtils;
import my.virkato.db.model.User;

import javax.sql.RowSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UserDaoImpl extends Dao<User> {

    private final Connection conn = ConnectionUtils
            .getMySqlConnection("localhost", "first", "root", "root");

    /***
     * Пересоздать таблицу
     */
    @Override
    public void createTable() {
        try {
            // требуется транзакция
            conn.setAutoCommit(false);
            conn.beginRequest();
            Statement st = conn.createStatement();
//            // сначала удалим старый вариант таблицы
//            st.execute("DROP TABLE IF EXISTS first.users;");
            // создадим новый вариант таблицы
            st.execute("CREATE TABLE IF NOT EXISTS first.users (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT, " +
                    "  `login` VARCHAR(45) NOT NULL, " +
                    "  `password` VARCHAR(45) NOT NULL, " +
                    "  `name` VARCHAR(45), " +
                    "  `age` INT, " +
                    "  PRIMARY KEY (`id`)) " +
                    "ENGINE = MyISAM " +
                    "DEFAULT CHARACTER SET = utf8 " +
                    "COLLATE = utf8_unicode_ci;");
            newVersion(st);
            conn.endRequest();
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

    private static void newVersion(Statement st) throws SQLException {
        st.execute("ALTER TABLE users " +
                "ADD UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE, " +
                "ADD UNIQUE INDEX `login_UNIQUE` (`login` ASC) VISIBLE;");
    }

    /***
     * Создать пользователя
     * @param row данные о пользователе
     */
    @Override
    public void create(User row) {
        try {
            // требуется транзакция
            conn.setAutoCommit(false);
            PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO users (login, password, name, age) VALUES (?, ?, ?, ?);");
            st.setString(1, row.getLogin());
            st.setString(2, row.getPassword());
            st.setString(3, row.getName());
            st.setInt(4, row.getAge());
            st.execute();
            System.out.println("Добавлен: " + row);
        } catch (SQLException e) {
            try {
                System.out.println("Запрещено дублирование ID и LOGIN");
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        }
    }

    /***
     * Получить данные о пользователе по его ID
     * @param id ID пользователя
     * @return User - пользователь
     */
    @Override
    public User get(long id) {
        try {
            Statement st = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            st.execute("SELECT * FROM users WHERE id=" + id);
            ResultSet rs = st.getResultSet();
            if (rs.next()) {
                User user = new User(rs.getString(2), rs.getString(3), rs.getString(4), (byte) rs.getInt(5));
                user.setId(rs.getInt(1));
                return user;
            } else {
                System.out.println("Нет пользователя с ID = " + id);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * Получить список всех пользователей
     * @return список пользователей
     */
    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        try {
            Statement st = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            st.execute("SELECT * FROM users");
            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                User user = new User(rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        (byte) rs.getInt(5));
                user.setId(rs.getInt(1));
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * Изменить данные о пользователе
     * @param row все данные, включая ID
     */
    @Override
    public void update(User row) {
        try {
            // требуется транзакция
            conn.setAutoCommit(false);
            PreparedStatement st = conn.prepareStatement(
                    "UPDATE users SET login=?, password=?, name=?, age=? WHERE id=?;");
            st.setString(1, row.getLogin());
            st.setString(2, row.getPassword());
            st.setString(3, row.getName());
            st.setInt(4, row.getAge());
            st.setLong(5, row.getId());
            st.execute();
            System.out.println("Изменён: " + row);
        } catch (SQLException e) {
            try {
                System.out.println("Запрещено дублирование LOGIN");
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        }
    }

    /***
     * Удалить пользователя
     * @param id ID пользователя
     */
    @Override
    public void delete(long id) {
        try {
            // требуется транзакция
            conn.setAutoCommit(false);
            Statement st = conn.createStatement();
            st.execute("DELETE FROM users WHERE id=" + id);
            System.out.println("Удалено " + st.getUpdateCount() + " строк");
        } catch (SQLException e) {
            try {
                System.out.println("Что-то пошло не так");
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException ignored) {
                }
            }
        }
    }
}
