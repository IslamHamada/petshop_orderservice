create table order_item(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT,
    count INT,
    order_id BIGINT,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id)
);