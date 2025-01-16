DELETE FROM exercise;

INSERT INTO exercise (name, instructions, video_link, category, share_type, language)
VALUES
('Pushups', 'Do 10 pushups', 'https://www.youtube.com/watch?v=IODxDxX7oi4', 'ARMS', 'PUBLIC', 'ENGLISH'),
('Bent Over Dumbbell Row', 'ABCD', 'https://www.youtube.com/watch?v=DhewkuU_95s', 'BACK', 'PUBLIC', 'ENGLISH'),
('Dumbbell Bench Press', 'ABCD', 'https://www.youtube.com/watch?v=dGqI0Z5ul4k', 'CHEST', 'PUBLIC', 'ENGLISH'),
('Lunges', 'Do 10 lunges', 'https://www.youtube.com/watch?v=QOVaHwm-Q6U', 'LEGS', 'PUBLIC', 'ENGLISH'),

('Pompes', 'Faites 10 pompes', 'https://www.youtube.com/watch?v=IODxDxX7oi4', 'ARMS', 'PUBLIC', 'FRENCH'),
('Rangée avec haltères', 'ABCD', 'https://www.youtube.com/watch?v=DhewkuU_95s', 'BACK', 'PUBLIC', 'FRENCH'),
('Développé couché avec haltères', 'ABCD', 'https://www.youtube.com/watch?v=dGqI0Z5ul4k', 'CHEST', 'PUBLIC', 'FRENCH'),
('Fentes', 'Faites 10 fentes', 'https://www.youtube.com/watch?v=QOVaHwm-Q6U', 'LEGS', 'PUBLIC', 'FRENCH'),

('Отжимания', 'Сделайте 10 отжиманий', 'https://www.youtube.com/watch?v=IODxDxX7oi4', 'ARMS', 'PUBLIC', 'RUSSIAN'),
('Тяга гантелей в наклоне', 'ABCD', 'https://www.youtube.com/watch?v=DhewkuU_95s', 'BACK', 'PUBLIC', 'RUSSIAN'),
('Жим гантелей лежа', 'ABCD', 'https://www.youtube.com/watch?v=dGqI0Z5ul4k', 'CHEST', 'PUBLIC', 'RUSSIAN'),
('Выпады', 'Сделайте 10 выпадов', 'https://www.youtube.com/watch?v=QOVaHwm-Q6U', 'LEGS', 'PUBLIC', 'RUSSIAN');


INSERT INTO routine (name, frequency, share_type, language) VALUES
('Routine 1', 'DAILY', 'PRIVATE', 'ENGLISH'),
('Routine 2', 'WEEKLY', 'PRIVATE', 'ENGLISH'),
('Routine 3', 'MONTHLY', 'PRIVATE', 'ENGLISH');

INSERT INTO routine_exercise (routine_id, exercise_id) VALUES
(1, 1),
(1, 2),
(1, 3),

(2, 1),
(2, 2),
(2, 3);

INSERT INTO routine_exercise_set (weight, reps, routine_exercise_id) VALUES
(10, 10, 1),
(15, 10, 1),
(20, 10, 1),

(10, 15, 2),
(15, 20, 2),
(20, 25, 2),

(10, 30, 3),
(15, 35, 3),
(20, 40, 3);
