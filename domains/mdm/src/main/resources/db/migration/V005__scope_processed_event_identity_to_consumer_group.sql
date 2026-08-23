ALTER TABLE processed_event
    DROP CONSTRAINT processed_event_pkey;

ALTER TABLE processed_event
    ADD CONSTRAINT processed_event_pkey PRIMARY KEY (event_id, consumer_group);
