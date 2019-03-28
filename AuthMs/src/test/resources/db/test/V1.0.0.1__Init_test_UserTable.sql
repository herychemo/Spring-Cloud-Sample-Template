
/*	BASE ROWS	*/

INSERT INTO users
(user_id, active, email, username, name, last_name, password)
VALUES ('01e3d8d5-1119-4111-b3d0-be6562ca5914', true, 'admin@admin.com',
        'admin', 'Some User', 'Admin Admin',

               -- https://www.browserling.com/tools/bcrypt
               /*Hashed with BCCrypt 8 Rounds: password*/
        '$2a$08$hrFZ0qYGUVyCvjrwMbPJ4ei7b7eQYQfUdGOP3r73LFE9xg/7epmp2'),
       ('01e3d8d5-1119-4111-b3d0-be6562ca5901', true, 'user@user.com',
        'user', 'Some User', 'User User',

               -- https://www.browserling.com/tools/bcrypt
               /*Hashed with BCCrypt 8 Rounds: password*/
        '$2a$08$hrFZ0qYGUVyCvjrwMbPJ4ei7b7eQYQfUdGOP3r73LFE9xg/7epmp2');

INSERT INTO user_role VALUES ('01e3d8d5-1119-4111-b3d0-be6562ca5914', 1);
INSERT INTO user_role VALUES ('01e3d8d5-1119-4111-b3d0-be6562ca5914', 2);

INSERT INTO user_role VALUES ('01e3d8d5-1119-4111-b3d0-be6562ca5901', 2);
