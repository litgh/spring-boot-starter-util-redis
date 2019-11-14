local getValue = redis.call('get', KEYS[1])
if getValue == false then
    return 1
end
if getValue ~= KEYS[2] then
    return 0
end
return redis.call('del', KEYS[1])