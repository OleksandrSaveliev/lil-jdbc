package com.my.lil_jdbc;

import com.my.lil_jdbc.dao.CustomerDAO;
import com.my.lil_jdbc.dao.OrderDAO;
import com.my.lil_jdbc.model.Customer;
import com.my.lil_jdbc.model.Order;

import java.sql.SQLException;


public class LilJdbcApplication {
    public static void main(String[] args) {
        DatabaseConnectionManager dbcm = new DatabaseConnectionManager(
                "localhost", "hplussport", "postgres", "password"
        );

        try {
            var connection = dbcm.getConnection();

            CustomerDAO customerDAO = new CustomerDAO(connection);
            Customer janeDoe = new Customer();
            janeDoe.setFirstName("Jane");
            janeDoe.setLastName("Doe");
            janeDoe.setEmail("janedoe@mail.com");
            janeDoe.setPhone("123-456-7890");
            janeDoe.setAddress("123 Main St");
            janeDoe.setState("NY");
            janeDoe.setCity("New York");
            janeDoe.setZipcode("10001");

            Customer res = customerDAO.create(janeDoe);
            System.out.println(res + "\n" + "CREATED");
            res.setFirstName("Luke");

            Customer res1 = customerDAO.update(res);
            System.out.println(res1 + "\n" + "UPDATED");

            customerDAO.delete(res1.getId());
            System.out.println(customerDAO.findById(res1.getId()) + "\n" + "DELETED");

            OrderDAO orderDAO = new OrderDAO(connection);
            Order order = orderDAO.findById(1000);
            System.out.println(order + "\n" + "ORDER");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
