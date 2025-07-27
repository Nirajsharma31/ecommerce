-- Fix the image_data column to support larger images
-- Run this SQL script in your MySQL database

USE ecommerceai;

-- Check current column type
DESCRIBE products;

-- Alter the image_data column to LONGBLOB to support larger images
ALTER TABLE products MODIFY COLUMN image_data LONGBLOB;

-- Also ensure other image columns exist
ALTER TABLE products ADD COLUMN IF NOT EXISTS image_name VARCHAR(255);
ALTER TABLE products ADD COLUMN IF NOT EXISTS image_type VARCHAR(100);

-- Verify the changes
DESCRIBE products;

-- Check if there are any constraint issues
SHOW CREATE TABLE products;