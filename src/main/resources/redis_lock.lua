local result = redis.call('SET', KEYS[1], ARGV[1], 'PX', ARGV[2], 'NX')
if result == false then
    return 0
elseif type(result) == 'table' and result['ok'] == 'OK' then
    return 1
end
return 0