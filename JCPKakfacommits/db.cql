


CREATE INDEX IF NOT EXISTS historic_data_ideal_node_item_id
ON yantriks.historic_data_ideal_node (item_id);

CREATE INDEX IF NOT EXISTS historic_data_ideal_node_order_created_date
ON yantriks.historic_data_ideal_node (order_created_date);