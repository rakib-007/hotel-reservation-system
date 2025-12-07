-- ================================
-- Change Admin Password Script
-- ================================
-- Replace 'your_new_password' with your desired password
-- ================================

-- Update admin password
UPDATE users 
SET password = 'your_new_password' 
WHERE username = 'admin';

-- Verify the change
SELECT username, role, 
       CASE 
           WHEN password = 'your_new_password' THEN 'Password updated successfully'
           ELSE 'Password update failed'
       END AS status
FROM users 
WHERE username = 'admin';

