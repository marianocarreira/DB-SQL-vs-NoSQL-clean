SELECT OwnerUserId, MAX(Score) AS MaxScore 
FROM Posts 
GROUP BY OwnerUserId;