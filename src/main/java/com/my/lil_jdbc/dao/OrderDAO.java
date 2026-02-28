package com.my.lil_jdbc;

import com.my.lil_jdbc.util.DataAccessObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDAO extends DataAccessObject<Order> {
    private static final String FIND_BY_ID =
            "SELECT " +
                    "c.first_name AS customer_first_name, " +
                    "c.last_name AS customer_last_name, " +
                    "c.email AS customer_email, " +
                    "o.order_id, " +
                    "o.creation_date, " +
                    "o.total_due, " +
                    "o.status, " +
                    "s.first_name AS salesperson_first_name, " +
                    "s.last_name AS salesperson_last_name, " +
                    "s.email AS salesperson_email, " +
                    "ol.quantity, " +
                    "p.name AS product_name, " +
                    "p.size AS product_size, " +
                    "p.variety AS product_variety, " +
                    "p.price AS product_price " +
                    "FROM orders o " +
                    "JOIN customer c ON o.customer_id = c.customer_id " +
                    "JOIN salesperson s ON o.salesperson_id = s.salesperson_id " +
                    "JOIN order_item ol ON ol.order_id = o.order_id " +
                    "JOIN product p ON ol.product_id = p.product_id " +
                    "WHERE o.order_id = ?";

    public OrderDAO(Connection connection) {
        super(connection);
    }

    @Override
    public Order findById(long id) {

        Order order = null;
        List<OrderLine> orderLines = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID)) {

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                // Create Order only once
                if (order == null) {
                    order = new Order();
                    order.setId(rs.getLong("order_id"));
                    order.setCustomerFirstName(rs.getString("customer_first_name"));
                    order.setCustomerLastName(rs.getString("customer_last_name"));
                    order.setCustomerEmail(rs.getString("customer_email"));

                    java.sql.Date sqlDate = rs.getDate("creation_date");
                    if (sqlDate != null) {
                        order.setCreationDate(new Date(sqlDate.getTime()));
                    }

                    order.setTotalDue(rs.getBigDecimal("total_due"));
                    order.setStatus(rs.getString("status"));

                    order.setSalespersonFirstName(rs.getString("salesperson_first_name"));
                    order.setSalespersonLastName(rs.getString("salesperson_last_name"));
                    order.setSalespersonEmail(rs.getString("salesperson_email"));
                }

                // Create OrderLine for every row
                OrderLine orderLine = new OrderLine();
                orderLine.setQuantity(rs.getInt("quantity"));
                orderLine.setProductName(rs.getString("product_name"));
                orderLine.setProductSize(rs.getInt("product_size"));
                orderLine.setProductVariety(rs.getString("product_variety"));
                orderLine.setProductPrice(rs.getBigDecimal("product_price"));

                orderLines.add(orderLine);
            }


            if (order != null) {
                order.setOrderLines(orderLines);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching order with id " + id, e);
        }

        return order;
    }

    @Override
    public List<Order> findAll() {
        return List.of();
    }

    @Override
    public Order update(Order dto) {
        return null;
    }

    @Override
    public Order create(Order dto) {
        return null;
    }

    @Override
    public void delete(long id) {

    }

    @Override
    protected int getLastVal(String sequence) {
        return super.getLastVal(sequence);
    }

    public List<Order> getOrdersForCustomer(long customerId){
        List<Order> orders = new ArrayList<>();
        try(PreparedStatement statement = this.connection.prepareStatement(GET_FOR_CUST);){
            statement.setLong(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            long orderId = 0;
            Order order = null;
            while(resultSet.next()){
                long localOrderId = resultSet.getLong(4);
                if(orderId!=localOrderId){
                    order = new Order();
                    orders.add(order);
                    order.setId(localOrderId);
                    orderId = localOrderId;
                    order.setCustomerFirstName(resultSet.getString(1));
                    order.setCustomerLastName(resultSet.getString(2));
                    order.setCustomerEmail(resultSet.getString(3));
                    order.setCreationDate(new Date(resultSet.getDate(5).getTime()));
                    order.setTotalDue(resultSet.getBigDecimal(6));
                    order.setStatus(resultSet.getString(7));
                    order.setSalespersonFirstName(resultSet.getString(8));
                    order.setSalespersonLastName(resultSet.getString(9));
                    order.setSalespersonEmail(resultSet.getString(10));
                    List<OrderLine> orderLines = new ArrayList<>();
                    order.setOrderLines(orderLines);
                }
                OrderLine orderLine = new OrderLine();
                orderLine.setQuantity(resultSet.getInt(11));
                orderLine.setProductCode(resultSet.getString(12));
                orderLine.setProductName(resultSet.getString(13));
                orderLine.setProductSize(resultSet.getInt(14));
                orderLine.setProductVariety(resultSet.getString(15));
                orderLine.setProductPrice(resultSet.getBigDecimal(16));
                order.getOrderLines().add(orderLine);
            }
        }catch(SQLException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return orders;
    }

}



