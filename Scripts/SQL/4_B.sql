SELECT p.Title, pt.[Type], u.DisplayName
FROM Posts p
INNER JOIN PostTypes pt
ON p.PostTypeId = pt.Id
INNER JOIN Users u ON p.OwnerUserId = u.Id
WHERE p.id > 20000000