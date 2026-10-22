UPDATE    POSTS
SET         lastEditorUserId = 11
WHERE    lastEditorUserId IS NULL
   AND     ownerUserId = 11
   AND     id < 0