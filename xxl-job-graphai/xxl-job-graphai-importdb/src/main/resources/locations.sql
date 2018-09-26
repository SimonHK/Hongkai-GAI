SELECT CONCAT(  
  "*6\r\n",  
  '$', LENGTH(redis_cmd), '\r\n',redis_cmd, '\r\n','$', LENGTH(redis_key), '\r\n',redis_key, '\r\n',
  '$', LENGTH(hkey1), '\r\n',  hkey1, '\r\n','$', LENGTH(hval1), '\r\n',  hval1, '\r\n',
  '$', LENGTH(hkey2), '\r\n',  hkey2, '\r\n','$', LENGTH(hval2), '\r\n',  hval2, '\r'
)  
FROM ( 
SELECT 
'HMSET' as redis_cmd,
 locationname AS redis_key,
 'location' AS hkey1, locationid AS hval1,
 'realname' AS hkey2,realname AS hval2
from locations2 order by locationid
) AS t 