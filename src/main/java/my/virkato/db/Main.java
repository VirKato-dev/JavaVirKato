package my.virkato.db;

import my.virkato.db.dao.Dao;
import my.virkato.db.dao.UserDaoImpl;
import my.virkato.db.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;


public class Main {
    public static void main(String[] args) throws SQLException {
        System.out.println("Hello world!");
        System.out.println(ConnectionUtils.welcome());
        Connection connection = ConnectionUtils.getMySqlConnection("localhost", "first", "root", "root");
        System.out.println("Connected to :" + connection.getMetaData().getDatabaseProductName());

        test();
    }

    private static void test() {
        Dao<User> dao = new UserDaoImpl();
        try {
            dao.createTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("\nСоздать/добавить пользователя");
        dao.create(new User("user" + new Random().nextInt(99), "pass", "User", (byte) 10));

        System.out.println("\nПолучить пользователя по ID");
        System.out.println(dao.get(1));

        System.out.println("\nПолучить всех пользователей");
        dao.getAll().forEach(System.out::println);

        System.out.println("\nИзменить пользователя");
        User user = dao.get(6);
        if (user != null) {
            user.setName("USER");
            dao.update(user);
        }

        System.out.println("\nУдалить пользователя");
        dao.delete(5);
    }

}