-- Generate 1,000,000 rows of random data and insert into the "Posts" table
SET IDENTITY_INSERT Posts ON
DECLARE @counter INT = 1
DECLARE @maxRows INT = 500000

WHILE @counter <= @maxRows
BEGIN
   INSERT INTO Posts (
       Id,
       AcceptedAnswerId,
       AnswerCount,
       Body,
       ClosedDate,
       CommentCount,
       CommunityOwnedDate,
       CreationDate,
       FavoriteCount,
       LastActivityDate,
       LastEditDate,
       LastEditorDisplayName,
       LastEditorUserId,
       OwnerUserId,
       ParentId,
       PostTypeId,
       Score,
       Tags,
       Title,
       ViewCount
   )
   VALUES (
       -@counter,
       ABS(CHECKSUM(NEWID())) % 1000000 + 1, -- Random value between 1 and 1,000,000
       ABS(CHECKSUM(NEWID())) % 100,
       CAST(NEWID() AS NVARCHAR(MAX)),
       GETDATE(), -- ClosedDate can be null
       ABS(CHECKSUM(NEWID())) % 100,
       NULL, -- CommunityOwnedDate can be null
       GETDATE(), -- Current date
       ABS(CHECKSUM(NEWID())) % 100,
       GETDATE(), -- Current date
       GETDATE(), -- Current date
       LEFT(CONVERT(VARCHAR(255), NEWID()), 30), -- LastEditorDisplayName can be null
       null,
       11,
       null, -- Random value between 1 and 1,000,000
       1, -- Random value between 0 and 9
       ABS(CHECKSUM(NEWID())) % 100,
       CAST(NEWID() AS NVARCHAR(300)),
       CAST(NEWID() AS NVARCHAR(500)),
       ABS(CHECKSUM(NEWID())) % 1000000
   )

   SET @counter = @counter + 1
END
SET IDENTITY_INSERT Posts OFF