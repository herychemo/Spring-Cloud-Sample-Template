
INSERT INTO oauth_client_details
(client_id, resource_ids, client_secret, scope, authorized_grant_types,
 web_server_redirect_uri, authorities, access_token_validity,
 refresh_token_validity, additional_information, autoapprove)
VALUES (
        'default_oauth2_client_id',

        'auth-ms-resource-id'
          || ',accounts-ms-resource-id'
          || ',spring-admin-resource-id'
          || ',hystrix-dashboard-resource-id'
        ,

         -- https://www.browserling.com/tools/bcrypt
         /*Hashed with BCCrypt 4 Rounds: QWERTY74gray75raccoon634*/
        '$2a$04$qWE9dU8ZeFa3SoyZcobydeNvlgE70olQEOGvkgBEciwnNUR3Rc82q',
        'read,write,user_info', 'password,authorization_code,refresh_token,client_credentials',

        'http://localhost:8080/login/oauth2/code/custom-client' --  API Gateway UI
            || ',http://swarm-master0:8080/login/oauth2/code/custom-client'
        ,

        'ROLE_ACTUATOR', 36000, 360000, null, true);
/*
You can add more redirect uri using comma
*/
