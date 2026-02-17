CREATE TABLE card_sets
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name       TEXT        NOT NULL,
    icon       TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE cards
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    card_set_id UUID        NOT NULL REFERENCES card_sets (id) ON DELETE CASCADE,
    front_text  TEXT        NOT NULL,
    back_text   TEXT        NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_cards_card_set_id ON cards (card_set_id);
