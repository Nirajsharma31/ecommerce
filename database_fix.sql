-- Fix the image_data column to support larger images
-- Run this SQL script in your MySQL database

USE ecommerceai;

-- Check current column type
DESCRIBE products;

-- Alter the image_data column to LONGBLOB to support larger images
ALTER TABLE products MODIFY COLUMN image_data LONGBLOB;

-- Verify the change
DESCRIBE products;