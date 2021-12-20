CREATE TABLE IF NOT EXISTS Messages (
    receiver varchar,
    content varchar,
    timestamp timestamp,
    SKsender varchar,
    SKreceiver varchar

)

CREATE TABLE IF NOT EXISTS Users (
    username varchar,
    password varchar,
    publickey varchar
)