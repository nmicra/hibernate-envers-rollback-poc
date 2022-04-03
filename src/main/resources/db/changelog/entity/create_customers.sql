INSERT INTO public.customer(customer_id, created_date,updated_date,path,parent_customer_id)
VALUES('af13e37e-a461-11ec-b909-0242ac120002',current_timestamp,current_timestamp,'af13e37e-a461-11ec-b909-0242ac120002',null);
--VALUES(gen_random_uuid(),current_timestamp,current_timestamp,'a/b/c',null)

INSERT INTO public.customer(customer_id, created_date,updated_date,path,parent_customer_id)
VALUES('2d591e9c-a463-11ec-b909-0242ac120002',current_timestamp,current_timestamp,'af13e37e-a461-11ec-b909-0242ac120002/2d591e9c-a463-11ec-b909-0242ac120002','af13e37e-a461-11ec-b909-0242ac120002');
