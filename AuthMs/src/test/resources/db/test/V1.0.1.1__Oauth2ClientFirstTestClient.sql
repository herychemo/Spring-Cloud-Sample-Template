
INSERT INTO oauth_client_details
(client_id, resource_ids, client_secret, scope, authorized_grant_types,
 web_server_redirect_uri, authorities, access_token_validity,
 refresh_token_validity, additional_information, autoapprove)
VALUES (
        'test-client-id',
        'test-resource-id',

         /*Hashed with BCCrypt 4 Rounds: test-client-secret*/
        '$2a$04$uc0oRwRXu0uO.JRHUbbxf.qhw5Knx1vhjZ9MdngSFPM.V7DK/jooG',
        'read,write,user_info', 'password,authorization_code,refresh_token,client_credentials',

        '' ,'ROLE_ACTUATOR', 36000, 360000, null, true);
/*
You can add more redirect uri using comma
*/
