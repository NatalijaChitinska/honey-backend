-- Create categories table
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE,
    description VARCHAR(2000)
);

CREATE UNIQUE INDEX uq_categories_name ON categories(name);
CREATE INDEX idx_categories_slug ON categories(slug);

-- Create products table
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    price NUMERIC(10, 2) NOT NULL,
    stock INTEGER NOT NULL,
    category_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id)
            REFERENCES categories(id)
            ON DELETE RESTRICT
);

CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_active ON products(active);
