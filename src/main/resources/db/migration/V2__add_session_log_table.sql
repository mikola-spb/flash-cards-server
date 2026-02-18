CREATE TABLE session_log
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id   UUID        NOT NULL,
    card_id      UUID        NOT NULL REFERENCES cards (id) ON DELETE CASCADE,
    is_known     BOOLEAN     NOT NULL,
    displayed_at TIMESTAMPTZ NOT NULL,
    flipped_at   TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_session_log_card_id ON session_log (card_id);
