ALTER TABLE card_sets ADD COLUMN user_id UUID REFERENCES users(id);
CREATE INDEX idx_card_sets_user_id ON card_sets (user_id);
