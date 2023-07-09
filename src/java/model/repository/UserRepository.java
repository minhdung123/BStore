package model.repository;

import model.entity.Cart;
import model.entity.User;
import ultis.DBHelper.query.QueryHelper;
import ultis.DBHelper.repository.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class UserRepository extends Repository<User> {
    private static UserRepository userRepository;
    private static CartRepository cartRepository;

    public static UserRepository getInstance() {
        if (userRepository == null) {
            userRepository = new UserRepository();

        }
        return userRepository;
    }

    private UserRepository() {
        table("user");
        fillable("username", "address", "password", "email");
        this.init();
    }

    @Override
    public User mapper(ResultSet rs) throws SQLException {
        return User.builder().username(rs.getString("username"))
                .user_id(rs.getInt("user_id"))
                .address(rs.getString("address"))
                .email(rs.getString("email"))
                .build();
    }

    public User get(int id) {
        String sql = userRepository.queryHelper.select("*").where().condition("user_id = " + id).endCondition().build();
        var records = queryExecutor.records(sql, this);
        return records.map(users -> users.get(0)).orElse(null);
    }

    public User getByEmail(String email) {

        String sql = userRepository.queryHelper
                .select("*")
                .where()
                .condition(String.format("email LIKE '%s'", email))
                .endCondition()
                .build();
        var records = queryExecutor.records(sql, this);
        return records.map(users -> users.get(0)).orElse(null);
    }

    public ArrayList<User> getByEmailAndPassword(String email, String password) {
        String sql = userRepository.queryHelper.select("*")
                .where()
                .condition(String.format("email LIKE '%s'", email))
                .condition("password =" + password)
                .endCondition()
                .build();
        var records = queryExecutor.records(sql, this);
        return records.orElse(new ArrayList<>());
    }

    @Override
    public User save(User data) {
        cartRepository = CartRepository.getInstance();
        User user = super.save(data);
        Cart cart = Cart.builder()
                .user_id((int) data.getUser_id())
                .build();
        cartRepository.save(cart);
        return user;
    }
}
