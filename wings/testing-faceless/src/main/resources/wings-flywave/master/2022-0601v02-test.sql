-- @plain error@skip
REPLACE INTO `tst_sharding`(`id`, `commit_id`, `login_info`, `other_info`)
VALUES (100, -1, 'LOGIN_INFO-00', 'OTHER_INFO-00'),
       (101, -1, 'LOGIN_INFO-01', 'OTHER_INFO-01'),
       (102, -1, 'LOGIN_INFO-02', 'OTHER_INFO-02'),
       (103, -1, 'LOGIN_INFO-03', 'OTHER_INFO-03'),
       (104, -1, 'LOGIN_INFO-04', 'OTHER_INFO-04'),
       (105, -1, 'LOGIN_INFO-05', 'OTHER_INFO-05'),
       (106, -1, 'LOGIN_INFO-06', 'OTHER_INFO-06'),
       (107, -1, 'LOGIN_INFO-07', 'OTHER_INFO-07'),
       (108, -1, 'LOGIN_INFO-08', 'OTHER_INFO-08'),
       (109, -1, 'LOGIN_INFO-09', 'OTHER_INFO-09'),
       (110, -1, 'LOGIN_INFO-10', 'OTHER_INFO-10'),
       (111, -1, 'LOGIN_INFO-11', 'OTHER_INFO-11'),
       (112, -1, 'LOGIN_INFO-12', 'OTHER_INFO-12'),
       (113, -1, 'LOGIN_INFO-13', 'OTHER_INFO-13'),
       (114, -1, 'LOGIN_INFO-14', 'OTHER_INFO-14'),
       (115, -1, 'LOGIN_INFO-15', 'OTHER_INFO-15'),
       (116, -1, 'LOGIN_INFO-16', 'OTHER_INFO-16'),
       (117, -1, 'LOGIN_INFO-17', 'OTHER_INFO-17'),
       (118, -1, 'LOGIN_INFO-18', 'OTHER_INFO-18'),
       (119, -1, 'LOGIN_INFO-19', 'OTHER_INFO-19');

-- CALL FLYWAVE('2022-0601v02-test.sql');