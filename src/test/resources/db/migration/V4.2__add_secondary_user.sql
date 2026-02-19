INSERT INTO users (id, external_id)
VALUES ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'test-user-secondary');

INSERT INTO card_sets (id, user_id, name, icon)
VALUES ('bbbbbbbb-1111-1111-1111-111111111111', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Spanish', 'flag');

INSERT INTO cards (id, card_set_id, front_text, back_text)
VALUES ('bbbbbbbb-2222-2222-2222-222222222222', 'bbbbbbbb-1111-1111-1111-111111111111', 'hello', 'hola');
