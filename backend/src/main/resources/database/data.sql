-- Provide SQL scripts here

insert into repair_orders (id, shop_id, external_RO, status, created_date, odometer_in, odometer_out, notes, created_at, modified_at)
values ('0a7fc60a-46cf-4238-9ae4-db68be4ef234', '21dd86fe-e974-4147-81b3-86ab7953d0b7', 'Mavis-123', 'ESTIMATE', now(), NULL, NULL, 'Customer requests checking of tires', now(), now()),
      ('d0ab853b-828a-434a-94d8-c60e177dd88f', '21dd86fe-e974-4147-81b3-86ab7953d0b7', 'Mavis-456', 'CLOSED', TIMESTAMP '2025-11-05 13:39:12', 4000, 4100, 'Tune up A/C', now(), now())
;

insert into repair_order_lines (id, repair_order_id, description, quantity, unit_price, created_at, modified_at)
VALUES ('b4be2a94-ca12-4aff-b2c3-b01a33e59b78', '0a7fc60a-46cf-4238-9ae4-db68be4ef234', 'check left front tire pressure', 1, 0, now(), now()),
        ('277eec8d-b5d2-4212-affc-4f2339c267dc','0a7fc60a-46cf-4238-9ae4-db68be4ef234', 'check alignment', 1, 57.99, now(), now()),
        ('f09021c8-508d-4a44-b78e-7873328943b8','0a7fc60a-46cf-4238-9ae4-db68be4ef234', 'check oil level', 1, 10, now(), now()),
        ('36b0bb7c-ae6d-47bc-9997-9c9d70cfc70e', 'd0ab853b-828a-434a-94d8-c60e177dd88f', 'replace coolant', 4, 24.6, now(), now()),
        ('0567727a-1f2e-4de7-82d6-3710259a048e', 'd0ab853b-828a-434a-94d8-c60e177dd88f', 'replace filter', 1, 42.87, now(), now()),
        ('7132f896-5fec-450a-9e05-54e0bb9ef1df', 'd0ab853b-828a-434a-94d8-c60e177dd88f', 'labor for a/c tune', 1, 150.49, now(), now()),
        ('f2d1ecf9-04d9-4289-aa32-64fc647f29ea', 'd0ab853b-828a-434a-94d8-c60e177dd88f', 'vacuum', 0, 0, now(), now())
        ;

