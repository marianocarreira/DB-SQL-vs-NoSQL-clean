SELECT p.Title, u.DisplayName
FROM Posts p
INNER JOIN Users u ON p.OwnerUserId = u.Id
WHERE u.id = 11