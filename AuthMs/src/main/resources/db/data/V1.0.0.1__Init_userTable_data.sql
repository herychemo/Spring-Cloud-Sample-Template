
/*	BASE ROWS	*/

INSERT INTO users
(user_id, active, email, username, name, last_name, password)
VALUES ('01e3d8d5-1119-4111-b3d0-be6562ca5913', true, 'admin@admin.com',
        'admin', 'Some User', 'Admin Admin',

         -- https://www.browserling.com/tools/bcrypt
         /*Hashed with BCCrypt 8 Rounds: some1234password*/
        '$2a$08$.SSWQCEIk1UaWsZhBisWdO/8Qvix8GT/aW9XHkNS6JEJS/pnAXOxO');

INSERT INTO user_role VALUES ('01e3d8d5-1119-4111-b3d0-be6562ca5913', 1);
INSERT INTO user_role VALUES ('01e3d8d5-1119-4111-b3d0-be6562ca5913', 2);
