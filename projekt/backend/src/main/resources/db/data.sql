

INSERT INTO users (id, email, password_hash, created_at, updated_at) VALUES
                                                                         ('ea952ebe-c38b-43cf-af1a-0b2a70402c34', 'john.don@gmail.com', '$argon2id$v=19$m=16,t=2,p=1$RFpyc0N1MVprZDZVQ0d4Tw$UqgALGLHr60VgINlAm63aQ', NOW(), NOW()),
                                                                         ('413972c5-d918-49a2-a425-7fa5bb68ff3b', 'ben.schmidt@gmx.de', '$argon2id$v=19$m=16,t=2,p=1$Wk1TMFZtV1ZVd1pRVG9uZw$eXhY5Vn3K0lXU1Z3b3JqYQ', NOW(), NOW()),
                                                                         ('7f4e2d1a-8c3b-4f5e-9f6a-2b3c4d5e6f70', 'lisa.bach@yahoo.de', '$argon2id$v=19$m=16,t=2,p=1$T1ZsV1ZtV1ZVd1pRVG9uZw$Y2hpbGRyZW4xMjM0NTY3OA', NOW(), NOW());

INSERT INTO notes (note_id, title, md_content, is_private, created_at, updated_at, user_id) VALUES
                                                                                                ('60d17d7c-3fbb-428f-9d2b-b5aee81457e6', 'Grocery List', '**Milk, Eggs, Bread, Butter**', FALSE, NOW(), NOW(), 'ea952ebe-c38b-43cf-af1a-0b2a70402c34'),
                                                                                                ('a6267337-a192-4cc7-b60f-44eb8b150762', 'Project Ideas', '## Header Build a mobile app for task management', TRUE, NOW(), NOW(),'413972c5-d918-49a2-a425-7fa5bb68ff3b'),
                                                                                                ('b929b280-2684-40c1-a7e8-f03ef6a6b09e', 'Travel Plans', 'Visit *Paris* and *Rome* next summer', FALSE, NOW(), NOW(), '7f4e2d1a-8c3b-4f5e-9f6a-2b3c4d5e6f70'),
                                                                                                ('d68d419b-cefb-412d-ad16-7a32804364e6', 'Workout Routine', '- Monday: Chest\n- Tuesday: Back\n- Wednesday: Legs', TRUE, NOW(), NOW(), 'ea952ebe-c38b-43cf-af1a-0b2a70402c34'),
                                                                                                ('b17a1ad6-8c55-49cb-8718-d6fc7c4a4277', 'Book Recommendations', '1. The Great Gatsby\n2. 1984\n3. To Kill a Mockingbird', FALSE, NOW(), NOW(), '413972c5-d918-49a2-a425-7fa5bb68ff3b'),
                                                                                                ('c5f0cabf-092b-42a4-ae2b-077db13b7479', 'Meeting Notes', 'Discussed project timeline and deliverables', TRUE, NOW(), NOW(), '7f4e2d1a-8c3b-4f5e-9f6a-2b3c4d5e6f70');


INSERT INTO pw_reset_tokens (id, token, expires_at, used, user_id) VALUES
('23cd908e-f03e-4770-a7d5-0ff522d2cb88', 'reset-token-123', NOW() + INTERVAL '1 hour', FALSE, 'ea952ebe-c38b-43cf-af1a-0b2a70402c34');
