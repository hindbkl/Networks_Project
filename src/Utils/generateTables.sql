CREATE TABLE if not exists messages (
    sender varchar,         -- its username
    receiver varchar,       -- its username
    content varchar,
    timestamp timestamp,
    SKsender varchar,       -- symmetric key encrypted with sender public key
    SKreceiver varchar      -- symmetric key encrypted with receiver public key

);

CREATE TABLE if not exists users (
    username varchar,
    password varchar,
    publickey varchar,
    connected boolean
)