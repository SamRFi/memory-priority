# memory-priority

## Database Schemas:

```
CREATE TABLE users (
    username VARCHAR(255) PRIMARY KEY,
    hashed_password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

insert into users(username, hashed_password)
value ('root', 'root');

CREATE TABLE memory_sets (
    id INT AUTO_INCREMENT,
    name VARCHAR(255),
    priority_level ENUM('HIGH', 'MEDIUM', 'LOW'),
    last_time_rehearsed TIMESTAMP,
    username VARCHAR(255),
    FOREIGN KEY(username) REFERENCES users(username),
    PRIMARY KEY(id)
);

CREATE TABLE memory_set_entries (
    memory_set_id INT,
    key_name VARCHAR(255),
    value_name VARCHAR(255),
    FOREIGN KEY(memory_set_id) REFERENCES memory_sets(id),
    PRIMARY KEY(memory_set_id, key_name)
);
