CREATE TABLE contact_messages (
                                  id BIGSERIAL PRIMARY KEY,
                                  full_name VARCHAR(200) NOT NULL,
                                  email VARCHAR(255) NOT NULL,
                                  phone_number VARCHAR(50) NOT NULL,
                                  message TEXT NOT NULL,
                                  created_at TIMESTAMP WITH TIME ZONE NOT NULL
);